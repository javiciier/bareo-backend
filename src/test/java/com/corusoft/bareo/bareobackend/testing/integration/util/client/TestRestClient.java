/*
 * Copyright (c) 2026 Bareo. All rights reserved.
 *
 * This software is the proprietary and confidential property of the author.
 * Unauthorized copying, distribution, or use is strictly prohibited.
 */
package com.corusoft.bareo.bareobackend.testing.integration.util.client;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.springframework.test.web.servlet.client.RestTestClient.RequestBodySpec;
import org.springframework.test.web.servlet.client.RestTestClient.ResponseSpec;

import static java.util.Objects.isNull;

public class TestRestClient {

  private final RestTestClient restClient;

  public TestRestClient(RestTestClient restClient) {
    this.restClient = restClient;
  }

  // region PUBLIC CLIENT METHODS

  public ResponseSpec get(String endpoint) {
    return restClient.get().uri(endpoint).accept(MediaType.APPLICATION_JSON).exchange();
  }

  public ResponseSpec post(String endpoint) {
    return post(endpoint, null);
  }

  public ResponseSpec post(String endpoint, Object body) {
    RequestBodySpec requestSpec =
        restClient
            .post()
            .uri(endpoint)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON);
    if (!isNull(body)) {
      requestSpec.body(body);
    }

    return requestSpec.exchange();
  }

  // endregion PUBLIC CLIENT METHODS

}
