package com.gitrepositoryscore.domain;

import com.gitrepositoryscore.domain.exceptions.ErrorCode;
import com.gitrepositoryscore.domain.exceptions.GithubRepositoryServiceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class GithubRepositoryServiceImpl implements GithubRepositoryService {
    public final GithubRepositoryClient githubRepositoryClient;
    @Override
    public Flux<RepositoryWithPopularityScore> getGitHubRepositoriesWithScore(LocalDate createdAt, String language) {
        return githubRepositoryClient.getGithubPublicRepositories(createdAt, language)
                .map(githubRepository -> {
                    try {
                        double finalScore;
                        double scoreForStars = getScoreOfForksAndStars(githubRepository.getStargazersCount());
                        double scoreForForks = getScoreOfForksAndStars(githubRepository.getForks());
                        double scoreForRecencyOfUpdates = getScoreOfRecentUpdate(githubRepository.getUpdatedAt());
                        finalScore = (scoreForStars + scoreForForks + scoreForRecencyOfUpdates) / 3;
                        return new RepositoryWithPopularityScore(githubRepository.getFullName(), BigDecimal.valueOf(finalScore).setScale(1, RoundingMode.HALF_UP), githubRepository.getStargazersCount(), githubRepository.getForks(), githubRepository.getUpdatedAt());
                    } catch (ParseException e) {
                       throw  new GithubRepositoryServiceException(ErrorCode.INTERNAL_SERVER_ERROR, "Error occured while parsing updatedAt");
                    }
                   });
    }

    private double getScoreOfRecentUpdate(String updatedAt) throws ParseException {
        SimpleDateFormat sdf
                = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Date date1 = sdf.parse(updatedAt);
        Date date2 = new Date();

        long daysOld = getDateDifference(date1, date2, TimeUnit.DAYS);
        if (daysOld > 365) {
            return 1;
        } else if (daysOld > 31 && daysOld < 365) {
            return 2;
        } else if (daysOld > 7 && daysOld < 31) {
            return 3;
        } else if (daysOld > 2 && daysOld < 7) {
            return 4;
        } else {
            return 5;
        }
    }

    private static long getDateDifference(Date date1, Date date2, TimeUnit timeUnit) {
        long differenceInMilliseconds = date2.getTime() - date1.getTime();
        return timeUnit.convert(differenceInMilliseconds, TimeUnit.MILLISECONDS);
    }

    private double getScoreOfForksAndStars(int forksOrStars) {
        if(forksOrStars >= 0 && forksOrStars <= 100) {
            return 1;
        } else if (forksOrStars > 100 && forksOrStars <= 300) {
            return 2;
        } else if (forksOrStars > 300 && forksOrStars <= 500) {
            return 3;
        } else if (forksOrStars > 500 && forksOrStars <= 1000) {
            return 4;
        } else {
            return 5;
        }
    }
}
