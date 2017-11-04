/*
 * (c) 2003-2016 MuleSoft, Inc. This software is protected under international copyright law. All use of this software is subject to
 * MuleSoft's Master Subscription Agreement (or other Terms of Service) separately entered into between you and MuleSoft. If such an
 * agreement is not in place, you may not use the software.
 */
package com.mulesoft.runtime.services.analytics.integration.subscriber;

import com.mulesoft.runtime.services.analytics.integration.service.MetricsIngestService;
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
public class SegmentNotificationSubscriber extends AbstractNotificationSubscriber {


    private final MetricsIngestService metricsIngestService;

    private static Logger logger = LoggerFactory.getLogger(SegmentNotificationSubscriber.class);

    @Inject
    public SegmentNotificationSubscriber(MetricsIngestService metricsIngestService,
                                         @Value("${analytics.ingest.poolSize:8}") int poolSize) {
        super(logger, Schedulers.from(Executors.newFixedThreadPool(poolSize, new RxThreadFactory(SegmentNotificationSubscriber.class.getSimpleName()))));
        this.metricsIngestService = metricsIngestService;
    }

    @Override
    protected boolean shouldFilter(Notification notification) {
        return notification.shouldNotifySegment();
    }

    @Override
    protected void put(Notification notification) {
        metricsIngestService.computeSegmentData(notification.toEventKey(metricsIngestService.getCurrentTimeBucket()));
    }

    @Override
    protected void flush() {
        metricsIngestService.flushAllSegmentData();
    }

}
