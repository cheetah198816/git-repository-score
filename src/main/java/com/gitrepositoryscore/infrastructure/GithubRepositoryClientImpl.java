package com.gitrepositoryscore.infrastructure;

import com.gitrepositoryscore.domain.GithubRepository;
import com.gitrepositoryscore.domain.GithubRepositoryClient;
import com.gitrepositoryscore.domain.exceptions.ErrorCode;
import com.gitrepositoryscore.domain.exceptions.GithubRepositoryServiceException;
import com.gitrepositoryscore.infrastructure.model.GithubRepositoriesDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import reactor.core.publisher.Flux;

@Slf4j
@Component
@RequiredArgsConstructor
public class GithubRepositoryClientImpl implements GithubRepositoryClient {

  private final WebClient githubWebClient;

  private final GithubRepositoryMapper githubRepositoryMapper;

  @Override
  public Flux<GithubRepository> getGithubPublicRepositories(String createdAt, String language) {
    log.info("getGithubPublicRepositories({}, {})", createdAt, language);
    return githubWebClient.get()
            .uri("/search/repositories?q=created:"+ createdAt + "+language:" + language+ "&per_page=100")
            .exchangeToFlux(response -> {
              if (response.statusCode() == HttpStatus.OK) {
                return response.bodyToMono(GithubRepositoriesDto.class)
                        .map(githubRepositoriesDto -> githubRepositoryMapper.mapToGithubRepositoryList(githubRepositoriesDto.getItems()))
                        .flatMapMany(Flux::fromIterable);

              }
              //ERROR HANDLING
              log.error("getGithubPublicRepositories({}, {}) failed, HttpStatus " + response.statusCode(), createdAt, language);
              if (response.statusCode() == HttpStatus.INTERNAL_SERVER_ERROR) {
                return Flux.error(new GithubRepositoryServiceException(ErrorCode.GIT_HUB_INTERNAL_SERVER_ERROR,
                        "An internal server error occurred while retrieving repository data"));
              } else {
                return Flux.error(new GithubRepositoryServiceException(ErrorCode.GIT_HUB_OTHER_ERROR,
                                "Error while retrieving repository data  " + response.statusCode()));
              }
            })
            .onErrorMap(WebClientRequestException.class, ex -> {
                log.error("Cannot reach " + ex.getUri());
                return new GithubRepositoryServiceException(ErrorCode.GIT_HUB_SERVICE_UNREACHABLE, ex,
                        String.format("Cannot reach %s, error: %s", ex.getUri(), ex.getMessage()));
            });
  }
}
