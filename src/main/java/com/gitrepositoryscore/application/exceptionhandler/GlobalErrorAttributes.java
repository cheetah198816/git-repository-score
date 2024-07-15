package com.gitrepositoryscore.application.exceptionhandler;

import com.gitrepositoryscore.domain.exceptions.ErrorCode;
import com.gitrepositoryscore.domain.exceptions.GithubRepositoryServiceException;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

@Component
public class GlobalErrorAttributes extends DefaultErrorAttributes {

    private Environment env;

    public GlobalErrorAttributes(Environment env) {
        this.env = env;
    }

    @Override
    public Map<String, Object> getErrorAttributes(ServerRequest request, ErrorAttributeOptions options) {
        Map<String, Object> defaultMap = super.getErrorAttributes(
                request, options);
        Map<String, Object> attrMap = new HashMap<>();
        var ex = getError(request);
        attrMap.put("status", defaultMap.get("status"));
        attrMap.put("message", ex.getMessage());
        attrMap.put("code", ErrorCode.GENERAL_ERROR.getCode());

        if (ex instanceof GithubRepositoryServiceException) {
            var map = getErrorAttributesForGithubRepositoryServiceException((GithubRepositoryServiceException) ex);
            map.forEach((key, value) -> attrMap.merge(key, value, (v1, v2) -> v2));
        }
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        attrMap.put("trace", sw.toString());
        return attrMap;
    }

    private Map<String, Object> getErrorAttributesForGithubRepositoryServiceException(GithubRepositoryServiceException ex) {
        Map<String, Object> map = new HashMap<>();
        ErrorCode errorCode = ex.getErrorCode();
        map.put("code", errorCode.getCode());
        map.put("status", errorCode.getHttpStatus().value());
        map.put("retryable", errorCode.isRetryable());

        if (ex.getMessage() != null) {
            map.put("message", String.format("%s: %s", errorCode.getMessage(), ex.getMessage()));
        } else {
            map.put("message", errorCode.getMessage());
        }

        return map;
    }
}

