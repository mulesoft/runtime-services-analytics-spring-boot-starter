/*
 * (c) 2003-2017 MuleSoft, Inc. This software is protected under international copyright law. All use of this software is subject to
 * MuleSoft's Master Subscription Agreement (or other Terms of Service) separately entered into between you and MuleSoft. If such an
 * agreement is not in place, you may not use the software.
 */
package com.mulesoft.runtime.services.analytics.integration.autoconfiguration;


import com.mulesoft.anypoint.httpclient.HttpClient;
import com.mulesoft.anypoint.restclient.BasicRestClient;
import com.mulesoft.anypoint.restclient.RestClient;
import com.mulesoft.runtime.services.analytics.integration.service.AnalyticsClient;
import com.mulesoft.runtime.services.analytics.integration.service.MetricsIngestService;
import com.mulesoft.runtime.servicies.analytics.integration.subscriber.AnalyticsNotificationSubscriber;
import com.mulesoft.runtime.servicies.analytics.integration.subscriber.SegmentNotificationSubscriber;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AnalyticsAutoConfiguration {

    @Bean
    @ConditionalOnProperty(value = "analytics.subscription.enabled", havingValue = "true")
    public AnalyticsNotificationSubscriber analyticsNotificationSubscriber(MetricsIngestService metricsIngestService,
                                                                           @Value("${analytics.senderId}") String analyticsSenderId,
                                                                           @Value("${analytics.ingest.poolSize:8}") int poolSize) {
        return new AnalyticsNotificationSubscriber(metricsIngestService, analyticsSenderId, poolSize);
    }

    @Bean
    @ConditionalOnProperty(value = "segment.subscription.enabled", havingValue = "true")
    public SegmentNotificationSubscriber segmentNotificationSubscriber(MetricsIngestService metricsIngestService,
                                                                       @Value("${analytics.ingest.poolSize:8}") int poolSize) {
        return new SegmentNotificationSubscriber(metricsIngestService, poolSize);
    }

    @Bean
    @ConditionalOnProperty(value = "analytics.subscription.enabled", havingValue = "true")
    public MetricsIngestService metricsIngestService(AnalyticsClient anypointAnalyticsClient,
                                                     @Value("${segment.writekey}") String writekey,
                                                     @Value("${analytics.senderId}") String analyticsSenderId,
                                                     @Value("${analytics.ingest.granularity}") int granularity,
                                                     @Value("${analytics.ingest.period}") int period,
                                                     @Value("${analytics.ingest.poolSize:8}") int poolSize) {
        return new MetricsIngestService(anypointAnalyticsClient, writekey, analyticsSenderId, granularity, period, poolSize);
    }

    @Bean
    @ConditionalOnProperty(value = "analytics.subscription.enabled", havingValue = "true")
    public AnalyticsClient analyticsClient(@Value("${analytics.ingest.url}") String analyticsIngestUrl,
                                           @Value("${analytics.query.url}") String analyticsQueryUrl,
                                           RestClient httpClient) {
        return new AnalyticsClient(analyticsIngestUrl, analyticsQueryUrl, httpClient);
    }

    @Bean
    @ConditionalOnMissingBean(RestClient.class)
    @ConditionalOnProperty(value = "analytics.subscription.enabled", havingValue = "true")
    public RestClient restClient(HttpClient httpClient) {
        return new BasicRestClient(httpClient);
    }

    @Bean
    @ConditionalOnMissingBean(HttpClient.class)
    @ConditionalOnProperty(value = "analytics.subscription.enabled", havingValue = "true")
    public HttpClient objectStoreDefaultHttpClient(@Value("${http.client.insecure:false}") boolean insecure,
                                                   @Value("${http.client.timeout:20000}") int timeout) {
        return new HttpClient.Builder().setInsecure(insecure).setTimeout(timeout).build();
    }

}
