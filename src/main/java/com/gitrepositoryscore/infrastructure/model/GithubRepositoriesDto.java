package com.gitrepositoryscore.infrastructure.model;

import lombok.Data;

import java.util.List;

@Data
public class GithubRepositoriesDto {
    List<GithubRepositoryDto> items;
}
