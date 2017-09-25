/*
 * (c) 2003-2017 MuleSoft, Inc. This software is protected under international copyright law. All use of this software is subject to
 * MuleSoft's Master Subscription Agreement (or other Terms of Service) separately entered into between you and MuleSoft. If such an
 * agreement is not in place, you may not use the software.
 */
package com.mulesoft.runtime.services.analytics.integration.service;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.mulesoft.anypoint.httpclient.AnypointPlatformRequest;
import com.mulesoft.anypoint.httpclient.AnypointPlatformRequestBuilder;
import com.mulesoft.anypoint.restclient.RestClient;
import com.mulesoft.runtime.services.analytics.integration.model.MetricsIngestException;
import com.mulesoft.runtime.services.analytics.integration.model.MetricsResponseItem;
import com.mulesoft.runtime.services.analytics.integration.model.Period;
import com.mulesoft.runtime.services.analytics.integration.model.RawResponse;
import com.ning.http.client.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import rx.Observable;
import rx.Single;

import javax.inject.Inject;
import javax.inject.Named;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Named
public class AnalyticsClient {

    private static final String EMPTY_BODY = "";
    private static Logger logger = LoggerFactory.getLogger(AnalyticsClient.class);

    private final String analyticsIngestUrl;

    private final String analyticsQueryUrl;

    private RestClient httpClient;

    private final ObjectMapper objectMapper;

    @Inject
    public AnalyticsClient(
            @Value("${analytics.ingest.url}") String analyticsIngestUrl,
            @Value("${analytics.query.url}") String analyticsQueryUrl,
            RestClient httpClient) {
        this.analyticsIngestUrl = analyticsIngestUrl;
        this.analyticsQueryUrl = analyticsQueryUrl;
        this.httpClient = httpClient;
        this.objectMapper = new ObjectMapper();
    }

    public Observable<RawResponse> postJson(String payload) {
        logger.info("sending metrics to analytics ingest");
        return httpClient.call(() -> new AnypointPlatformRequestBuilder("PostMetrics", "POST").setUrl(analyticsIngestUrl).addHeader("Content-Type", "application/json").setBody(payload).build(),
                                     anypointPlatformResponse -> new RawResponse(anypointPlatformResponse.getStatusCode(), EMPTY_BODY)).asObservable()
                .doOnError(throwable -> {
                    logger.warn("could not execute ingest request to analytics", throwable);
                    throw new MetricsIngestException("Could not execute ingest request to analytics", throwable);
                });
    }

    public Single<List<MetricsResponseItem>> getMetricsByDate(
            String organizationId,
            OffsetDateTime startDate,
            OffsetDateTime endDate,
            Period period
    ) {
        logger.info("get metrics for {} / {} / {} / {}", organizationId, startDate.toString(), endDate.toString(), period.toString());

        AnypointPlatformRequest
            queryRequest = generateQueryRequest(buildMetricsByDateUrl(organizationId, period), startDate, startDate);

        logger.debug("analytics query metrics by date url {}", queryRequest.toHttpRequest().getUrl());

        return executeQuery(queryRequest)
                .map(this::invalidStatusCodesToExceptions)
                .map(this::rawResponseToMetricsItems);
    }

    public Single<List<MetricsResponseItem>> getMetricsByEnvironment(
            String organizationId,
            String environmentId,
            OffsetDateTime startDate,
            OffsetDateTime endDate,
            Period period
    ) {
        logger.info("get metrics for {} / {} / {} / {} / {}", organizationId, environmentId, startDate.toString(), endDate.toString(), period.toString());

        AnypointPlatformRequest queryRequest = generateQueryRequest(buildMetricsByEnvironmentUrl(organizationId, environmentId, period), startDate, endDate);

        logger.debug("analytics query metrics by environment url {}", queryRequest.toHttpRequest().getUrl());

        return executeQuery(queryRequest)
                .map(this::invalidStatusCodesToExceptions)
                .map(this::rawResponseToMetricsItems);
    }

    private AnypointPlatformRequest generateQueryRequest(String url, OffsetDateTime startDate, OffsetDateTime endDate) {
        return new AnypointPlatformRequestBuilder("QueryMetrics", "GET").setUrl(url)
                    .addQueryParam("start_date", startDate.toString())
                    .addQueryParam("end_date", endDate.toString())
                    .addQueryParam("group_by", "event_type").build();
    }

    private Single<RawResponse> executeQuery(AnypointPlatformRequest request) {
        return httpClient.call(() -> request, (response) -> {
            int statusCode = response.getStatusCode();
            String responseBody;

            try {
                responseBody = response.getResponseBody();
            } catch (Exception e) {
                responseBody = EMPTY_BODY;
            }

            logger.info("received response from analytics with status code {} and response {}", statusCode, responseBody);
            return new RawResponse(statusCode, responseBody);
        }).asObservable().toSingle();
    }

    private String buildMetricsByDateUrl(String organizationId, Period period) {
        return analyticsQueryUrl
                + "/" + organizationId
                + "/" + period.toString();
    }

    private String buildMetricsByEnvironmentUrl(String organizationId, String environmentId, Period period) {
        return analyticsQueryUrl
                + "/" + organizationId
                + "/" + environmentId
                + "/" + period.toString();
    }

    private List<Param> buildQueryParams(OffsetDateTime startDate, OffsetDateTime endDate, String groupBy) {
        List<Param> params = new ArrayList<>();
        params.add(new Param("start_date", startDate.toString()));
        params.add(new Param("end_date", endDate.toString()));
        params.add(new Param("group_by", groupBy));
        return params;
    }

    private RawResponse invalidStatusCodesToExceptions(RawResponse rawResponse) {
        int statusCode = rawResponse.getStatusCode();
        if (statusCode == 200) {
            return rawResponse;
        } else {
            logger.warn("unexpected status code {} from analytics with body: {}", statusCode, rawResponse.getResponseBody());
            throw new MetricsIngestException("Unexpected status code " + statusCode + " from analytics: " + rawResponse.getResponseBody());
        }
    }

    private List<MetricsResponseItem> rawResponseToMetricsItems(RawResponse rawResponse) {
        try {
            return objectMapper.readValue(rawResponse.getResponseBody(), new TypeReference<List<MetricsResponseItem>>() {
            });
        } catch (JsonParseException | InvalidFormatException e) {
            throw new MetricsIngestException(e.getMessage(), e);
        } catch (Exception e) {
            logger.warn("unable to deserialize response from analytics: {}", rawResponse);
            throw new MetricsIngestException("Unable to deserialize response from analytics:" + rawResponse, e);
        }
    }

}
