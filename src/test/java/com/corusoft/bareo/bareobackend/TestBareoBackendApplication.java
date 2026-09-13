/*
 * Copyright (c) 2026 Bareo. All rights reserved.
 *
 * This software is the proprietary and confidential property of the author.
 * Unauthorized copying, distribution, or use is strictly prohibited.
 */
package com.corusoft.bareo.bareobackend;

import com.corusoft.bareo.bareobackend.config.TestcontainersConfiguration;

import org.springframework.boot.SpringApplication;

public class TestBareoBackendApplication {

  static void main(String[] args) {
    SpringApplication.from(BareoBackendApplication::main)
        .with(TestcontainersConfiguration.class)
        .run(args);
  }
}
