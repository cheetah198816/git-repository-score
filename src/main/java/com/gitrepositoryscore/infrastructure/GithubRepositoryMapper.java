package com.gitrepositoryscore.infrastructure;

import com.gitrepositoryscore.domain.GithubRepository;
import com.gitrepositoryscore.infrastructure.model.GithubRepositoryDto;
import org.mapstruct.Mapper;
import java.util.List;

@Mapper
public interface GithubRepositoryMapper {
    List<GithubRepository> mapToGithubRepositoryList(List<GithubRepositoryDto> githubRepositoriesDto);
}
