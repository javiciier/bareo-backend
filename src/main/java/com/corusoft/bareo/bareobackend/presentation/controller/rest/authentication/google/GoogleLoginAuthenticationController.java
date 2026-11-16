/*
 * Copyright (c) 2026 Bareo. All rights reserved.
 *
 * This software is the proprietary and confidential property of the author.
 * Unauthorized copying, distribution, or use is strictly prohibited.
 */
package com.corusoft.bareo.bareobackend.presentation.controller.rest.authentication.google;

import java.net.URI;

import jakarta.validation.Valid;

import com.corusoft.bareo.bareobackend.application.usecase.authentication.GoogleLoginUseCase;
import com.corusoft.bareo.bareobackend.application.usecase.authentication.command.GoogleLoginUseCaseCommand;
import com.corusoft.bareo.bareobackend.application.usecase.authentication.result.GoogleLoginUseCaseResult;
import com.corusoft.bareo.bareobackend.domain.entity.user.User;
import com.corusoft.bareo.bareobackend.presentation.api.ApiResponse;
import com.corusoft.bareo.bareobackend.presentation.api.ApiResponseBuilder;
import com.corusoft.bareo.bareobackend.presentation.dto.auth.request.GoogleLoginRequest;
import com.corusoft.bareo.bareobackend.presentation.dto.auth.response.AuthenticatedUserResponse;
import com.corusoft.bareo.bareobackend.presentation.mapper.auth.AuthenticationResponseMapper;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Validated
@RestController
@RequestMapping("/v1/auth/google")
public class GoogleLoginAuthenticationController {

  private final GoogleLoginUseCase googleLoginUseCase;

  public GoogleLoginAuthenticationController(GoogleLoginUseCase googleLoginUseCase) {
    this.googleLoginUseCase = googleLoginUseCase;
  }

  @PostMapping(
      path = "",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ApiResponse<AuthenticatedUserResponse>> loginWithGoogle(
      @Valid @RequestBody GoogleLoginRequest request) {
    // Authenticate with provider
    GoogleLoginUseCaseCommand command = new GoogleLoginUseCaseCommand(request.idToken());
    GoogleLoginUseCaseResult result = googleLoginUseCase.execute(command);

    // Build response
    User authenticatedUser = result.authenticatedUser();
    AuthenticatedUserResponse authenticatedUserResponse =
        AuthenticationResponseMapper.toAuthenticatedUserResponse(
            request.idToken(), authenticatedUser);
    ApiResponse<AuthenticatedUserResponse> responseBody =
        ApiResponseBuilder.success(authenticatedUserResponse);
    URI resourceLocation =
        ServletUriComponentsBuilder.fromCurrentContextPath()
            .path("/v1")
            .pathSegment("users", "{userId}")
            .build(authenticatedUser.getId().toString());

    return ResponseEntity.created(resourceLocation)
        .contentType(MediaType.APPLICATION_JSON)
        .body(responseBody);
  }
}
