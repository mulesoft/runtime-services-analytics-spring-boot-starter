/*
 * (c) 2003-2016 MuleSoft, Inc. This software is protected under international copyright law. All use of this software is subject to
 * MuleSoft's Master Subscription Agreement (or other Terms of Service) separately entered into between you and MuleSoft. If such an
 * agreement is not in place, you may not use the software.
 */
package com.mulesoft.integration.springboot.client.action;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.junit.Rule;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpHeaders;

public class BaseTest {


    protected TestRestTemplate restTemplate = new TestRestTemplate();

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(8090);

    protected HttpHeaders headers = new HttpHeaders();

    protected String createUrl(String url, String uri) {
        return url + ":" + wireMockRule.port() + uri;
    }
}
