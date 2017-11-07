/*
 * (c) 2003-2016 MuleSoft, Inc. This software is protected under international copyright law. All use of this software is subject to
 * MuleSoft's Master Subscription Agreement (or other Terms of Service) separately entered into between you and MuleSoft. If such an
 * agreement is not in place, you may not use the software.
 */
package com.mulesoft.runtime.services.analytics.integration.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;

public class MetricsResponseItem {

    private static final ZoneId UTC_ZONE = ZoneId.of("UTC+00:00");

    public enum Event {API_REQUEST, MESSAGE_RECEIPT}

    private OffsetDateTime timestamp;

    @JsonProperty("event_type")
    private Event event;

    private long count;

    @JsonProperty("billable_unit_count")
    private long billableUnitCount;

    @JsonProperty("byte_count")
    private long byteCount;

    public MetricsResponseItem() {
    }

    public MetricsResponseItem(OffsetDateTime timestamp, Event event, long count, long billableUnitCount, long byteCount) {
        this.timestamp = timestamp;
        this.event = event;
        this.count = count;
        this.billableUnitCount = billableUnitCount;
        this.byteCount = byteCount;
    }

    public OffsetDateTime getTimestamp() {
        return timestamp;
    }

    @JsonProperty("timestamp")
    public void setTimeStamp(String timeString) {
        this.timestamp = OffsetDateTime.ofInstant(Instant.ofEpochMilli(Long.parseLong(timeString)), UTC_ZONE);
    }

    public Event getEvent() {
        return event;
    }

    @JsonProperty("event_type")
    public void setEvent(Event event) {
        this.event = event;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public long getBillableUnitCount() {
        return billableUnitCount;
    }

    @JsonProperty("billable_unit_count")
    public void setBillableUnitCount(long billableUnitCount) {
        this.billableUnitCount = billableUnitCount;
    }

    public long getByteCount() {
        return byteCount;
    }

    @JsonProperty("byte_count")
    public void setByteCount(long byteCount) {
        this.byteCount = byteCount;
    }

}
