package com.corusoft.bareo.bareobackend.integration;

import com.corusoft.bareo.bareobackend.config.TestcontainersConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class BareoBackendApplicationTests {

  @Test
  void contextLoads() {
    // Nothing to do
  }

}
