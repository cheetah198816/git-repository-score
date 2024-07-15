package com.gitrepositoryscore.application.api;

import com.gitrepositoryscore.application.V1Api;
import com.gitrepositoryscore.domain.GithubRepositoryService;
import com.gitrepositoryscore.domain.RepositoryWithPopularityScore;
import com.gitrepositoryscore.generated.api.GithubRepositoriesApi;
import com.gitrepositoryscore.generated.api.model.RepositoryWithPopularityScoreDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@RestController
@Slf4j
@RequiredArgsConstructor
public class GithubRepositoriesApiImpl extends V1Api implements GithubRepositoriesApi {
    public final GithubRepositoryService githubRepositoryService;
    @Override
    public Mono<ResponseEntity<Flux<RepositoryWithPopularityScoreDto>>> getGitHubRepositoriesWithScore(String createdAt, String language, ServerWebExchange exchange) {
        log.info("getGitHubRepositoriesWithScore({}, {})", createdAt, language);
        return Mono.just(ResponseEntity.ok(githubRepositoryService.getGitHubRepositoriesWithScore(createdAt, language)
                .map(repositoryWithPopularityScore -> new RepositoryWithPopularityScoreDto().repositoryName(repositoryWithPopularityScore.getFullName()).score(BigDecimal.valueOf(repositoryWithPopularityScore.getScore())))));
    }
}
