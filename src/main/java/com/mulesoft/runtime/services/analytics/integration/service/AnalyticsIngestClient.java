/*
 * (c) 2003-2016 MuleSoft, Inc. This software is protected under international copyright law. All use of this software is subject to
 * MuleSoft's Master Subscription Agreement (or other Terms of Service) separately entered into between you and MuleSoft. If such an
 * agreement is not in place, you may not use the software.
 */
package com.mulesoft.runtime.services.analytics.integration.service;

import com.mulesoft.anypoint.httpclient.AnypointPlatformRequest;
import com.mulesoft.anypoint.httpclient.AnypointPlatformRequestBuilder;
import com.mulesoft.anypoint.restclient.RestClient;
import com.mulesoft.runtime.services.analytics.integration.log.RestClientLogHelper;
import com.mulesoft.runtime.services.analytics.integration.model.MetricsIngestException;
import com.mulesoft.runtime.services.analytics.integration.model.RawResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import rx.Observable;

import javax.inject.Inject;
import javax.inject.Named;

public class AnalyticsIngestClient {

    private static final String EMPTY_BODY = "";

    private static Logger logger = LoggerFactory.getLogger(AnalyticsIngestClient.class);

    private final String analyticsIngestUrl;

    private RestClient httpClient;

    private final RestClientLogHelper logHelper;

    @Inject
    public AnalyticsIngestClient(
        @Value("${analytics.ingest.url}") String analyticsIngestUrl,
        RestClient httpClient) {
        this.analyticsIngestUrl = analyticsIngestUrl;
        this.httpClient = httpClient;
        logHelper = new RestClientLogHelper(logger);
    }

    public Observable<RawResponse> postJson(String payload) {
        logger.info("sending metrics to analytics ingest");
        return httpClient.call(() -> {
            AnypointPlatformRequest request = new AnypointPlatformRequestBuilder("PostMetrics", "POST").setUrl(analyticsIngestUrl)
                .addHeader("Content-Type", "application/json").setBody(payload).build();
            logHelper.logRequest(request.getName(), request, payload);
            return request;

        }, anypointPlatformResponse -> {
            logHelper.logResponse(anypointPlatformResponse);
            return new RawResponse(anypointPlatformResponse.getStatusCode(), EMPTY_BODY);
        }).asObservable()
            .doOnError(throwable -> {
                logger.warn("could not execute ingest request to analytics", throwable);
                throw new MetricsIngestException("Could not execute ingest request to analytics", throwable);
            });
    }
}
