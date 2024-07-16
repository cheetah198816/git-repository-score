package com.gitrepositoryscore.application.api;

import com.gitrepositoryscore.domain.RepositoryWithPopularityScore;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

public class RepositoryWithPopularityScoreMapperTest {
    private final RepositoryWithPopularityScoreMapper mapper = Mappers.getMapper(RepositoryWithPopularityScoreMapper.class);

    @Test
    void mapToDto_ValidInput_Success() {
        var githubRepositoryDto = new RepositoryWithPopularityScore("test", 2l, 100, 200, "2020-07-23T17:23:28Z");

        var gitHubRepositoryWithPopularityScoreDto = mapper.mapToDto(githubRepositoryDto);
        // assertions to verify the result
        assertThat(gitHubRepositoryWithPopularityScoreDto.getRepositoryName()).isEqualTo(githubRepositoryDto.getFullName());
        assertThat(gitHubRepositoryWithPopularityScoreDto.getForks()).isEqualTo(BigDecimal.valueOf(githubRepositoryDto.getForks()));
        assertThat(gitHubRepositoryWithPopularityScoreDto.getStars()).isEqualTo(BigDecimal.valueOf(githubRepositoryDto.getStars()));
        assertThat(gitHubRepositoryWithPopularityScoreDto.getScore()).isEqualTo(BigDecimal.valueOf(githubRepositoryDto.getScore()));
        assertThat(gitHubRepositoryWithPopularityScoreDto.getUpdatedAt()).isEqualTo(githubRepositoryDto.getUpdatedAt());
    }
}
