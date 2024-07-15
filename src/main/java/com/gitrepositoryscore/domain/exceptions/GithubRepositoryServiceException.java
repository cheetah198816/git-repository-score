package com.gitrepositoryscore.domain.exceptions;

public class GithubRepositoryServiceException extends RuntimeException {
  private ErrorCode errorCode;

  public GithubRepositoryServiceException(ErrorCode errorCode, Throwable cause) {
    super(cause);
    this.errorCode = errorCode;
  }

  public GithubRepositoryServiceException(ErrorCode errorCode, String additionalMessage) {
    super(additionalMessage);
    this.errorCode = errorCode;
  }

  public GithubRepositoryServiceException(ErrorCode errorCode, Throwable cause, String additionalMessage) {
    super(additionalMessage, cause);
    this.errorCode = errorCode;
  }

  public ErrorCode getErrorCode() {
    return errorCode;
  }
}
