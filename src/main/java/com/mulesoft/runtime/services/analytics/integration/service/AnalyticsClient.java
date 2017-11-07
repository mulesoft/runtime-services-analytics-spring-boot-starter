/*
 * (c) 2003-2016 MuleSoft, Inc. This software is protected under international copyright law. All use of this software is subject to
 * MuleSoft's Master Subscription Agreement (or other Terms of Service) separately entered into between you and MuleSoft. If such an
 * agreement is not in place, you may not use the software.
 */
package com.mulesoft.runtime.services.analytics.integration.service;

import com.mulesoft.runtime.services.analytics.integration.model.MetricsResponseItem;
import com.mulesoft.runtime.services.analytics.integration.model.Period;
import com.mulesoft.runtime.services.analytics.integration.model.RawResponse;
import rx.Observable;
import rx.Single;

import javax.inject.Inject;
import javax.inject.Named;
import java.time.OffsetDateTime;
import java.util.List;

@Named
public class AnalyticsClient {

    private AnalyticsQueryClient queryClient;

    private AnalyticsIngestClient ingestClient;

    @Inject
    public AnalyticsClient(AnalyticsQueryClient queryClient, AnalyticsIngestClient ingestClient) {
        this.queryClient = queryClient;
        this.ingestClient = ingestClient;
    }

    public Observable<RawResponse> postJson(String payload) {
        return ingestClient.postJson(payload);
    }

    public Single<List<MetricsResponseItem>> getMetricsByDate(
        String organizationId,
        OffsetDateTime startDate,
        OffsetDateTime endDate,
        Period period) {
        return queryClient.getMetricsByDate(organizationId, startDate, endDate, period);
    }

    public Single<List<MetricsResponseItem>> getMetricsByEnvironment(
        String organizationId,
        String environmentId,
        OffsetDateTime startDate,
        OffsetDateTime endDate,
        Period period) {
        return queryClient.getMetricsByEnvironment(organizationId, environmentId, startDate, endDate, period);
    }

}
