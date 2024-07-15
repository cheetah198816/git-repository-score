package com.gitrepositoryscore.infrastructure.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

@Configuration
public class GithubRepositoryClientConfiguration {

  @Value("${github.url}")
  private String baseUrl;

  @Value("${github.timeout}")
  private int timeoutInSeconds;

  @Value("${webflux.memory.size}")
  private int maxMemoryInKb;

  @Bean
  public WebClient githubWebClient(ObjectMapper objectMapper) {
    HttpClient httpClient = HttpClient.create()
      .responseTimeout(Duration.ofSeconds(timeoutInSeconds));

    return WebClient.builder()
      .baseUrl(baseUrl)
      .clientConnector(new ReactorClientHttpConnector(httpClient))
      .exchangeStrategies(
        ExchangeStrategies
          .builder()
          .codecs(configurer -> {
            configurer.defaultCodecs().jackson2JsonEncoder(new Jackson2JsonEncoder(objectMapper));
            configurer.defaultCodecs().maxInMemorySize(maxMemoryInKb * 1024);
          }).build())
      .build();
  }
}
