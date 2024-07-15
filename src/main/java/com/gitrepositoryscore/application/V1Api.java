package com.gitrepositoryscore.application;

import com.gitrepositoryscore.domain.exceptions.ErrorCode;
import com.gitrepositoryscore.domain.exceptions.GithubRepositoryServiceException;
import com.gitrepositoryscore.generated.api.model.Error;
import com.gitrepositoryscore.generated.api.model.ErrorResponse;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.codec.DecodingException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;

import java.util.ArrayList;

@Slf4j
@RequestMapping("/v1/")
public class V1Api {

  @ExceptionHandler({ConstraintViolationException.class})
  public Mono<ResponseEntity<Error>> handleConstraintViolationException(ConstraintViolationException ex) {
    var cv = ex.getConstraintViolations().stream().findFirst().get();
    var pathPartials = cv.getPropertyPath().toString().split("\\.");
    var property = pathPartials[pathPartials.length - 1];
    var rethrow = new GithubRepositoryServiceException(ErrorCode.INPUT_VALIDATION_FAILED, ex, String.format("%s %s", property, cv.getMessage()));
    log.error(rethrow.getMessage(), rethrow);
    var er = new ErrorResponse()
      .code(rethrow.getErrorCode().getCode())
      .status(rethrow.getErrorCode().getHttpStatus().value())
      .message(rethrow.getMessage());

    return Mono.just(ResponseEntity.status(rethrow.getErrorCode().getHttpStatus()).body(new Error().error(er)));
  }

  @ExceptionHandler({ServerWebInputException.class, BindException.class})
  public Mono<ResponseEntity<Error>> handleServerWebInputException(Exception ex) {
    String message = null;

    if (ex instanceof BindException exception) {
      message = extractBindingErrors(exception.getBindingResult());
    }
    if (ex instanceof WebExchangeBindException exception) {
      message = extractBindingErrors(exception.getBindingResult());
    }
    if (ex.getCause() instanceof DecodingException) {
      message = ex.getCause().getMessage();
    }
    if (message == null && ex instanceof ServerWebInputException exception) {
      message = exception.getReason();
    }

    var rethrow = new GithubRepositoryServiceException(ErrorCode.INPUT_VALIDATION_FAILED, ex, message);
    log.error(rethrow.getMessage(), rethrow);
    var er = new ErrorResponse()
      .code(rethrow.getErrorCode().getCode())
      .status(rethrow.getErrorCode().getHttpStatus().value())
      .message(rethrow.getMessage());

    return Mono.just(ResponseEntity.status(rethrow.getErrorCode().getHttpStatus()).body(new Error().error(er)));
  }

  private static String extractBindingErrors(BindingResult bindingResult) {
    ArrayList<String> messages = new ArrayList<>();
    var errors = bindingResult.getAllErrors();
    errors.forEach(error -> {
      if (error instanceof FieldError fieldError) {
        messages.add(fieldError.getField() + " " + error.getDefaultMessage());
      } else {
        messages.add(error.getDefaultMessage());
      }
    });
    return messages.toString();
  }
}
