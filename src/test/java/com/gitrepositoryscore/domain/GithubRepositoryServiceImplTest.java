package com.gitrepositoryscore.domain;

import com.gitrepositoryscore.domain.exceptions.ErrorCode;
import com.gitrepositoryscore.domain.exceptions.GithubRepositoryServiceException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
public class GithubRepositoryServiceImplTest {

    @MockBean
    GithubRepositoryClient githubRepositoryClient;

    @Autowired
    GithubRepositoryService githubRepositoryService;

    @Test
    void getGitHubRepositoriesWithScore_Test() {
        var gitHubRepositories = List.of(
                new GithubRepository("test1", "2024-07-15T07:43:29Z", "2024-06-14", 3, 1),
                new GithubRepository("test2", "2024-07-15T07:43:29Z", "2024-06-14", 200, 200));


        when(githubRepositoryClient.getGithubPublicRepositories(any(), any())).thenReturn(Flux.fromIterable(gitHubRepositories));

        StepVerifier.create(githubRepositoryService.getGitHubRepositoriesWithScore(LocalDate.now(),"java"))
                .expectNextMatches(repositoryWithPopularityScore -> repositoryWithPopularityScore.equals(new RepositoryWithPopularityScore("test1", BigDecimal.valueOf(2.3), 3, 1, "2024-07-15T07:43:29Z")))
                .expectNextMatches(repositoryWithPopularityScore -> repositoryWithPopularityScore.equals(new RepositoryWithPopularityScore("test2", BigDecimal.valueOf(3.0), 200, 200, "2024-07-15T07:43:29Z")))
                .verifyComplete();
    }

    @Test
    void getGitHubRepositoriesWithScore_WithParsingException_Test() {
        var gitHubRepositories = List.of(
                new GithubRepository("test1", "2024-07-15", "2024-06-14", 3, 1),
                new GithubRepository("test2", "2024-07-15", "2024-06-14", 200, 200));


        when(githubRepositoryClient.getGithubPublicRepositories(any(), any())).thenReturn(Flux.fromIterable(gitHubRepositories));

        StepVerifier.create(githubRepositoryService.getGitHubRepositoriesWithScore(LocalDate.now(),"java"))
                .expectErrorMessage("Error occured while parsing updatedAt")
                .verify();
    }

    @Test
    void getGitHubRepositoriesWithScore_WithGithubRepositoryException_Test() {
        when(githubRepositoryClient.getGithubPublicRepositories(any(), any())).thenReturn(Flux.error(new GithubRepositoryServiceException(ErrorCode.GIT_HUB_OTHER_ERROR, "some-other-error")));

        StepVerifier.create(githubRepositoryService.getGitHubRepositoriesWithScore(LocalDate.now(),"java"))
                .expectErrorMessage("some-other-error")
                .verify();
    }
}
