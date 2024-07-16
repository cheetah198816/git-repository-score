package com.gitrepositoryscore.infrastructure;

import org.springframework.http.HttpMethod;
import org.springframework.web.reactive.function.client.WebClient;

public class WebClientDelegate implements WebClient {

  private WebClient delegate;

  public void setDelegate(WebClient delegate) {
    this.delegate = delegate;
  }

  @Override
  public RequestHeadersUriSpec<?> get() {
    return delegate.get();
  }

  @Override
  public RequestHeadersUriSpec<?> head() {
    return delegate.head();
  }

  @Override
  public RequestBodyUriSpec post() {
    return delegate.post();
  }

  @Override
  public RequestBodyUriSpec put() {
    return delegate.put();
  }

  @Override
  public RequestBodyUriSpec patch() {
    return delegate.patch();
  }

  @Override
  public RequestHeadersUriSpec<?> delete() {
    return delegate.delete();
  }

  @Override
  public RequestHeadersUriSpec<?> options() {
    return delegate.options();
  }

  @Override
  public RequestBodyUriSpec method(HttpMethod method) {
    return delegate.method(method);
  }

  @Override
  public Builder mutate() {
    return delegate.mutate();
  }
}

