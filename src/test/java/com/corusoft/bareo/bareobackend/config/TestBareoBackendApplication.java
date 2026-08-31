package com.corusoft.bareo.bareobackend.config;

import com.corusoft.bareo.bareobackend.BareoBackendApplication;
import org.springframework.boot.SpringApplication;

public class TestBareoBackendApplication {

  static void main(String[] args) {
    SpringApplication.from(BareoBackendApplication::main).with(TestcontainersConfiguration.class).run(args);
  }

}
