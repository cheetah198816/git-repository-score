package com.gitrepositoryscore.application.api;

import com.gitrepositoryscore.domain.GithubRepositoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.springSecurity;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GithubRepositoryApiImplTest {
  @Autowired
  ApplicationContext context;
  WebTestClient webTestClient;
  @MockBean
  private GithubRepositoryService githubRepositoryService;

  @BeforeEach
  public void setup() {
    this.webTestClient = WebTestClient
      .bindToApplicationContext(this.context)
      // add Spring Security test Support
      .apply(springSecurity())
      .configureClient()
      .build();
  }

  @Test
  @WithMockUser(username = "user", password = "user")
  void requestGetGitHubRepositoriesShouldReturn200() {

    when(githubRepositoryService.getGitHubRepositoriesWithScore(any(), any()))
      .thenReturn(Flux.empty());

    webTestClient
            .get()
      .uri("/v1/githubRepositories?createdAt=2010-01-01&language=java")
      .accept(MediaType.APPLICATION_JSON)
      .exchange()
      .expectStatus().isOk();

    verify(githubRepositoryService, times(1)).getGitHubRepositoriesWithScore(any(), any());
    verifyNoMoreInteractions(githubRepositoryService);
  }

  @Test
  @WithAnonymousUser
  void requestGetGitHubRepositoriesWithoutAuthorityShouldReturn401() {

    when(githubRepositoryService.getGitHubRepositoriesWithScore(any(), any()))
            .thenReturn(Flux.empty());

    webTestClient
            .get()
            .uri("/v1/githubRepositories?createdAt=2010-01-01&language=java")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isUnauthorized();
  }

  @Test
  @WithMockUser(username = "user", password = "user")
  void requestGetGitHubRepositoriesWithAuthorityShouldReturn400_WhenCreatedAtIsNull() {
    webTestClient
            .get()
            .uri("/v1/githubRepositories?language=java")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isBadRequest()
            .expectBody()
            .jsonPath("$.error.message").isEqualTo("Required query parameter 'createdAt' is not present.");
  }

  @Test
  @WithMockUser(username = "user", password = "user")
  void requestGetGitHubRepositoriesWithAuthorityShouldReturn400_WhenLanguageIsNull() {

    webTestClient
            .get()
            .uri("/v1/githubRepositories?createdAt=2011-01-01")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isBadRequest()
            .expectBody()
            .jsonPath("$.error.message").isEqualTo("Required query parameter 'language' is not present.");
  }


}
