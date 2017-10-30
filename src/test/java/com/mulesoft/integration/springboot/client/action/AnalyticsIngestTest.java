/*
 * (c) 2003-2017 MuleSoft, Inc. This software is protected under international copyright law. All use of this software is subject to
 * MuleSoft's Master Subscription Agreement (or other Terms of Service) separately entered into between you and MuleSoft. If such an
 * agreement is not in place, you may not use the software.
 */
package com.mulesoft.integration.springboot.client.action;


import com.mulesoft.runtime.services.analytics.integration.model.MetricsIngestItem;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.web.client.TestRestTemplate;

import org.springframework.http.*;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.List;


@RunWith(SpringRunner.class)
@ContextConfiguration
@TestPropertySource("classpath:application-test.properties")
public class AnalyticsIngestTest {

    private TestRestTemplate restTemplate = new TestRestTemplate();

    @Value("${analytics.query.url}")
    private String analyticsQueueUrl;

    HttpHeaders headers = new HttpHeaders();

    @Test
    public void testIngestQuery() {
        MetricsIngestItem ingestItem = new MetricsIngestItem("c3a49b88-6143-4f71-a586-d54ad345da3e", "1cc83dc2-3471-4492-bd1f-0d739b7b24f3", "us-east-1",
                "test_store", "STORE", "API_EVENT", "2017-10-25T17:52:00.000Z", "0b7c83f9-0ffb-42f9-bbb8-813598e01e0a", 1L, 1L, 97L );
        HttpEntity<List<MetricsIngestItem>> entity = new HttpEntity<>(Arrays.asList(ingestItem), headers);
        ResponseEntity<String> response = this.restTemplate.exchange(createUrl("v2/analytics/topics/usage-tracking-objectstore"),
                HttpMethod.POST, entity, String.class);

        assertThat(response.getStatusCode(), is((HttpStatus.ACCEPTED)));
    }

    @Test
    public void testIngestIncorrectMethodType() {
        MetricsIngestItem ingestItem = new MetricsIngestItem("c3a49b88-6143-4f71-a586-d54ad345da3e", "1cc83dc2-3471-4492-bd1f-0d739b7b24f3", "us-east-1",
                "test_store", "STORE", "API_EVENT", "2017-10-25T17:52:00.000Z", "0b7c83f9-0ffb-42f9-bbb8-813598e01e0a", 1L, 1L, 97L );
        HttpEntity<List<MetricsIngestItem>> entity = new HttpEntity<>(Arrays.asList(ingestItem), headers);
        ResponseEntity<String> response = this.restTemplate.exchange(createUrl("v2/analytics/topics/usage-tracking-objectstore"),
                HttpMethod.GET, entity, String.class);

        assertThat(response.getStatusCode(), is((HttpStatus.METHOD_NOT_ALLOWED)));
    }

    @Test
    public void testInvalidRequest() {
        MetricsIngestItem ingestItem = new MetricsIngestItem("c3a49b88-6143-4f71-a586-d54ad345da3e", "1cc83dc2-3471-4492-bd1f-0d739b7b24f3", "us-east-1",
                "test_store", "STORE", "API_EVENT", "2017-10-25T17:52:00.000Z", "0b7c83f9-0ffb-42f9-bbb8-813598e01e0a", 1L, 1L, 97L );
        HttpEntity<MetricsIngestItem> entity = new HttpEntity<>(ingestItem, headers);
        ResponseEntity<String> response = this.restTemplate.exchange(createUrl("v2/analytics/topics/usage-tracking-objectstore"),
                HttpMethod.POST, entity, String.class);

        assertThat(response.getStatusCode(), is((HttpStatus.INTERNAL_SERVER_ERROR)));
    }

    private String createUrl(String uri) {
        return analyticsQueueUrl + uri;
    }
}
