/*
 * (c) 2003-2017 MuleSoft, Inc. This software is protected under international copyright law. All use of this software is subject to
 * MuleSoft's Master Subscription Agreement (or other Terms of Service) separately entered into between you and MuleSoft. If such an
 * agreement is not in place, you may not use the software.
 */
package com.mulesoft.runtime.services.analytics.integration.model;

import java.time.OffsetDateTime;

public class MetricsKey {

    private String organizationId;

    private String environmentId;

    private String objectId;

    private String regionId;

    private String objectType;

    private String eventType;

    private OffsetDateTime dateTime;

    public MetricsKey(String organizationId, String environmentId, String regionId, String objectId, String objectType, String eventType, OffsetDateTime dateTime) {
        this.organizationId = organizationId;
        this.environmentId = environmentId;
        this.objectId = objectId;
        this.regionId = regionId;
        this.objectType = objectType;
        this.eventType = eventType;
        this.dateTime = dateTime;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public String getEnvironmentId() {
        return environmentId;
    }

    public String getObjectId() {
        return objectId;
    }

    public String getRegionId() {
        return regionId;
    }

    public String getObjectType() {
        return objectType;
    }

    public String getEventType() {
        return eventType;
    }

    public OffsetDateTime getDateTime() {
        return dateTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MetricsKey that = (MetricsKey) o;

        return organizationId.equals(that.organizationId) &&
                environmentId.equals(that.environmentId) &&
                objectId.equals(that.objectId) &&
                regionId.equals(that.regionId) &&
                objectType == that.objectType &&
                eventType == that.eventType &&
                dateTime.equals(that.dateTime);

    }

    @Override
    public int hashCode() {
        int result = organizationId.hashCode();
        result = 31 * result + environmentId.hashCode();
        result = 31 * result + objectId.hashCode();
        result = 31 * result + regionId.hashCode();
        result = 31 * result + objectType.hashCode();
        result = 31 * result + eventType.hashCode();
        result = 31 * result + dateTime.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "MetricsKey{" +
                "organizationId='" + organizationId + '\'' +
                ", environmentId='" + environmentId + '\'' +
                ", objectId='" + objectId + '\'' +
                ", regionId='" + regionId + '\'' +
                ", objectType=" + objectType +
                ", eventType=" + eventType +
                ", dateTime=" + dateTime +
                '}';
    }
}
