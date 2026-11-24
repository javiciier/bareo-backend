/*
 * Copyright (c) 2026 Bareo. All rights reserved.
 *
 * This software is the proprietary and confidential property of the author.
 * Unauthorized copying, distribution, or use is strictly prohibited.
 */
package com.corusoft.bareo.bareobackend.testing.integration.presentation.authentication;

import com.corusoft.bareo.bareobackend.application.usecase.authentication.GoogleLoginUseCase;
import com.corusoft.bareo.bareobackend.application.usecase.authentication.command.GoogleLoginUseCaseCommand;
import com.corusoft.bareo.bareobackend.application.usecase.authentication.result.GoogleLoginUseCaseResult;
import com.corusoft.bareo.bareobackend.domain.entity.user.User;
import com.corusoft.bareo.bareobackend.presentation.api.ApiResponse;
import com.corusoft.bareo.bareobackend.presentation.controller.rest.authentication.google.GoogleLoginAuthenticationController;
import com.corusoft.bareo.bareobackend.presentation.dto.auth.request.GoogleLoginRequest;
import com.corusoft.bareo.bareobackend.presentation.dto.auth.response.AuthenticatedUserResponse;
import com.corusoft.bareo.bareobackend.presentation.dto.user.response.UserSummaryResponse;
import com.corusoft.bareo.bareobackend.testing.config.annotation.WebControllerTest;
import com.corusoft.bareo.bareobackend.testing.dataset.TestUserDataset;
import com.corusoft.bareo.bareobackend.testing.integration.util.client.TestRestClient;
import com.corusoft.bareo.bareobackend.testing.integration.util.client.TestRestClientBuilder;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.client.RestTestClient.ResponseSpec;

import static com.corusoft.bareo.bareobackend.testing.dataset.TestUserDataset.RAW_JWT_TOKEN;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebControllerTest(controllers = {GoogleLoginAuthenticationController.class})
public class GoogleLoginAuthenticationControllerIntegrationTest {
  private static final String ENDPOINT = "/v1/auth/google";
  private final User testUser = TestUserDataset.getTestUser();

  @Autowired private TestRestClientBuilder testRestClientBuilder;

  @MockitoBean private GoogleLoginUseCase googleLoginUseCase;

  @Test
  void when_googleLogin_then_ok() {
    // Arrange
    GoogleLoginUseCaseCommand expectedCommand = new GoogleLoginUseCaseCommand(RAW_JWT_TOKEN);
    GoogleLoginUseCaseResult expectedResult = new GoogleLoginUseCaseResult(testUser);
    when(googleLoginUseCase.execute(any(GoogleLoginUseCaseCommand.class)))
        .thenReturn(expectedResult);

    // Act
    TestRestClient testClient = testRestClientBuilder.build();
    GoogleLoginRequest requestBody = new GoogleLoginRequest(RAW_JWT_TOKEN);
    ResponseSpec controllerResponse = testClient.post(ENDPOINT, requestBody);
    ApiResponse<AuthenticatedUserResponse> apiResponse =
        controllerResponse
            .expectStatus()
            .isCreated()
            .expectBody(new ParameterizedTypeReference<ApiResponse<AuthenticatedUserResponse>>() {})
            .returnResult()
            .getResponseBody();

    // Assert
    verify(googleLoginUseCase).execute(expectedCommand);
    assertAll(
        () -> assertNotNull(apiResponse),
        () -> assertTrue(apiResponse.success()),
        () -> assertNotNull(apiResponse.data()));
    AuthenticatedUserResponse authenticatedUser = apiResponse.data();
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
