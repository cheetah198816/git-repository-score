package com.gitrepositoryscore.domain;

import reactor.core.publisher.Flux;

import java.time.LocalDate;

public interface GithubRepositoryClient {
    Flux<GithubRepository> getGithubPublicRepositories(LocalDate createdAt, String language);
}
