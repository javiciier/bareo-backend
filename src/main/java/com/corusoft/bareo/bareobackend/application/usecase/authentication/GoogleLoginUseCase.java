/*
 * Copyright (c) 2026 Bareo. All rights reserved.
 *
 * This software is the proprietary and confidential property of the author.
 * Unauthorized copying, distribution, or use is strictly prohibited.
 */
package com.corusoft.bareo.bareobackend.application.usecase.authentication;

import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import com.corusoft.bareo.bareobackend.application.service.authentication.AuthenticationService;
import com.corusoft.bareo.bareobackend.application.service.user.UserService;
import com.corusoft.bareo.bareobackend.application.usecase.authentication.command.GoogleLoginUseCaseCommand;
import com.corusoft.bareo.bareobackend.application.usecase.authentication.result.GoogleLoginUseCaseResult;
import com.corusoft.bareo.bareobackend.domain.entity.user.User;
import com.corusoft.bareo.bareobackend.domain.entity.user.vo.UserId;
import com.corusoft.bareo.bareobackend.domain.exception.auth.TokenCustomizationException;
import com.corusoft.bareo.bareobackend.domain.exception.user.UserNotFoundException;
import com.corusoft.bareo.bareobackend.infrastructure.thirdparty.firebase.FirebaseService;

import org.springframework.stereotype.Service;

import static com.corusoft.bareo.bareobackend.domain.shared.enums.UserRole.ROLE_BASIC;
import static com.corusoft.bareo.bareobackend.infrastructure.config.security.TokenClaim.ROLE_CLAIM;

@Service
@Slf4j
public class GoogleLoginUseCase {

  private static final Map<String, Object> DEFAULT_NEW_USER_CLAIMS =
      Map.of(ROLE_CLAIM.getClaimName(), ROLE_BASIC.name());

  private final AuthenticationService authenticationService;
  private final UserService userService;
  private final FirebaseService firebaseService;

  public GoogleLoginUseCase(
      AuthenticationService authenticationService,
      UserService userService,
      FirebaseService firebaseService) {
    this.authenticationService = authenticationService;
    this.userService = userService;
    this.firebaseService = firebaseService;
  }

  public GoogleLoginUseCaseResult execute(GoogleLoginUseCaseCommand command) {
    logger.trace("Executing {}", getClass().getSimpleName());

    User authenticatedUser = authenticateToken(command);
    UserId userId = authenticatedUser.getId();
    try {
      assignClaims(userId, DEFAULT_NEW_USER_CLAIMS);
      authenticatedUser = persistUser(authenticatedUser);
    } catch (Exception e) {
      userService.deleteUser(userId);
      firebaseService.removeUser(userId);
      throw e;
    }

    GoogleLoginUseCaseResult result = new GoogleLoginUseCaseResult(authenticatedUser);
    logger.trace("Executed {}", getClass().getSimpleName());

    return result;
  }

  private User authenticateToken(GoogleLoginUseCaseCommand command) {
    try {
      return authenticationService.authenticateUserWithToken(command.token());
    } catch (UserNotFoundException | TokenCustomizationException e) {
      logger.error("Cannot authenticate user with token", e);
      throw e;
    }
  }

  private void assignClaims(UserId userId, Map<String, Object> defaultClaims) {
    Map<String, Object> currentClaims = firebaseService.getClaimsForUser(userId);
    if (!currentClaims.containsValue(defaultClaims)) {
      firebaseService.assignClaimsToUser(userId, defaultClaims);
    }
  }

  private User persistUser(User authenticatedUser) {
    // If already exists, return it
    if (userService.existsById(authenticatedUser.getId())) {
      return userService.findById(authenticatedUser.getId());
    }

    return userService.saveUser(authenticatedUser);
  }
}
