package com.gitrepositoryscore.application.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gitrepositoryscore.domain.exceptions.ErrorCode;
import com.gitrepositoryscore.generated.api.model.Error;
import com.gitrepositoryscore.generated.api.model.ErrorResponse;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class ReactiveServerAuthenticationEntryPoint implements ServerAuthenticationEntryPoint {
  private ObjectMapper objectMapper;

  public ReactiveServerAuthenticationEntryPoint(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  @Override
  public Mono<Void> commence(ServerWebExchange exchange, AuthenticationException authenticationException) {
    ErrorCode unauthorizedErrorCode = ErrorCode.UNAUTHORIZED;

    ServerHttpResponse response = exchange.getResponse();
    response.setStatusCode(unauthorizedErrorCode.getHttpStatus());
    response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

    Error error = new Error().error(new ErrorResponse()
      .code(unauthorizedErrorCode.getCode())
      .status(unauthorizedErrorCode.getHttpStatus().value())
      .message(authenticationException.getMessage())
      .retryable(unauthorizedErrorCode.isRetryable()));

    return response.writeWith(Mono.fromCallable(() -> {
      DataBuffer dataBuffer = response.bufferFactory().allocateBuffer();
      objectMapper.writeValue(dataBuffer.asOutputStream(), error);
      return dataBuffer;
    }));
  }
}
