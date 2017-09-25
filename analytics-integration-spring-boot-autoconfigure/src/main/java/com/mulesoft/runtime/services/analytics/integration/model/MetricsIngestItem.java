/*
 * (c) 2003-2017 MuleSoft, Inc. This software is protected under international copyright law. All use of this software is subject to
 * MuleSoft's Master Subscription Agreement (or other Terms of Service) separately entered into between you and MuleSoft. If such an
 * agreement is not in place, you may not use the software.
 */
package com.mulesoft.runtime.services.analytics.integration.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class MetricsIngestItem {

    @JsonProperty("org_id")
    private String organizationId;

    @JsonProperty("env_id")
    private String environmentId;

    @JsonProperty("object_type")
    private String objectType;

    @JsonProperty("object_name")
    private String objectName;

    @JsonProperty("region_id")
    private String regionId;

    @JsonProperty("event_type")
    private String event;

    private String timestamp;

    @JsonProperty("sender_id")
    private String senderId;

    private long count;

    @JsonProperty("billable_unit_count")
    private long billableUnitCount;

    @JsonProperty("byte_count")
    private long byteCount;

    public MetricsIngestItem(String organizationId, String environmentId, String regionId, String destinationId, String destinationType, String event, String timestamp, String senderId, long count, long billableUnitCount, long byteCount) {
        this.organizationId = organizationId;
        this.environmentId = environmentId;
        this.objectType = destinationType;
        this.objectName = destinationId;
        this.regionId = regionId;
        this.event = event;
        this.timestamp = timestamp;
        this.senderId = senderId;
        this.count = count;
        this.billableUnitCount = billableUnitCount;
        this.byteCount = byteCount;
    }

    @JsonProperty("org_id")
    public String getOrganizationId() {
        return organizationId;
    }

    @JsonProperty("env_id")
    public String getEnvironmentId() {
        return environmentId;
    }

    @JsonProperty("object_type")
    public String getObjectType() {
        return objectType;
    }

    @JsonProperty("object_name")
    public String getObjectName() {
        return objectName;
    }

    @JsonProperty("region_id")
    public String getRegionId() {
        return regionId;
    }

    @JsonProperty("event_type")
    public String getEvent() {
        return event;
    }

    public String getTimestamp() {
        return timestamp;
    }

    @JsonProperty("sender_id")
    public String getSenderId() {
        return senderId;
    }

    public long getCount() {
        return count;
    }

    @JsonProperty("billable_unit_count")
    public long getBillableUnitCount() {
        return billableUnitCount;
    }

    @JsonProperty("byte_count")
    public long getByteCount() {
        return byteCount;
    }

    @Override
    public String toString() {
        return "MetricsIngestItem{" +
                "organizationId='" + organizationId + '\'' +
                ", environmentId='" + environmentId + '\'' +
                ", objectType='" + objectType + '\'' +
                ", objectName='" + objectName + '\'' +
                ", regionId='" + regionId + '\'' +
                ", event='" + event + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", senderId='" + senderId + '\'' +
                ", count=" + count +
                ", billableUnitCount=" + billableUnitCount +
                ", byteCount=" + byteCount +
                '}';
    }

}
