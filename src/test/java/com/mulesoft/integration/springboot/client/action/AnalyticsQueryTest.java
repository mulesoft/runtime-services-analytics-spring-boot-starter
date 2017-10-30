/*
 * (c) 2003-2017 MuleSoft, Inc. This software is protected under international copyright law. All use of this software is subject to
 * MuleSoft's Master Subscription Agreement (or other Terms of Service) separately entered into between you and MuleSoft. If such an
 * agreement is not in place, you may not use the software.
 */
package com.mulesoft.integration.springboot.client.action;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.util.UriComponentsBuilder;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@ContextConfiguration
@TestPropertySource("classpath:application-test.properties")
public class AnalyticsQueryTest {


    @Value("${analytics.query.url}")
    private String analyticsQueueUrl;

    private TestRestTemplate restTemplate = new TestRestTemplate();

    HttpHeaders headers = new HttpHeaders();

    @Test
    public void testAnalyticsQuery() {

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(createURL("events/1.0/c3a49b88-6143-4f71-a586-d54ad345da3e/1day"))
                .queryParam("start_date", "2017-10-01T00:00:00")
                .queryParam("end_date", "2017-10-26T00:00:00");

        HttpEntity<?> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(builder.build().toUriString(),
                HttpMethod.GET, entity, String.class);

        assertThat(response.getStatusCode(), is((HttpStatus.OK)));
    }

    @Test
    public void testWrongQueryMethod() {

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(createURL("events/1.0/c3a49b88-6143-4f71-a586-d54ad345da3e/1day"))
                .queryParam("start_date", "2017-10-01T00:00:00")
                .queryParam("end_date", "2017-10-26T00:00:00");

        HttpEntity<?> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(builder.build().toUriString(),
                HttpMethod.PUT, entity, String.class);

        assertThat(response.getStatusCode(), is((HttpStatus.METHOD_NOT_ALLOWED)));
    }

    private String createURL(String uri) {
        return analyticsQueueUrl + uri;
    }
}
