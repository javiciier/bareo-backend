/*
 * Copyright (c) 2026 Bareo. All rights reserved.
 *
 * This software is the proprietary and confidential property of the author.
 * Unauthorized copying, distribution, or use is strictly prohibited.
 */
package com.corusoft.bareo.bareobackend.infrastructure.service;

import java.net.URI;
import java.time.Instant;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import com.corusoft.bareo.bareobackend.application.service.auth.AuthenticationService;
import com.corusoft.bareo.bareobackend.domain.entity.user.User;
import com.corusoft.bareo.bareobackend.domain.entity.user.vo.UserAvatar;
import com.corusoft.bareo.bareobackend.domain.entity.user.vo.UserId;
import com.corusoft.bareo.bareobackend.domain.exception.user.UserNotFoundException;
import com.corusoft.bareo.bareobackend.domain.repository.UserRepository;
import com.corusoft.bareo.bareobackend.domain.shared.vo.EmailVO;
import com.corusoft.bareo.bareobackend.infrastructure.thirdparty.firebase.FirebaseService;

import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.auth.UserRecord;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.corusoft.bareo.bareobackend.domain.shared.enums.UserRole.ROLE_BASIC;
import static com.corusoft.bareo.bareobackend.infrastructure.config.security.TokenClaim.ROLE_CLAIM;

@Slf4j
@Service
public class AuthenticationServiceImpl implements AuthenticationService {

  private final FirebaseService firebaseService;
  private final UserRepository userRepository;

  public AuthenticationServiceImpl(FirebaseService firebaseService, UserRepository userRepository) {
    this.firebaseService = firebaseService;
    this.userRepository = userRepository;
  }

  @Override
  @Transactional
  public User authenticateUserWithToken(String token) {
    UserId userId;
    UserRecord userData;

    // Validate token
    try {
      FirebaseToken firebaseToken = firebaseService.verifyToken(token);
      userId = new UserId(firebaseToken.getUid());
      userData = firebaseService.getUser(userId);
    } catch (UserNotFoundException e) {
      logger.error("Could not authenticate user using token", e);
      throw e;
    }

    // Check if user exists in database. Else register it
    return userRepository.findById(userId).orElseGet(() -> registerUser(userData, userId));
  }

  public User registerUser(UserRecord userRecord, UserId userId) {
    EmailVO email = new EmailVO(userRecord.getEmail());
    UserAvatar avatar = new UserAvatar(URI.create(userRecord.getPhotoUrl()));
    Instant creationDate =
        Instant.ofEpochMilli(userRecord.getUserMetadata().getCreationTimestamp());
    Map<String, Object> newUserClaim = Map.of(ROLE_CLAIM.getClaimName(), ROLE_BASIC.name());
    firebaseService.assignClaimsToUser(userId, newUserClaim);

    var newUser =
        User.builder()
            .id(userId)
            .email(email)
            .avatar(avatar)
            .createdAt(creationDate)
            .updatedAt(Instant.now())
            .build();

    return userRepository.save(newUser);
  }
}
