/*
 * (c) 2003-2016 MuleSoft, Inc. This software is protected under international copyright law. All use of this software is subject to
 * MuleSoft's Master Subscription Agreement (or other Terms of Service) separately entered into between you and MuleSoft. If such an
 * agreement is not in place, you may not use the software.
 */
package com.mulesoft.runtime.services.analytics.integration.model;

import java.util.Arrays;

public enum Period {

    MINUTES_1("1minute", 1), MINUTES_15("15minutes", 15),
    HOURS_1("1hour", 60), HOURS_3("3hours", 180),
    DAYS_1("1day", 1_440), DAYS_7("7days", 10_080),
    MONTHS_1("1month", 0), MONTHS_3("3months", 0);

    private String period;
    private long minutes;

    Period(String period, long minutes) {
        this.period = period;
        this.minutes = minutes;
    }

    public static Period fromString(String period) {
        return Arrays.stream(values())
                .filter(value -> value.toString().equals(period))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("invalid model: " + period));
    }

    public String toString() {
        return period;
    }

    public long toMinutes() {
        return minutes;
    }

}
