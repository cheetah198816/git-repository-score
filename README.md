# git-repository-score
This service scores the github public repositories between 1-5 considering factors like stars, forks and recent updates.

## Score Calculation Algorithm for each Github Repository
For each Github Repository individual scores for stars, forks and recent updates are calculated according to tables below 
### Score Calculation for stars and forks
| Stars / Forks        | Score |
|----------------------|---|
| Between 0 and 100    | 1 | 
| Between 101 and 300  | 2 | 
| Between 301 and 500  | 3 | 
| Between 501 and 1000 | 4 | 
| More than 1000       | 5 |
### Score Calculation for recency of updates
| Recency of updates                                                           | Score |
|------------------------------------------------------------------------------|---|
| Last updated more than a year ago                                            | 1 | 
| Last Updated more than a month ago but less than a year                      | 2 | 
| Last Updated more than a week ago but less than a month                      | 3 | 
| Last updated more than 2 days ago from the current date but less than a week | 4 | 
| Last updated recently                                                        | 5 |

### The final score for each Github Repository is the average of individuals scores of stars, forks and recency of updates.


### Prerequisites

Before starting, make sure you have at least those components on your workstation:

- [Java Development Kit (JDK)](https://www.azul.com/downloads/?package=jdk#download-openjdk) >= 17
- Docker
- Maven

## Build the project

```shell
$ mvn install
```

## Run the project locally

```shell
$ docker run --name git-hub-score-service -d -p 8080:8080 git-repository/git-repository-score
```