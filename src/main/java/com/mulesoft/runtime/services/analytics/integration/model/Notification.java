/*
 * (c) 2003-2017 MuleSoft, Inc. This software is protected under international copyright law. All use of this software is subject to
 * MuleSoft's Master Subscription Agreement (or other Terms of Service) separately entered into between you and MuleSoft. If such an
 * agreement is not in place, you may not use the software.
 */
package com.mulesoft.runtime.services.analytics.integration.model;

import java.time.OffsetDateTime;

public interface Notification {
    MetricsKey toMetricsKey(OffsetDateTime currentTimeBucket);

    String getOrganizationId();

    String getEnvironmentId();

    String getObjectId();

    long getCount();

    long getBillableUnitCount();

    long getByteCount();

    boolean isBillable();

    String getResourceName();

    boolean shouldNotifySegment();

    EventKey toEventKey(OffsetDateTime offsetDateTime);
}
