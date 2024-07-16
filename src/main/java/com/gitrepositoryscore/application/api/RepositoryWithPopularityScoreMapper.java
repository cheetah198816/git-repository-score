package com.gitrepositoryscore.application.api;

import com.gitrepositoryscore.domain.RepositoryWithPopularityScore;
import com.gitrepositoryscore.generated.api.model.RepositoryWithPopularityScoreDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;

@Mapper(imports = BigDecimal.class)
public interface RepositoryWithPopularityScoreMapper {

    @Mapping(target = "repositoryName", source = "fullName")
    RepositoryWithPopularityScoreDto mapToDto(RepositoryWithPopularityScore repositoryWithPopularityScore);
}
