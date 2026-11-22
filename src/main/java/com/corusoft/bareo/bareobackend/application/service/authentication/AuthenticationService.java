/*
 * Copyright (c) 2026 Bareo. All rights reserved.
 *
 * This software is the proprietary and confidential property of the author.
 * Unauthorized copying, distribution, or use is strictly prohibited.
 */
package com.corusoft.bareo.bareobackend.application.service.authentication;

import lombok.extern.slf4j.Slf4j;

import com.corusoft.bareo.bareobackend.domain.entity.user.User;
import com.corusoft.bareo.bareobackend.domain.entity.user.vo.UserId;
import com.corusoft.bareo.bareobackend.domain.exception.auth.ExpiredTokenException;
import com.corusoft.bareo.bareobackend.domain.exception.auth.InvalidTokenException;
import com.corusoft.bareo.bareobackend.infrastructure.thirdparty.firebase.FirebaseService;
import com.corusoft.bareo.bareobackend.infrastructure.thirdparty.firebase.adapter.FirebaseAdapter;

import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.auth.UserRecord;

import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AuthenticationService {

  private final FirebaseService firebaseService;

  public AuthenticationService(FirebaseService firebaseService) {
    this.firebaseService = firebaseService;
  }

  public User authenticateUserWithToken(String token)
      throws IllegalArgumentException, ExpiredTokenException, InvalidTokenException {
    UserId userId;

    // Validate token
    try {
      FirebaseToken firebaseToken = verifyToken(token);
      userId = new UserId(firebaseToken.getUid());
    } catch (IllegalArgumentException | ExpiredTokenException | InvalidTokenException e) {
      logger.error("Could not authenticate user using token", e);
      throw e;
    }

    UserRecord userRecord = firebaseService.getUser(userId);
    return FirebaseAdapter.toDomainUser(userRecord);
  }

  public FirebaseToken verifyToken(String token)
      throws IllegalArgumentException, ExpiredTokenException, InvalidTokenException {
    return firebaseService.verifyToken(token);
  }

  public void revokeAuthentication(UserId userId) throws InvalidTokenException {
    firebaseService.revokeToken(userId);
  }
}
