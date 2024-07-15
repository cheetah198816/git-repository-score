package com.gitrepositoryscore.domain.exceptions;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

  INTERNAL_SERVER_ERROR(
    20199,
    HttpStatus.INTERNAL_SERVER_ERROR,
    "INTERNAL_SERVER_ERROR",
    false
  ),
  UNAUTHORIZED(
    20101,
    HttpStatus.UNAUTHORIZED,
    "UNAUTHORIZED",
    false
  ),
  //    this error is used to pass through spring internal errors and is used as the default. It should only ever happen
  //    if we do not know about the specific error case and therefore haven't added any explicit error code for it
  GENERAL_ERROR(
    20198,
    //            will be ignored
    HttpStatus.INTERNAL_SERVER_ERROR,
    //            will be ignored
    "GENERAL_ERROR",
    //            will be ignored
    true
  ),
  INPUT_VALIDATION_FAILED(
    20104,
    HttpStatus.BAD_REQUEST,
    "INPUT_VALIDATION_FAILED",
    false
  ),
  FORBIDDEN(
    20102,
    HttpStatus.FORBIDDEN,
    "FORBIDDEN",
    false
  ),

  //Configuration Service Specific.
  GIT_HUB_OTHER_ERROR(
    20123,
    HttpStatus.INTERNAL_SERVER_ERROR,
    "GIT_HUB_OTHER_ERROR",
    false
  ),
  GIT_HUB_INTERNAL_SERVER_ERROR(
    20125,
    HttpStatus.INTERNAL_SERVER_ERROR,
    "GIT_HUB_INTERNAL_SERVER_ERROR",
    false
  ),
  GIT_HUB_SERVICE_UNAVAILABLE(
    20124,
    HttpStatus.INTERNAL_SERVER_ERROR,
    "GIT_HUB_SERVICE_UNAVAILABLE",
    true
  ),
  GIT_HUB_SERVICE_UNREACHABLE(
          20117,
          HttpStatus.INTERNAL_SERVER_ERROR,
          "GIT_HUB_SERVICE_UNREACHABLE",
          true
  );

  private final int code;

  private final HttpStatus httpStatus;

  private final String message;

  private final boolean retryable;

  ErrorCode(int code, HttpStatus httpStatus, String message, boolean retryable) {
    this.code = code;
    this.httpStatus = httpStatus;
    this.message = message;
    this.retryable = retryable;
  }

  public int getCode() {
    return code;
  }

  public HttpStatus getHttpStatus() {
    return httpStatus;
  }

  public String getMessage() {
    return message;
  }

  public boolean isRetryable() {
    return retryable;
  }
}
