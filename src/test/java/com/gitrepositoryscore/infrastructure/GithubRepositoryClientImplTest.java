package com.gitrepositoryscore.infrastructure;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.http.Fault;
import com.gitrepositoryscore.domain.GithubRepository;
import com.gitrepositoryscore.domain.GithubRepositoryClient;
import com.gitrepositoryscore.domain.exceptions.ErrorCode;
import com.gitrepositoryscore.domain.exceptions.GithubRepositoryServiceException;
import com.gitrepositoryscore.generated.api.model.Error;
import com.gitrepositoryscore.generated.api.model.ErrorResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;

import java.util.stream.Stream;
import java.time.LocalDate;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
class GithubRepositoryClientImplTest {

  @TestConfiguration
  static class Config {
    @Bean
    public WebClient githubWebClient() {
      return new WebClientDelegate();
    }
  }

  @Autowired
  WebClient githubWebClient;

  @Autowired
  GithubRepositoryClient githubRepositoryClient;

  @Autowired
  ObjectMapper objectMapper;

  @Value("${wiremock.server.port}")
  String wireMockPort;

  @BeforeEach
  void setUp() {
    WebClient mockedWebClient = WebClient.builder()
      .baseUrl("http://localhost:" + wireMockPort)
      .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(20 * 1024 * 1024))
      .build();
    ((WebClientDelegate) githubWebClient).setDelegate(mockedWebClient);
  }

  @Test
  void getGithubPublicRepositories_WithSuccessResponse() {

    stubFor(get(urlEqualTo("/search/repositories?q=created:" + LocalDate.now() + "+language:java&per_page=100"))
            .willReturn(aResponse()
        .withHeader("Content-Type", "application/json")
        .withBody("""
                     {
                      "items": [{
                                   "full_name": "test",
                                   "created_at": "2014-08-09T16:45:18Z",
                                   "updated_at": "2024-07-14T07:43:29Z",
                                   "pushed_at": "2024-07-11T16:29:54Z",
                                   "stargazers_count": 88399,
                                   "forks": 26224
                               }]
                      }
                """)
        .withStatus(HttpStatus.OK.value())
      ));

    var githubPublicRepositories = githubRepositoryClient.getGithubPublicRepositories(LocalDate.now(), "java");
    System.out.println(LocalDate.now());
    StepVerifier.create(githubPublicRepositories)
      .expectNextMatches(githubRepository -> githubRepository.equals(new GithubRepository("test", "2024-07-11T16:29:54Z", "2014-08-09T16:45:18Z", 88399, 26224)))
      .verifyComplete();
  }

  @ParameterizedTest
  @MethodSource("errorCases")
  void getGithubPublicRepositories_ErrorCases(HttpStatus httpStatus, String errorMessage, ErrorCode errorCode)
    throws JsonProcessingException, JsonProcessingException {

    stubFor(get(urlEqualTo("/search/repositories?q=created:" + LocalDate.now() + "+language:java&per_page=100"))
      .willReturn(aResponse()
        .withStatus(httpStatus.value())
        .withHeader("Content-Type", "application/json")));

    var githubPublicRepositories = githubRepositoryClient.getGithubPublicRepositories(LocalDate.now(), "java");

    StepVerifier.create(githubPublicRepositories)
      .expectErrorSatisfies(e -> {
        assertThat(e).isInstanceOf(GithubRepositoryServiceException.class);
        assertThat(e.getMessage()).startsWith(errorMessage);
        assertThat(((GithubRepositoryServiceException) e).getErrorCode()).isEqualTo(errorCode);
      })
      .verify();
  }

  private static Stream<Arguments> errorCases() {
    return Stream.of(
      Arguments.arguments(
        HttpStatus.NOT_FOUND,
              "Error while retrieving repository data ",
              ErrorCode.GIT_HUB_OTHER_ERROR
      ),
      Arguments.arguments(
        HttpStatus.INTERNAL_SERVER_ERROR,
              "An internal server error occurred while retrieving repository data",
              ErrorCode.GIT_HUB_INTERNAL_SERVER_ERROR
      )
    );
  }

  @Test
  void getGithubPublicRepositories_ServiceUnreachable() {

    stubFor(get(urlEqualTo("/search/repositories?q=created:" + LocalDate.now() + "+language:java&per_page=100"))
      .willReturn(aResponse()
        .withFault(Fault.CONNECTION_RESET_BY_PEER))
    );

    var githubPublicRepositories = githubRepositoryClient.getGithubPublicRepositories(LocalDate.now(), "java");

    StepVerifier.create(githubPublicRepositories)
      .expectErrorSatisfies(e -> {
        assertThat(e).isInstanceOf(GithubRepositoryServiceException.class);
        assertThat(e.getMessage()).startsWith("Cannot reach http://localhost:");
        assertThat(((GithubRepositoryServiceException) e).getErrorCode()).isEqualTo(ErrorCode.GIT_HUB_SERVICE_UNREACHABLE);
      }).verify();
  }
}
