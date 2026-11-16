/*
 * Copyright (c) 2026 Bareo. All rights reserved.
 *
 * This software is the proprietary and confidential property of the author.
 * Unauthorized copying, distribution, or use is strictly prohibited.
 */
package com.corusoft.bareo.bareobackend.testing.integration.util.client;

import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

import com.corusoft.bareo.bareobackend.domain.entity.user.vo.UserId;

import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static com.corusoft.bareo.bareobackend.infrastructure.config.security.SecurityConstants.USER_ID_REQUEST_ATTRIBUTE;

public class TestRestClientBuilder {
  private final Map<String, Object> requestAttributes = new HashMap<>();
  private final HttpHeaders defaultHeaders = new HttpHeaders();

  private final WebApplicationContext webApplicationContext;

  public TestRestClientBuilder(WebApplicationContext webApplicationContext) {
    this.webApplicationContext = webApplicationContext;
  }

  public TestRestClient build() {
    // RestTestClient is a wrapper of MockMvc, so MockMvc can be customized
    MockMvc mockMvc =
        MockMvcBuilders.webAppContextSetup(webApplicationContext)
            .addFilters(
                (ServletRequest request, ServletResponse response, FilterChain chain) -> {
                  // Add all request attributes to client
                  requestAttributes.forEach(request::setAttribute);
                  chain.doFilter(request, response);
                })
            .build();

    RestTestClient restTestClient =
        RestTestClient.bindTo(mockMvc)
            .defaultHeaders(httpHeaders -> httpHeaders.putAll(defaultHeaders))
            .build();

    return new TestRestClient(restTestClient);
  }

  private TestRestClientBuilder withRequestAttribute(String name, Object value) {
    this.requestAttributes.put(name, value);
    return this;
  }

  public TestRestClientBuilder withUserIdAttribute(UserId userId) {
    return withRequestAttribute(USER_ID_REQUEST_ATTRIBUTE, userId);
  }

  public TestRestClientBuilder withHeader(String name, String value) {
    this.defaultHeaders.add(name, value);
    return this;
  }
}
