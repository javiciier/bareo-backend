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

import com.corusoft.bareo.bareobackend.application.service.auth.AuthenticationService;
import com.corusoft.bareo.bareobackend.domain.entity.user.User;
import com.corusoft.bareo.bareobackend.presentation.api.ApiResponse;
import com.corusoft.bareo.bareobackend.presentation.api.ApiResponseBuilder;
import com.corusoft.bareo.bareobackend.presentation.dto.auth.request.GoogleAuthenticationRequest;
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

@Slf4j
@Validated
@RestController
@RequestMapping("/v1/auth")
public class AuthController {

  private final AuthenticationService authService;

  public AuthController(AuthenticationService authService) {
    this.authService = authService;
  }

  @PostMapping(
      path = "/google",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ApiResponse<AuthenticatedUserResponse>> authenticateWithGoogle(
      @Valid @RequestBody GoogleAuthenticationRequest request) {
    // Authenticate with provider
    User user = authService.authenticateUserWithToken(request.idToken());

    // Build response
    AuthenticatedUserResponse authenticatedUserResponse =
        AuthenticationResponseMapper.toAuthenticatedUserResponse(request.idToken(), user);
    ApiResponse<AuthenticatedUserResponse> responseBody =
        ApiResponseBuilder.success(authenticatedUserResponse);
    URI resourceLocation =
        ServletUriComponentsBuilder.fromCurrentRequest()
            .pathSegment("api", "v1", "users", "{userId}")
            .build(user.getId().toString());

    return ResponseEntity.created(resourceLocation)
        .contentType(MediaType.APPLICATION_JSON)
        .body(responseBody);
  }
}
