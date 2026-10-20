/*
 * Copyright (c) 2026 Bareo. All rights reserved.
 *
 * This software is the proprietary and confidential property of the author.
 * Unauthorized copying, distribution, or use is strictly prohibited.
 */
package com.corusoft.bareo.bareobackend.presentation.controller.rest.auth;

import java.net.URI;

import jakarta.validation.Valid;

import lombok.extern.slf4j.Slf4j;

import com.corusoft.bareo.bareobackend.application.usecase.authentication.GoogleAuthenticationRevokeUseCase;
import com.corusoft.bareo.bareobackend.application.usecase.authentication.GoogleAuthenticationUseCase;
import com.corusoft.bareo.bareobackend.application.usecase.authentication.command.GoogleAuthenticationRevocationUseCaseCommand;
import com.corusoft.bareo.bareobackend.application.usecase.authentication.command.GoogleAuthenticationUseCaseCommand;
import com.corusoft.bareo.bareobackend.application.usecase.authentication.result.GoogleAuthenticationUseCaseResult;
import com.corusoft.bareo.bareobackend.domain.entity.user.User;
import com.corusoft.bareo.bareobackend.domain.entity.user.vo.UserId;
import com.corusoft.bareo.bareobackend.domain.exception.auth.InvalidTokenException;
import com.corusoft.bareo.bareobackend.presentation.api.ApiResponse;
import com.corusoft.bareo.bareobackend.presentation.api.ApiResponseBuilder;
import com.corusoft.bareo.bareobackend.presentation.dto.auth.request.GoogleAuthenticationRequest;
import com.corusoft.bareo.bareobackend.presentation.dto.auth.response.AuthenticatedUserResponse;
import com.corusoft.bareo.bareobackend.presentation.mapper.auth.AuthenticationResponseMapper;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import static com.corusoft.bareo.bareobackend.infrastructure.config.security.SecurityConstants.USER_ID_REQUEST_ATTRIBUTE;

@Slf4j
@Validated
@RestController
@RequestMapping("/v1/auth")
public class AuthController {

  private final GoogleAuthenticationUseCase googleAuthenticationUseCase;
  private final GoogleAuthenticationRevokeUseCase revokeGoogleAuthenticationUseCase;

  public AuthController(
      GoogleAuthenticationUseCase googleAuthenticationUseCase,
      GoogleAuthenticationRevokeUseCase googleAuthenticationRevokeUseCase) {
    this.googleAuthenticationUseCase = googleAuthenticationUseCase;
    this.revokeGoogleAuthenticationUseCase = googleAuthenticationRevokeUseCase;
  }

  @PostMapping(
      path = "/google",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ApiResponse<AuthenticatedUserResponse>> authenticateWithGoogle(
      @Valid @RequestBody GoogleAuthenticationRequest request) {
    // Authenticate with provider
    GoogleAuthenticationUseCaseCommand command =
        new GoogleAuthenticationUseCaseCommand(request.idToken());
    GoogleAuthenticationUseCaseResult result = googleAuthenticationUseCase.execute(command);

    // Build response
    User authenticatedUser = result.authenticatedUser();
    AuthenticatedUserResponse authenticatedUserResponse =
        AuthenticationResponseMapper.toAuthenticatedUserResponse(
            request.idToken(), authenticatedUser);
    ApiResponse<AuthenticatedUserResponse> responseBody =
        ApiResponseBuilder.success(authenticatedUserResponse);
    URI resourceLocation =
        ServletUriComponentsBuilder.fromCurrentRequest()
            .pathSegment("api", "v1", "users", "{userId}")
            .build(authenticatedUser.getId().toString());

    return ResponseEntity.created(resourceLocation)
        .contentType(MediaType.APPLICATION_JSON)
        .body(responseBody);
  }

  @PostMapping(path = "/google/logout")
  public ResponseEntity<ApiResponse<Void>> removeAuthenticationWithGoogle(
      @RequestAttribute(USER_ID_REQUEST_ATTRIBUTE) UserId userId) throws InvalidTokenException {
    // Remove authentication from provider
    GoogleAuthenticationRevocationUseCaseCommand command =
        new GoogleAuthenticationRevocationUseCaseCommand(userId);
    revokeGoogleAuthenticationUseCase.execute(command);

    // Build response
    ApiResponse<Void> responseBody = ApiResponseBuilder.success();

    return ResponseEntity.ok(responseBody);
  }
}
