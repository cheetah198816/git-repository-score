package com.gitrepositoryscore.domain;

import lombok.Value;

@Value
public class GithubRepository {
    String fullName;
    String updatedAt;
    String createdAt;
    int stargazersCount;
    int forks;
}
