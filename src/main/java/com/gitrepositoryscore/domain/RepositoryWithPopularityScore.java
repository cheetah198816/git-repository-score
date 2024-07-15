package com.gitrepositoryscore.domain;

import lombok.Value;

@Value
public class RepositoryWithPopularityScore {
    String fullName;
    long score;
}
