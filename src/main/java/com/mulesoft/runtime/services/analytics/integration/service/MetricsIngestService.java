/*
 * (c) 2003-2016 MuleSoft, Inc. This software is protected under international copyright law. All use of this software is subject to
 * MuleSoft's Master Subscription Agreement (or other Terms of Service) separately entered into between you and MuleSoft. If such an
 * agreement is not in place, you may not use the software.
 */
package com.mulesoft.runtime.services.analytics.integration.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mulesoft.runtime.services.analytics.integration.model.EventKey;
import com.mulesoft.runtime.services.analytics.integration.model.MetricsCounts;
import com.mulesoft.runtime.services.analytics.integration.model.MetricsIngestException;
import com.mulesoft.runtime.services.analytics.integration.model.MetricsIngestItem;
import com.mulesoft.runtime.services.analytics.integration.model.MetricsKey;
import com.mulesoft.runtime.services.analytics.integration.model.RawResponse;
import com.segment.analytics.Analytics;
import com.segment.analytics.messages.TrackMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import rx.Subscriber;

import javax.inject.Inject;
import javax.inject.Named;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.Collectors;

@Named
public class MetricsIngestService {

    private static final long CACHE_SIZE = 16384;

    private static Analytics segment;

    private ConcurrentHashMap<EventKey, LongAdder> data;

    private static final String TIME_ZONE = "UTC+00:00";

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    private LoadingCache<MetricsKey, MetricsCounts> metricsCache;

    private AnalyticsClient anypointAnalyticsClient;

    private ObjectWriter objectWriter;

    private String analyticsSenderId;

    private int granularity; // should be in minutes

    private int period; // should be in milliseconds

    private Instant nextFlush; // the next time data will be flushed to analytics

    private static Logger logger = LoggerFactory.getLogger(MetricsIngestService.class);

    @Inject
    public MetricsIngestService(AnalyticsClient anypointAnalyticsClient,
                                @Value("${segment.writekey}") String writekey,
                                @Value("${analytics.senderId}") String analyticsSenderId,
                                @Value("${analytics.ingest.granularity}") int granularity,
                                @Value("${analytics.ingest.period}") int period,
                                @Value("${analytics.ingest.poolSize:8}") int poolSize) {
        this.analyticsSenderId = analyticsSenderId;

        this.metricsCache = CacheBuilder.newBuilder()
                .maximumSize(CACHE_SIZE)
                .build(
                        new CacheLoader<MetricsKey, MetricsCounts>() {
                            @Override
                            public MetricsCounts load(MetricsKey metricsKey) throws Exception {
                                return new MetricsCounts();
                            }
                        }
                );

        this.anypointAnalyticsClient = anypointAnalyticsClient;

        segment = Analytics.builder(writekey).build();

        data = new ConcurrentHashMap<>();

        this.objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();

        this.granularity = granularity;

        this.period = period;

        updateNextFlushTime();
    }

    private void postMetricsBatch(List<MetricsIngestItem> metricsBatch, List<MetricsKey> keysToInvalidate) throws
        JsonProcessingException {

        //enable more visibility by printing out the metrics batch in the debug logs
        if (logger.isDebugEnabled()) {
            metricsBatch.forEach(item -> logger.debug("MetricsIngestItem before sending the batch to analytics for orgId: {}; itemString: {}", item.getOrganizationId(), item.toString()));
        }

        logger.info("instance {} posting {} objects to analytics ingest", analyticsSenderId, metricsBatch.size());

        anypointAnalyticsClient.postJson(objectWriter.writeValueAsString(metricsBatch))
                .subscribe(new Subscriber<RawResponse>() {
                    @Override
                    public void onCompleted() {
                        logger.info("instance finished {} posting {} objects to analytics ingest", analyticsSenderId, metricsBatch.size());
                        metricsCache.invalidateAll(keysToInvalidate);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        logger.error("There was an error while trying to post information to analytics", throwable);
                    }

                    @Override
                    public void onNext(RawResponse rawResponse) {
                        int statusCode = rawResponse.getStatusCode();
                        logger.info("processing response from analytics ingest in instance {} with status code {}", analyticsSenderId, statusCode);
                        if (statusCode != 202) {
                            logger.warn("instance {} received unexpected status code {} from analytics ingest", analyticsSenderId, statusCode);
                        }
                    }
                });
    }

