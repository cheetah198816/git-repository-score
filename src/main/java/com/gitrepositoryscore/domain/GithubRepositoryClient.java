package com.gitrepositoryscore.domain;

import reactor.core.publisher.Flux;

public interface GithubRepositoryClient {
    Flux<GithubRepository> getGithubPublicRepositories(String createdAt, String language);
}
