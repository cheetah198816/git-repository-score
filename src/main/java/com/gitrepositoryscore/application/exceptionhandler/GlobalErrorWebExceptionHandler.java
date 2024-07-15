package com.gitrepositoryscore.application.exceptionhandler;

import com.gitrepositoryscore.generated.api.model.Error;
import com.gitrepositoryscore.generated.api.model.ErrorResponse;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
public final class GlobalErrorWebExceptionHandler extends AbstractErrorWebExceptionHandler {

  public GlobalErrorWebExceptionHandler(ErrorAttributes errorAttributes, WebProperties webProperties, ApplicationContext applicationContext,
    ServerCodecConfigurer configurer) {
    super(errorAttributes, webProperties.getResources(), applicationContext);

    this.setMessageWriters(configurer.getWriters());
    this.setMessageReaders(configurer.getReaders());
  }

  @Override
  protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
    return RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse);
  }

  private Mono<ServerResponse> renderErrorResponse(ServerRequest request) {
    Map<String, Object> errorPropertiesMap = getErrorAttributes(request,
      ErrorAttributeOptions.defaults());
    var er = new ErrorResponse()
      .code((Integer) errorPropertiesMap.get("code"))
      .status((Integer) errorPropertiesMap.get("status"));

    if (errorPropertiesMap.containsKey("message") && errorPropertiesMap.get("message") != null) {
      er.message(errorPropertiesMap.get("message").toString());
    }

    if (errorPropertiesMap.containsKey("trace")) {
      er.trace(errorPropertiesMap.get("trace").toString());
    }

    if (errorPropertiesMap.containsKey("retryable")) {
      er.retryable((boolean) errorPropertiesMap.get("retryable"));
    } else {
      er.retryable(false);
    }

    var error = new Error().error(er);

    return ServerResponse.status(error.getError().getStatus())
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(error);
  }
}
