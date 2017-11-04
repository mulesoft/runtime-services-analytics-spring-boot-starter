/*
 * (c) 2003-2016 MuleSoft, Inc. This software is protected under international copyright law. All use of this software is subject to
 * MuleSoft's Master Subscription Agreement (or other Terms of Service) separately entered into between you and MuleSoft. If such an
 * agreement is not in place, you may not use the software.
 */
package com.mulesoft.integration.springboot.client.action;


import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.util.UriComponentsBuilder;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@ContextConfiguration
@TestPropertySource("classpath:application-test.properties")
public class AnalyticsQueryTest {

    @Value("${analytics.test.query.url}")
    private String analyticsQueryUrl;

    private TestRestTemplate restTemplate = new TestRestTemplate();

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(8090);

    HttpHeaders headers = new HttpHeaders();

    @Test
    public void testAnalyticsQuery() {

        wireMockRule.stubFor(any(urlPathMatching("/events/1.0/.*")).withQueryParam("start_date", containing("2017-10-01T00:00:00"))
                .withQueryParam("end_date", containing("2017-10-26T00:00:00")).willReturn(aResponse().
                withHeader("Content-Type", "text/json").withStatus(200).withBody("{}")));

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(createURL("/events/1.0/c3a49b88-6143-4f71-a586-d54ad345da3e/1day"))
                .queryParam("start_date", "2017-10-01T00:00:00")
                .queryParam("end_date", "2017-10-26T00:00:00");

        HttpEntity<?> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(builder.build().toUriString(),
                HttpMethod.GET, entity, String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void notMatchOnUrlPathWhenExtraPathStringPresent() {

        wireMockRule.stubFor(any(urlPathMatching("/events/1.0/.*")).withQueryParam("start_date", containing("2017-10-01T00:00:00"))
                .withQueryParam("end_date", containing("2017-10-26T00:00:00")).willReturn(aResponse().
                        withHeader("Content-Type", "text/json").withStatus(200).withBody("{}")));

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(createURL("/events/analytics/1.0/c3a49b88-6143-4f71-a586-d54ad345da3e/1day"))
                .queryParam("start_date", "2017-10-01T00:00:00")
                .queryParam("end_date", "2017-10-26T00:00:00");

        HttpEntity<?> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(builder.build().toUriString(),
                HttpMethod.GET, entity, String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testInvalidQueryParam() {

            wireMockRule.stubFor(any(urlPathMatching("/events/1.0/.*")).withQueryParam("start_date", containing("2017-10-01T00:00:00"))
                    .withQueryParam("end", containing("2017-10-26T00:00:00")).willReturn(aResponse().
                            withStatus(400)));

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(createURL("/events/1.0/c3a49b88-6143-4f71-a586-d54ad345da3e/1day"))
                    .queryParam("start_date", "2017-10-01T00:00:00")
                    .queryParam("end", "2017-10-26T00:00:00");

            HttpEntity<?> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(builder.build().toUriString(),
                    HttpMethod.GET, entity, String.class);

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        }

    private String createURL(String uri) {
        return analyticsQueryUrl + ":" + wireMockRule.port() + uri;
    }
}
