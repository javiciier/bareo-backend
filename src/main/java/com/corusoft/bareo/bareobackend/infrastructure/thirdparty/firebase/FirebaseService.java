/*
 * Copyright (c) 2026 Bareo. All rights reserved.
 *
 * This software is the proprietary and confidential property of the author.
 * Unauthorized copying, distribution, or use is strictly prohibited.
 */
package com.corusoft.bareo.bareobackend.infrastructure.thirdparty.firebase;

import java.util.HashMap;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import com.corusoft.bareo.bareobackend.domain.entity.user.vo.UserId;
import com.corusoft.bareo.bareobackend.domain.exception.auth.ExpiredTokenException;
import com.corusoft.bareo.bareobackend.domain.exception.auth.InvalidTokenException;
import com.corusoft.bareo.bareobackend.domain.exception.auth.TokenCustomizationException;
import com.corusoft.bareo.bareobackend.domain.exception.user.UserNotFoundException;

import com.google.firebase.auth.AuthErrorCode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.auth.UserRecord;

import org.springframework.stereotype.Service;

@Slf4j
@Service
public class FirebaseService {

  private final FirebaseAuth firebaseAuth;

  public FirebaseService(FirebaseAuth firebaseAuth) {
    this.firebaseAuth = firebaseAuth;
  }

  public FirebaseToken verifyToken(String token) {
    try {
      return firebaseAuth.verifyIdToken(token, true);
    } catch (FirebaseAuthException e) {
      AuthErrorCode authErrorCode = e.getAuthErrorCode();

      if (AuthErrorCode.EXPIRED_ID_TOKEN.equals(authErrorCode)) {
        throw new ExpiredTokenException();
      }
      if (AuthErrorCode.INVALID_ID_TOKEN.equals(authErrorCode)) {
        throw new InvalidTokenException();
      }

      throw new InvalidTokenException();
    }
  }

  public UserRecord getUser(UserId userId) {
    try {
      return firebaseAuth.getUser(userId.value());
    } catch (FirebaseAuthException e) {
      logger.error("Could not get user details from Firebase for user with ID '{}'", userId, e);
      throw new UserNotFoundException(userId);
    }
  }

  public Map<String, Object> getClaimsForUser(UserId userId) throws UserNotFoundException {
    return getUser(userId).getCustomClaims();
  }

  public void assignClaimsToUser(UserId userId, Map<String, Object> claims)
      throws UserNotFoundException, TokenCustomizationException {
    // Merge old claims with the new ones
    Map<String, Object> currentClaims = getUser(userId).getCustomClaims();
    Map<String, Object> mergedClaims =
        currentClaims != null ? new HashMap<>(currentClaims) : new HashMap<>();
    mergedClaims.putAll(claims);

    try {
      firebaseAuth.setCustomUserClaims(userId.value(), mergedClaims);
    } catch (FirebaseAuthException e) {
      logger.error("Could not assign clamis to user {}", userId);
      throw new TokenCustomizationException(userId);
    }
  }
}
