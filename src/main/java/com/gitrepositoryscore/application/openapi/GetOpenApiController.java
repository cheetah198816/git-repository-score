package com.gitrepositoryscore.application.openapi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.info.BuildProperties;
import org.springframework.core.io.Resource;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;

import static java.nio.charset.StandardCharsets.UTF_8;

@RestController
public class GetOpenApiController {

  @Autowired
  private BuildProperties buildProperties;


  @Value("classpath:/openapi.yaml")
  private Resource openApiDefinition;

  @GetMapping(value = "/openapi.yaml", produces = "application/yaml")
  public Mono<String> buildOpenApiDefinition() {
    String baseYaml = readResource(openApiDefinition);
    String finalBaseYaml = baseYaml.replace("{{version}}", buildProperties.getVersion());

    return Mono.fromCallable(() -> {
      return finalBaseYaml.toString();
    });
  }

  private String readResource(Resource file) {
    try (Reader reader = new InputStreamReader(file.getInputStream(), UTF_8)) {
      return FileCopyUtils.copyToString(reader);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

}
