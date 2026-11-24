/*
 * Copyright (c) 2026 Bareo. All rights reserved.
 *
 * This software is the proprietary and confidential property of the author.
 * Unauthorized copying, distribution, or use is strictly prohibited.
 */
package com.corusoft.bareo.bareobackend.application.usecase.authentication;

import lombok.extern.slf4j.Slf4j;

import com.corusoft.bareo.bareobackend.application.service.authentication.AuthenticationService;
import com.corusoft.bareo.bareobackend.application.usecase.authentication.command.GoogleLogoutUseCaseCommand;
import com.corusoft.bareo.bareobackend.domain.entity.user.vo.UserId;
import com.corusoft.bareo.bareobackend.domain.exception.auth.InvalidTokenException;

import org.springframework.stereotype.Service;

@Service
@Slf4j
public class GoogleLogoutUseCase {

  private final AuthenticationService authenticationService;

  public GoogleLogoutUseCase(AuthenticationService authenticationService) {
    this.authenticationService = authenticationService;
  }

  public void execute(GoogleLogoutUseCaseCommand command) {
    logger.trace("Executing {}", getClass().getSimpleName());

    UserId userId = command.userId();
    try {
      authenticationService.revokeAuthentication(userId);
    } catch (InvalidTokenException e) {
      logger.error("Cannot invalidate token from user '{}'", userId);
      throw e;
    }

    logger.trace("Executed {}", getClass().getSimpleName());
  }
}
