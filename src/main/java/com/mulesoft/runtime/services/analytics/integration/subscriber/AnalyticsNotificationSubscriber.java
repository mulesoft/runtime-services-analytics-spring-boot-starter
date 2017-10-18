/*
 * (c) 2003-2017 MuleSoft, Inc. This software is protected under international copyright law. All use of this software is subject to
 * MuleSoft's Master Subscription Agreement (or other Terms of Service) separately entered into between you and MuleSoft. If such an
 * agreement is not in place, you may not use the software.
 */
package com.mulesoft.runtime.services.analytics.integration.subscriber;


import com.mulesoft.runtime.services.analytics.integration.service.MetricsIngestService;
import com.mulesoft.runtime.services.analytics.integration.model.MetricsKey;
import com.mulesoft.runtime.services.analytics.integration.model.Notification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import rx.internal.util.RxThreadFactory;
import rx.schedulers.Schedulers;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.concurrent.Executors;

@Named
public class AnalyticsNotificationSubscriber extends AbstractNotificationSubscriber {

    private String analyticsSenderId;

    private MetricsIngestService metricsIngestService;

    private static Logger logger = LoggerFactory.getLogger(AnalyticsNotificationSubscriber.class);

    @Inject
    public AnalyticsNotificationSubscriber(MetricsIngestService metricsIngestService,
                                           @Value("${analytics.senderId}") String analyticsSenderId,
                                           @Value("${analytics.ingest.poolSize:8}") int poolSize) {

        super(logger, Schedulers.from(Executors.newFixedThreadPool(poolSize, new RxThreadFactory(AnalyticsNotificationSubscriber.class.getSimpleName()))));
        this.analyticsSenderId = analyticsSenderId;
        this.metricsIngestService = metricsIngestService;
    }

    @Override
    protected boolean shouldFilter(Notification notification) {
        return notification.isBillable();
    }

    @Override
    protected void put(Notification notification) {


        MetricsKey key = notification.toMetricsKey(metricsIngestService.getCurrentTimeBucket());
        if(key == null) {
            logger.error("instance {} tried to send unsupported resource type {} to analytics", analyticsSenderId, notification.getResourceName());
        } else {
            metricsIngestService.updateMetricsKey(key, notification.getCount(), notification.getBillableUnitCount(), notification.getByteCount());
        }

    }

    @Override
    public void flush() {
        metricsIngestService.updateNextFlushTime();
        metricsIngestService.flush();
    }

    public void forceFlushIfValidationFails() {
        metricsIngestService.flushAll();
    }

    public long getSecondsUntilNextFlush() {
        return metricsIngestService.getSecondsUntilNextFlush();
    }
}
