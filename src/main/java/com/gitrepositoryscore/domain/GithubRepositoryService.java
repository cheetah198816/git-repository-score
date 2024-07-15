package com.gitrepositoryscore.domain;
import reactor.core.publisher.Flux;

public interface GithubRepositoryService {
    Flux<RepositoryWithPopularityScore> getGitHubRepositoriesWithScore(String createdAt, String language);
}
