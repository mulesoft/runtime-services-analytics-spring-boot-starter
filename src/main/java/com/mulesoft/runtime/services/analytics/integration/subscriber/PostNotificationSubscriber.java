/*
 * (c) 2003-2016 MuleSoft, Inc. This software is protected under international copyright law. All use of this software is subject to
 * MuleSoft's Master Subscription Agreement (or other Terms of Service) separately entered into between you and MuleSoft. If such an
 * agreement is not in place, you may not use the software.
 */
package com.mulesoft.runtime.services.analytics.integration.subscriber;


import com.mulesoft.runtime.services.analytics.integration.model.Notification;
import rx.subjects.Subject;

public interface PostNotificationSubscriber {
    void subscribe(Subject<Notification, Notification> subject);
}
