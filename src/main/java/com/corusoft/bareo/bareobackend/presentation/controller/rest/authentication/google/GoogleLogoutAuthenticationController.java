/*
 * Copyright (c) 2026 Bareo. All rights reserved.
 *
 * This software is the proprietary and confidential property of the author.
 * Unauthorized copying, distribution, or use is strictly prohibited.
 */
package com.corusoft.bareo.bareobackend.presentation.controller.rest.authentication.google;

import com.corusoft.bareo.bareobackend.application.usecase.authentication.GoogleLogoutUseCase;
import com.corusoft.bareo.bareobackend.application.usecase.authentication.command.GoogleLogoutUseCaseCommand;
import com.corusoft.bareo.bareobackend.domain.entity.user.vo.UserId;
import com.corusoft.bareo.bareobackend.domain.exception.auth.InvalidTokenException;
import com.corusoft.bareo.bareobackend.presentation.api.ApiResponse;
import com.corusoft.bareo.bareobackend.presentation.api.ApiResponseBuilder;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.corusoft.bareo.bareobackend.infrastructure.config.security.SecurityConstants.USER_ID_REQUEST_ATTRIBUTE;

@Validated
@RestController
@RequestMapping("/v1/auth/google")
public class GoogleLogoutAuthenticationController {

  private final GoogleLogoutUseCase googleLogoutUseCase;

  public GoogleLogoutAuthenticationController(GoogleLogoutUseCase googleLogoutUseCase) {
    this.googleLogoutUseCase = googleLogoutUseCase;
  }

  @PostMapping(path = "/logout", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ApiResponse<Void>> googleLogout(
      @RequestAttribute(USER_ID_REQUEST_ATTRIBUTE) UserId userId) throws InvalidTokenException {

    GoogleLogoutUseCaseCommand command = new GoogleLogoutUseCaseCommand(userId);
    googleLogoutUseCase.execute(command);

    // Build response
    ApiResponse<Void> responseBody = ApiResponseBuilder.success();

    return ResponseEntity.ok(responseBody);
  }
}
