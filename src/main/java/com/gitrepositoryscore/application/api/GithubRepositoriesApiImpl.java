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
import java.time.LocalDate;
import java.util.Date;

@RestController
@Slf4j
@RequiredArgsConstructor
public class GithubRepositoriesApiImpl extends V1Api implements GithubRepositoriesApi {
    public final GithubRepositoryService githubRepositoryService;

    public final RepositoryWithPopularityScoreMapper repositoryWithPopularityScoreMapper;
    @Override
    public Mono<ResponseEntity<Flux<RepositoryWithPopularityScoreDto>>> getGitHubRepositoriesWithScore(LocalDate createdAt, String language, ServerWebExchange exchange) {
        log.info("getGitHubRepositoriesWithScore({}, {})", createdAt, language);
        Flux<RepositoryWithPopularityScoreDto> repositoryWithPopularityScoreDtoFlux = githubRepositoryService.getGitHubRepositoriesWithScore(createdAt, language)
                .map(repositoryWithPopularityScoreMapper::mapToDto);
        return Mono.just(ResponseEntity.ok(repositoryWithPopularityScoreDtoFlux));
    }
}
