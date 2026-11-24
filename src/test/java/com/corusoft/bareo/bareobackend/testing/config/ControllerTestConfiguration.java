/*
 * Copyright (c) 2026 Bareo. All rights reserved.
 *
 * This software is the proprietary and confidential property of the author.
 * Unauthorized copying, distribution, or use is strictly prohibited.
 */
package com.corusoft.bareo.bareobackend.testing.config;

import com.corusoft.bareo.bareobackend.testing.integration.util.client.TestRestClientBuilder;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.web.context.WebApplicationContext;

@TestConfiguration
public class ControllerTestConfiguration {

  @Bean
  @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE) // Create new instance each time it is called
  public TestRestClientBuilder testRestClientBuilder(WebApplicationContext webApplicationContext) {
    return new TestRestClientBuilder(webApplicationContext);
  }
}
