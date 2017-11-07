/*
 * (c) 2003-2016 MuleSoft, Inc. This software is protected under international copyright law. All use of this software is subject to
 * MuleSoft's Master Subscription Agreement (or other Terms of Service) separately entered into between you and MuleSoft. If such an
 * agreement is not in place, you may not use the software.
 */
package com.mulesoft.runtime.services.analytics.integration.model;

public class MetricsIngestException extends RuntimeException {

    private static final long serialVersionUID = 2934630719476250192L;

    public MetricsIngestException() {
        super();
    }

    public MetricsIngestException(String message) {
        super(message);
    }

    public MetricsIngestException(String message, Throwable cause) {
        super(message, cause);
    }

    public MetricsIngestException(Throwable cause) {
        super(cause);
    }

}



