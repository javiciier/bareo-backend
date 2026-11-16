/*
 * Copyright (c) 2026 Bareo. All rights reserved.
 *
 * This software is the proprietary and confidential property of the author.
 * Unauthorized copying, distribution, or use is strictly prohibited.
 */
package com.corusoft.bareo.bareobackend.testing.integration.presentation.auth;

import com.corusoft.bareo.bareobackend.application.usecase.auth.GoogleAuthenticationRevokeUseCase;
import com.corusoft.bareo.bareobackend.application.usecase.auth.GoogleAuthenticationUseCase;
import com.corusoft.bareo.bareobackend.application.usecase.auth.command.GoogleAuthenticationRevocationUseCaseCommand;
import com.corusoft.bareo.bareobackend.application.usecase.auth.command.GoogleAuthenticationUseCaseCommand;
import com.corusoft.bareo.bareobackend.application.usecase.auth.result.GoogleAuthenticationUseCaseResult;
import com.corusoft.bareo.bareobackend.domain.entity.user.User;
import com.corusoft.bareo.bareobackend.domain.entity.user.vo.UserId;
import com.corusoft.bareo.bareobackend.presentation.MessageTranslator;
import com.corusoft.bareo.bareobackend.presentation.api.ApiResponse;
import com.corusoft.bareo.bareobackend.presentation.controller.rest.auth.AuthController;
import com.corusoft.bareo.bareobackend.presentation.dto.auth.request.GoogleAuthenticationRequest;
import com.corusoft.bareo.bareobackend.presentation.dto.auth.response.AuthenticatedUserResponse;
import com.corusoft.bareo.bareobackend.presentation.dto.user.response.UserSummaryResponse;
import com.corusoft.bareo.bareobackend.testing.config.annotation.ControllerIntegrationTest;
import com.corusoft.bareo.bareobackend.testing.integration.util.client.TestRestClient;
import com.corusoft.bareo.bareobackend.testing.integration.util.client.TestRestClientBuilder;
import com.corusoft.bareo.bareobackend.testing.unit.utils.TestDataFactory;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.client.RestTestClient.ResponseSpec;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ControllerIntegrationTest(controllers = {AuthController.class})
@TestInstance(Lifecycle.PER_CLASS)
public class AuthControllerIntegrationTest {
  private final User testUser = TestDataFactory.createTestUser(new UserId("1234"), "testUser");
  String RAW_JWT_TOKEN =
      """
      eyJhbGciOiJSUzI1NiIsImtpZCI6Im1vY2tfa2V5X2lkIn0
      .eyJzdWIiOiJ0ZXN0LXVzZXItMTIzIiwiZW1haWwiOiJ0ZXN0QGV4YW1wbGUuY29tIiwiaXNzIjoiaHR0cHM6Ly9zZWN1cmV0b2tlbi5nb29nbGUuY29tL3Rlc3QtcHJvamVjdCIsImF1ZCI6InRlc3QtcHJvamVjdCIsImF1dGhfdGltZSI6MTcwMDAwMDAwMCwidXNlcl9pZCI6InRlc3QtdXNlci0xMjMiLCJzdWIiOiJ0ZXN0LXVzZXItMTIzIiwiaWF0IjoxNzAwMDAwMDAwLCJleHAiOjE4MDAwMDAwMDB9
      .signature
      """;

  @Autowired private TestRestClientBuilder testRestClientBuilder;

  @MockitoBean private MessageTranslator messageTranslator;
  @MockitoBean private GoogleAuthenticationUseCase googleAuthenticationUseCase;
  @MockitoBean private GoogleAuthenticationRevokeUseCase googleAuthenticationRevokeUseCase;

  @Nested
  class AuthenticateWithGoogleUseCaseIntegrationTest {
    private static final String ENDPOINT = "/v1/auth/google";

    @Test
    void authenticateWithGoogleUseCase() {
      // Arrange
      GoogleAuthenticationUseCaseCommand expectedCommand =
          new GoogleAuthenticationUseCaseCommand(RAW_JWT_TOKEN);
      GoogleAuthenticationUseCaseResult expectedResult =
          new GoogleAuthenticationUseCaseResult(testUser);
      when(googleAuthenticationUseCase.execute(any(GoogleAuthenticationUseCaseCommand.class)))
          .thenReturn(expectedResult);

      // Act
      TestRestClient testClient = testRestClientBuilder.build();
      GoogleAuthenticationRequest requestBody = new GoogleAuthenticationRequest(RAW_JWT_TOKEN);
      ResponseSpec controllerResponse = testClient.post(ENDPOINT, requestBody);

      // Assert
      verify(googleAuthenticationUseCase).execute(expectedCommand);
      ApiResponse<AuthenticatedUserResponse> responseDto =
          controllerResponse
              .expectStatus()
              .isCreated()
              .expectBody(
                  new ParameterizedTypeReference<ApiResponse<AuthenticatedUserResponse>>() {})
              .returnResult()
              .getResponseBody();

      assertAll(
          () -> assertNotNull(responseDto),
          () -> assertTrue(responseDto.success()),
          () -> assertNotNull(responseDto.data()));
      AuthenticatedUserResponse authenticatedUser = responseDto.data();
      UserSummaryResponse userSummary = authenticatedUser.userSummary();
      assertAll(
          () -> assertNotNull(authenticatedUser.token()),
          () -> assertEquals(authenticatedUser.token(), RAW_JWT_TOKEN),
          () -> assertNotNull(authenticatedUser.userSummary()),
          () -> assertEquals(userSummary.id(), testUser.getId().value()),
          () -> assertEquals(userSummary.username(), testUser.getUsername().value()),
          () -> assertEquals(userSummary.city(), testUser.getCity()),
          () -> assertEquals(userSummary.avatarUrl(), testUser.getAvatar().toString()));
    }
  }

  @Nested
  class RemoveAuthenticationWithGoogleUseCaseIntegrationTest {
    private static final String ENDPOINT = "/v1/auth/google/logout";

    @Test
    void removeAuthenticationWithGoogleUseCase() {
      // Arrange
      GoogleAuthenticationRevocationUseCaseCommand expectedCommand =
          new GoogleAuthenticationRevocationUseCaseCommand(testUser.getId());
      doNothing()
          .when(googleAuthenticationRevokeUseCase)
          .execute(any(GoogleAuthenticationRevocationUseCaseCommand.class));

      // Act
      TestRestClient testClient =
          testRestClientBuilder.withUserIdAttribute(testUser.getId()).build();
      ResponseSpec controllerResponse = testClient.post(ENDPOINT);

      // Assert
      verify(googleAuthenticationRevokeUseCase).execute(expectedCommand);
      ApiResponse<Void> responseDto =
          controllerResponse
              .expectStatus()
              .isOk()
              .expectBody(new ParameterizedTypeReference<ApiResponse<Void>>() {})
              .returnResult()
              .getResponseBody();

      assertAll(
          () -> assertNotNull(responseDto),
          () -> assertTrue(responseDto.success()),
          () -> assertNull(responseDto.data()));
    }
  }
}