    public void flush() throws MetricsIngestException {

        internalFlush(getMetricsKeys());
    }

    private void internalFlush(List<MetricsKey> metricsKeys) {
        try {
            if (!metricsKeys.isEmpty()) {
                postMetricsBatch(assembleMetricsBatch(metricsKeys), metricsKeys);
            }
        } catch (JsonProcessingException e) {
            logger.error("instance {} encountered a JSON processing error while flushing data to analytics: {}", analyticsSenderId, e);
            throw new MetricsIngestException(e);
        }
    }

    public long getSecondsUntilNextFlush() {
        long msUntilNextFlush = nextFlush.toEpochMilli() - Instant.now().toEpochMilli();
        long secondsUntilNextFlush = msUntilNextFlush / 1000;
        // round up to the next second instead of truncating
        if (msUntilNextFlush % 1000 > 0) {
            ++secondsUntilNextFlush;
        }
        return secondsUntilNextFlush;
    }

    public void updateNextFlushTime() {
        nextFlush = Instant.now().plus(period, ChronoUnit.MILLIS);
    }

    public void updateMetricsKey(MetricsKey key, long count, long billableUnitCount, long byteCount) {
        try {
            MetricsCounts counts = metricsCache.get(key);
            counts.getCount().add(count);
            counts.getBillableUnitCount().add(billableUnitCount);
            counts.getByteCount().add(byteCount);
            if (logger.isDebugEnabled()) {
                logger.debug("metrics updated for key {}, notificationCount {}, billableUnitCount {}, byteCount {}", key, count, billableUnitCount, byteCount);
            }
        } catch (ExecutionException e) {
            logger.error("instance {} encountered an error while trying to update its metrics cache: {}", analyticsSenderId, e);
        }
    }

    private List<MetricsIngestItem> assembleMetricsBatch(List<MetricsKey> keys) {

        List<MetricsIngestItem> metricsBatch = new ArrayList<>();

        metricsCache.getAllPresent(keys).forEach((key, counts) -> {
            metricsBatch.add(
                    new MetricsIngestItem(
                            key.getOrganizationId(),
                            key.getEnvironmentId(),
                            key.getRegionId(), key.getObjectId(),
                            key.getObjectType(),
                            key.getEventType(),
                            key.getDateTime().format(dateTimeFormatter),
                            analyticsSenderId,
                            counts.getCount().sumThenReset(),
                            counts.getBillableUnitCount().sumThenReset(),
                            counts.getByteCount().sumThenReset()
                    )
            );
        });
        return metricsBatch;
    }

    public void flushAllSegmentData() {
        data.forEach((key, val) -> {
            Map<String, String> properties = new HashMap<>();
            properties.put("organizationId", key.getOrganizationId());
            properties.put("environmentId", key.getEnvironmentId());
            properties.put("resourceId", key.getResourceId());
            properties.put("regionId", key.getRegionId());
            properties.put("timeGranularity", Integer.toString(granularity));
            properties.put("timeBucket", key.getDateTime().toString());
            properties.put("count", Long.toString(val.sumThenReset()));
            segment.enqueue(
                TrackMessage.builder(key.getEvent())
                            .userId(key.getOrganizationId())
                            .properties(properties)
            );
        });
        data.clear();
        segment.flush();
    }

    public void flushAll() {
        internalFlush(getAllMetrics());
    }

    public OffsetDateTime getCurrentTimeBucket() {
        OffsetDateTime dateTime = OffsetDateTime.now(ZoneId.of(TIME_ZONE)).truncatedTo(ChronoUnit.MINUTES);
        int minute = dateTime.getMinute();
        int excess = minute % granularity;
        dateTime = dateTime.withMinute(minute - excess);
        return dateTime;
    }

    private List<MetricsKey> getMetricsKeys() {
        return metricsCache.asMap().keySet().stream()
                .filter(key -> key.getDateTime().isBefore(getCurrentTimeBucket()))
                .collect(Collectors.toList());
    }

    private List<MetricsKey> getAllMetrics() {
        return metricsCache.asMap().keySet().stream().collect(Collectors.toList());
    }

    public void computeSegmentData(EventKey eventKey) {
        data.computeIfAbsent(eventKey, key -> new LongAdder()).increment();
    }
}
