/*
 * Copyright (c) 2026 Bareo. All rights reserved.
 *
 * This software is the proprietary and confidential property of the author.
 * Unauthorized copying, distribution, or use is strictly prohibited.
 */
package com.corusoft.bareo.bareobackend.application.usecase.auth;

import lombok.extern.slf4j.Slf4j;

import com.corusoft.bareo.bareobackend.application.service.auth.AuthenticationService;
import com.corusoft.bareo.bareobackend.application.usecase.auth.command.GoogleAuthenticationRevocationUseCaseCommand;
import com.corusoft.bareo.bareobackend.domain.entity.user.vo.UserId;
import com.corusoft.bareo.bareobackend.domain.exception.auth.InvalidTokenException;

import org.springframework.stereotype.Service;

@Service
@Slf4j
public class GoogleAuthenticationRevokeUseCase {

  private final AuthenticationService authenticationService;

  public GoogleAuthenticationRevokeUseCase(AuthenticationService authenticationService) {
    this.authenticationService = authenticationService;
  }

  public void execute(GoogleAuthenticationRevocationUseCaseCommand command) {
    logger.trace("Executing {}", getClass().getSimpleName());

    UserId userId = command.userId();
    try {
      authenticationService.revokeAuthentication(userId);
    } catch (InvalidTokenException e) {
      logger.error("Cannot invaliadte token from user '{}'", userId);
      throw e;
    }

    logger.trace("Executed {}", getClass().getSimpleName());
  }
}
