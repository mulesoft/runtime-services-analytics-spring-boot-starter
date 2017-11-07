/*
 * (c) 2003-2016 MuleSoft, Inc. This software is protected under international copyright law. All use of this software is subject to
 * MuleSoft's Master Subscription Agreement (or other Terms of Service) separately entered into between you and MuleSoft. If such an
 * agreement is not in place, you may not use the software.
 */
package com.mulesoft.runtime.services.analytics.integration.model;

import java.util.concurrent.atomic.LongAdder;

public class MetricsCounts {

    private LongAdder count;

    private LongAdder billableUnitCount;

    private LongAdder byteCount;

    public MetricsCounts() {
        count = new LongAdder();
        billableUnitCount = new LongAdder();
        byteCount = new LongAdder();
    }

    public LongAdder getCount() {
        return count;
    }

    public LongAdder getBillableUnitCount() {
        return billableUnitCount;
    }

    public LongAdder getByteCount() {
        return byteCount;
    }

}
