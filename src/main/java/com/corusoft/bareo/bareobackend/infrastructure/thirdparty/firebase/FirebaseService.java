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

  public UserRecord getUser(UserId userId) throws UserNotFoundException {
    try {
      return firebaseAuth.getUser(userId.value());
    } catch (FirebaseAuthException e) {
      logger.error("Could not get user details from Firebase for user with ID '{}'", userId, e);
      throw new UserNotFoundException(userId);
    }
  }

  public FirebaseToken verifyToken(String token)
      throws IllegalArgumentException, ExpiredTokenException, InvalidTokenException {
    try {
      return firebaseAuth.verifyIdToken(token, true);
    } catch (FirebaseAuthException e) {
      AuthErrorCode authErrorCode = e.getAuthErrorCode();

      if (AuthErrorCode.USER_NOT_FOUND.equals(authErrorCode)) {
        throw new IllegalArgumentException("No user found to verify token");
      }

      if (AuthErrorCode.EXPIRED_ID_TOKEN.equals(authErrorCode)) {
        throw new ExpiredTokenException();
      }
      if (AuthErrorCode.INVALID_ID_TOKEN.equals(authErrorCode)) {
        throw new InvalidTokenException();
      }

      throw new InvalidTokenException();
    }
  }

  public void revokeToken(UserId userId) throws InvalidTokenException {
    try {
      firebaseAuth.revokeRefreshTokens(userId.value());
    } catch (FirebaseAuthException e) {
      AuthErrorCode authErrorCode = e.getAuthErrorCode();

      if (AuthErrorCode.REVOKED_ID_TOKEN.equals(authErrorCode)) {
        // Token already revoked. Do nothing
        return;
      }
      if (AuthErrorCode.USER_NOT_FOUND.equals(authErrorCode)) {
        // User does not exist. Token does not require to be revoked
        return;
      }

      throw new InvalidTokenException();
    }
  }

  public void removeUser(UserId userId) throws UserNotFoundException {
    try {
      firebaseAuth.deleteUser(userId.value());
    } catch (IllegalArgumentException e) {
      logger.error("Cannot remove Firebase user with invalid user ID");
      throw new UserNotFoundException(userId);
    } catch (FirebaseAuthException e) {
      AuthErrorCode authErrorCode = e.getAuthErrorCode();

      if (AuthErrorCode.USER_NOT_FOUND.equals(authErrorCode)) {
        // User does not exist. Do nothing
      }
    }
  }

  public Map<String, Object> getClaimsForUser(UserId userId) throws UserNotFoundException {
    return getUser(userId).getCustomClaims();
  }

  public void assignClaimsToUser(UserId userId, Map<String, Object> claims)
      throws UserNotFoundException, TokenCustomizationException {
    if (claims == null) {
      throw new TokenCustomizationException(userId);
    }

    // Merge old claims with the new ones
    Map<String, Object> currentClaims = getUser(userId).getCustomClaims();
    Map<String, Object> mergedClaims =
        (currentClaims != null) ? new HashMap<>(currentClaims) : new HashMap<>();
    mergedClaims.putAll(claims);

    try {
      firebaseAuth.setCustomUserClaims(userId.value(), mergedClaims);
    } catch (FirebaseAuthException e) {
      logger.error("Could not assign clamis to user {}", userId);
      throw new TokenCustomizationException(userId);
    }
  }
}
