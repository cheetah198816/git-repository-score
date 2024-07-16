package com.gitrepositoryscore.domain;
import reactor.core.publisher.Flux;

import java.time.LocalDate;

public interface GithubRepositoryService {
    Flux<RepositoryWithPopularityScore> getGitHubRepositoriesWithScore(LocalDate createdAt, String language);
}
