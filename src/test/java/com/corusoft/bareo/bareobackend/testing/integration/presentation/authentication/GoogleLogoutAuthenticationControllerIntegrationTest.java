/*
 * Copyright (c) 2026 Bareo. All rights reserved.
 *
 * This software is the proprietary and confidential property of the author.
 * Unauthorized copying, distribution, or use is strictly prohibited.
 */
package com.corusoft.bareo.bareobackend.testing.integration.presentation.authentication;

import com.corusoft.bareo.bareobackend.application.usecase.authentication.GoogleLogoutUseCase;
import com.corusoft.bareo.bareobackend.application.usecase.authentication.command.GoogleLogoutUseCaseCommand;
import com.corusoft.bareo.bareobackend.domain.entity.user.User;
import com.corusoft.bareo.bareobackend.presentation.api.ApiResponse;
import com.corusoft.bareo.bareobackend.presentation.controller.rest.authentication.google.GoogleLogoutAuthenticationController;
import com.corusoft.bareo.bareobackend.testing.config.annotation.ControllerIntegrationTest;
import com.corusoft.bareo.bareobackend.testing.dataset.TestUserDataset;
import com.corusoft.bareo.bareobackend.testing.integration.util.client.TestRestClient;
import com.corusoft.bareo.bareobackend.testing.integration.util.client.TestRestClientBuilder;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.client.RestTestClient.ResponseSpec;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

@ControllerIntegrationTest(controllers = {GoogleLogoutAuthenticationController.class})
@TestInstance(Lifecycle.PER_CLASS)
public class GoogleLogoutAuthenticationControllerIntegrationTest {
  private static final String ENDPOINT = "/v1/auth/google/logout";
  private final User testUser = TestUserDataset.getTestUser();

  @Autowired private TestRestClientBuilder testRestClientBuilder;

  @MockitoBean private GoogleLogoutUseCase googleLogoutUseCase;

  @Test
  void when_googleLogout_then_ok() {
    // Arrange
    GoogleLogoutUseCaseCommand expectedCommand = new GoogleLogoutUseCaseCommand(testUser.getId());
    doNothing().when(googleLogoutUseCase).execute(any(GoogleLogoutUseCaseCommand.class));

    // Act
    TestRestClient testClient = testRestClientBuilder.withUserIdAttribute(testUser.getId()).build();
    ResponseSpec controllerResponse = testClient.post(ENDPOINT);

    // Assert
    ApiResponse<Void> responseDto =
        controllerResponse
            .expectStatus()
            .isOk()
            .expectBody(new ParameterizedTypeReference<ApiResponse<Void>>() {})
            .returnResult()
            .getResponseBody();
    verify(googleLogoutUseCase).execute(expectedCommand);

    assertAll(
        () -> assertNotNull(responseDto),
        () -> assertTrue(responseDto.success()),
        () -> assertNull(responseDto.data()));
  }
}
