package com.gitrepositoryscore.domain;

import lombok.Value;

import java.math.BigDecimal;

@Value
public class RepositoryWithPopularityScore {
    String fullName;
    BigDecimal score;
    int stars;
    int forks;
    String updatedAt;
}
