//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.mulesoft.runtime.services.analytics.integration.log;

import com.mulesoft.anypoint.httpclient.AnypointPlatformRequest;
import com.mulesoft.anypoint.httpclient.AnypointPlatformResponse;
import com.ning.http.client.Request;
import org.slf4j.Logger;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class RestClientLogHelper {
    private Logger logger;

    public RestClientLogHelper(Logger logger) {
        this.logger = logger;
    }

    public void logRequest(String requestDescription, AnypointPlatformRequest anypointRequest) {
        this.logRequest(requestDescription, anypointRequest, (String)null);
    }

    public void logRequest(String operation, AnypointPlatformRequest anypointRequest, String body) {
        if (this.logger.isDebugEnabled()) {
            Request request = anypointRequest.toHttpRequest();
            StringBuilder stringBuilder = new StringBuilder();
            this.stringsAppend(stringBuilder, "REQUEST -> ");
            this.stringsAppend(stringBuilder, operation, " ");
            this.stringsAppend(stringBuilder, request.getMethod(), " ", request.getUrl(), " ");
            stringBuilder.append("QUERY {");
            request.getQueryParams().forEach((queryParam) -> {
                this.stringsAppend(stringBuilder, "'", queryParam.getName(), "'");
                this.stringsAppend(stringBuilder, ",'", queryParam.getValue(), "'");
            });
            stringBuilder.append("} ");
            stringBuilder.append("FORM {");
            request.getFormParams().forEach((queryParam) -> {
                this.stringsAppend(stringBuilder, "'", queryParam.getName(), "'");
                this.stringsAppend(stringBuilder, ",'", queryParam.getValue(), "'");
            });
            stringBuilder.append("} ");
            stringBuilder.append("HEADERS {");
            request.getHeaders().forEach((header) -> {
                this.stringsAppend(stringBuilder, "'", (String)header.getKey(), "'");
                this.stringsAppend(stringBuilder, ",'", (String)((List)header.getValue()).stream().findFirst().orElse(""), "'");
            });
            stringBuilder.append("} ");
            if (!StringUtils.isEmpty(body)) {
                this.stringsAppend(stringBuilder, "BODY {'", this.normalizeString(body), "'}");
            }

            this.logger.debug(stringBuilder.toString());
        }
    }

    public void logResponse(AnypointPlatformResponse response) {
        if (this.logger.isDebugEnabled()) {
            StringBuilder stringBuilder = new StringBuilder();
            this.stringsAppend(stringBuilder, "RESPONSE -> ", String.valueOf(response.getStatusCode()), " ");
            stringBuilder.append("HEADERS {");
            response.getHeaders().forEach((k, v) -> {
                this.stringsAppend(stringBuilder, "'", k, "'");
                this.stringsAppend(stringBuilder, ",'", (String)v.stream().findFirst().orElse(""), "'; ");
            });
            stringBuilder.append("} ");

            try {
                if (response.hasResponseBody()) {
                    this.stringsAppend(stringBuilder, "BODY {'", this.normalizeString(response.getResponseBody()), "'}");
                }
            } catch (IOException var4) {
                ;
            }

            this.logger.debug(stringBuilder.toString());
        }
    }

    private void stringsAppend(StringBuilder stringBuilder, String... strings) {
        Arrays.stream(strings).forEach(stringBuilder::append);
    }

    private String normalizeString(String input) {
        return input.replaceAll("\\r|\\n", "").replaceAll("\\s+", " ");
    }
}
