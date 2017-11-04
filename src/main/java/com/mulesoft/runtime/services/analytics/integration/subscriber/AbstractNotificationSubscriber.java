/*
 * (c) 2003-2016 MuleSoft, Inc. This software is protected under international copyright law. All use of this software is subject to
 * MuleSoft's Master Subscription Agreement (or other Terms of Service) separately entered into between you and MuleSoft. If such an
 * agreement is not in place, you may not use the software.
 */
package com.mulesoft.runtime.services.analytics.integration.subscriber;

import com.mulesoft.runtime.services.analytics.integration.model.Notification;
import org.slf4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import rx.Scheduler;
import rx.Subscriber;
import rx.subjects.Subject;

public abstract class AbstractNotificationSubscriber implements PostNotificationSubscriber {

    private final Logger logger;

    private final Scheduler scheduler;

    protected AbstractNotificationSubscriber(Logger logger, Scheduler scheduler) {
        this.logger = logger;
        this.scheduler = scheduler;
    }

    public void subscribe(Subject<Notification, Notification> subject) {
        subject
                .onBackpressureBuffer()
                .filter(this::shouldFilter)
                .doOnNext(notification -> {
                    if (logger.isDebugEnabled()) {
                        logger.debug("analytics notification received {}", notification);
                    }
                })
                .subscribeOn(scheduler)
                .onBackpressureBuffer()
                .observeOn(scheduler)
                .subscribe(new Subscriber<Notification>() {
                    @Override
                    public void onCompleted() {
                        logger.info("finished adding notifications to the bucket");
                    }

                    @Override
                    public void onError(Throwable e) {
                        logger.error("there was an error while adding notifications to the bucket", e);
                    }

                    @Override
                    public void onNext(Notification notification) {
                        put(notification);
                    }
                });
    }

    protected abstract boolean shouldFilter(Notification notification);

    protected abstract void put(Notification notification);

    @Scheduled(fixedRateString = "${analytics.ingest.period}")
    private void flushAtFixedRate() {
        flush();
    }

    protected abstract void flush();
}
