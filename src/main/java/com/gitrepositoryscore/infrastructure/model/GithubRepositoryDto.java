package com.gitrepositoryscore.infrastructure.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class GithubRepositoryDto {
    @JsonProperty("full_name")
    String fullName;
    @JsonProperty("pushed_at")
    String updatedAt;
    @JsonProperty("created_at")
    String createdAt;
    @JsonProperty("stargazers_count")
    int stargazersCount;
    @JsonProperty("forks")
    int forks;
}
