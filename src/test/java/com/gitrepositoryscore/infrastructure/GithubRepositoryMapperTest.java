package com.gitrepositoryscore.infrastructure;


import com.gitrepositoryscore.infrastructure.model.GithubRepositoryDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class GithubRepositoryMapperTest {

  private final GithubRepositoryMapper mapper = Mappers.getMapper(GithubRepositoryMapper.class);

  @Test
  void mapToGithubRepositoryList_ValidInput_Success() {
    var githubRepositoryDto = new GithubRepositoryDto();
    githubRepositoryDto.setFullName("testRepository");
    githubRepositoryDto.setCreatedAt("2021-03-01");
    githubRepositoryDto.setUpdatedAt("2022-03-01");
    githubRepositoryDto.setForks(3);
    githubRepositoryDto.setStargazersCount(3);

    var gitHubDtoList = List.of(githubRepositoryDto);

    var gitHubRespositoryList = mapper.mapToGithubRepositoryList(gitHubDtoList);
    // assertions to verify the result
    var githubRepository = gitHubRespositoryList.get(0);
    assertThat(gitHubRespositoryList).hasSize(1);
    assertThat(githubRepository.getFullName()).isEqualTo(githubRepositoryDto.getFullName());
    assertThat(githubRepository.getForks()).isEqualTo(githubRepositoryDto.getForks());
    assertThat(githubRepository.getStargazersCount()).isEqualTo(githubRepositoryDto.getStargazersCount());
    assertThat(githubRepository.getCreatedAt()).isEqualTo(githubRepositoryDto.getCreatedAt());
    assertThat(githubRepository.getUpdatedAt()).isEqualTo(githubRepositoryDto.getUpdatedAt());
  }
}
