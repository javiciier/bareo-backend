/*
 * Copyright (c) 2026 Bareo. All rights reserved.
 *
 * This software is the proprietary and confidential property of the author.
 * Unauthorized copying, distribution, or use is strictly prohibited.
 */
package com.corusoft.bareo.bareobackend.testing.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration(proxyBeanMethods = false)
public class TestcontainersConfiguration {
  private static final String POSTGIS_IMAGE_NAME = "postgis/postgis:18-3.7-alpine";

  @Bean
  @ServiceConnection
  PostgreSQLContainer postgresContainer() {
    DockerImageName dockerImageName =
        DockerImageName.parse(POSTGIS_IMAGE_NAME).asCompatibleSubstituteFor("postgres");

    return new PostgreSQLContainer(dockerImageName);
  }
}
