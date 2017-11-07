/*
 * (c) 2003-2016 MuleSoft, Inc. This software is protected under international copyright law. All use of this software is subject to
 * MuleSoft's Master Subscription Agreement (or other Terms of Service) separately entered into between you and MuleSoft. If such an
 * agreement is not in place, you may not use the software.
 */
package com.mulesoft.runtime.services.analytics.integration.model;

import java.time.OffsetDateTime;

public class EventKey {

    private String organizationId;
    private String environmentId;
    private String resourceId;
    private String regionId;
    private String event;
    private OffsetDateTime dateTime;

    public EventKey(String organizationId, String environmentId, String regionId, String resourceId, String event, OffsetDateTime dateTime) {
        this.organizationId = organizationId;
        this.environmentId = environmentId;
        this.resourceId = resourceId;
        this.regionId = regionId;
        this.event = event;
        this.dateTime = dateTime;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public String getEnvironmentId() {
        return environmentId;
    }

    public String getResourceId() {
        return resourceId;
    }

    public String getRegionId() {
        return regionId;
    }

    public String getEvent() {
        return event;
    }

    public OffsetDateTime getDateTime() {
        return dateTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EventKey eventKey = (EventKey) o;

        return organizationId.equals(eventKey.organizationId) &&
                environmentId.equals(eventKey.environmentId) &&
                resourceId.equals(eventKey.resourceId) &&
                regionId.equals(eventKey.regionId) &&
                event.equals(eventKey.event) &&
                dateTime.equals(eventKey.dateTime);

    }

    @Override
    public int hashCode() {
        int result = organizationId.hashCode();
        result = 31 * result + environmentId.hashCode();
        result = 31 * result + resourceId.hashCode();
        result = 31 * result + regionId.hashCode();
        result = 31 * result + event.hashCode();
        result = 31 * result + dateTime.hashCode();
        return result;
    }
}
