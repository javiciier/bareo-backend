/*
 * Copyright (c) 2026 Bareo. All rights reserved.
 *
 * This software is the proprietary and confidential property of the author.
 * Unauthorized copying, distribution, or use is strictly prohibited.
 */
package com.corusoft.bareo.bareobackend.testing.unit.service.authentication;

import com.corusoft.bareo.bareobackend.application.service.authentication.AuthenticationService;
import com.corusoft.bareo.bareobackend.domain.entity.user.User;
import com.corusoft.bareo.bareobackend.domain.entity.user.vo.UserId;
import com.corusoft.bareo.bareobackend.domain.exception.auth.ExpiredTokenException;
import com.corusoft.bareo.bareobackend.domain.exception.auth.InvalidTokenException;
import com.corusoft.bareo.bareobackend.infrastructure.thirdparty.firebase.FirebaseService;
import com.corusoft.bareo.bareobackend.testing.config.annotation.UnitTest;
import com.corusoft.bareo.bareobackend.testing.dataset.TestUserDataset;
import com.corusoft.bareo.bareobackend.testing.mocking.firebase.FirebaseAuthMock;

import com.google.firebase.auth.AuthErrorCode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.auth.UserRecord;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.mockito.Mock;

import static com.corusoft.bareo.bareobackend.testing.dataset.TestUserDataset.RAW_JWT_TOKEN;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@UnitTest
@TestInstance(Lifecycle.PER_CLASS)
public class AuthenticationServiceUnitTest {
  private final User testUser = TestUserDataset.getTestUser();

  @Mock private FirebaseService firebaseService;
  private FirebaseAuth firebaseAuthMock;

  @BeforeAll
  public void setUp() {
    firebaseAuthMock = FirebaseAuthMock.initializeMock();
  }

  @AfterAll
  public void tearDown() {
    FirebaseAuthMock.resetMock();
  }

  @BeforeEach
  public void mockDependencies() {
    firebaseService = new FirebaseService(firebaseAuthMock);
  }

  @Nested
  class AuthenticateUserWithToken {
    UserId testUserId = testUser.getId();

    @Test
    void when_authenticateUserWithToken_then_ok() throws Exception {
      // Arrange
      FirebaseToken firebaseTokenMock = FirebaseAuthMock.mockFirebaseToken(testUser);
      FirebaseAuthMock.mockVerifyIdToken(firebaseTokenMock);
      UserRecord userRecordMock = FirebaseAuthMock.mockUserRecord(testUser);
      when(firebaseService.getUser(testUserId)).thenReturn(userRecordMock);

      // Act
      AuthenticationService authService = new AuthenticationService(firebaseService);
      User result = authService.authenticateUserWithToken(RAW_JWT_TOKEN);

      // Assert
      assertAll(() -> assertNotNull(result), () -> assertEquals(testUser, result));
    }

    @Test
    void when_authenticateUserWithToken_nonExistentUserInFirebase_then_fail() throws Exception {
      // Arrange
      FirebaseToken firebaseTokenMock = FirebaseAuthMock.mockFirebaseToken(testUser);
      FirebaseAuthMock.mockVerifyIdToken(firebaseTokenMock);
      when(firebaseService.getUser(testUserId)).thenReturn(null);

      // Act
      AuthenticationService authService = new AuthenticationService(firebaseService);
      User result = authService.authenticateUserWithToken(RAW_JWT_TOKEN);

      // Assert
      assertAll(() -> assertNull(result));
    }

    @Test
    void when_authenticateUserWithToken_nonRegisteredUserInFirebase_then_fail() throws Exception {
      // Arrange
      FirebaseAuthException firebaseAuthException =
          FirebaseAuthMock.mockFirebaseAuthException(AuthErrorCode.USER_NOT_FOUND);
      FirebaseAuthMock.mockVerifyIdToken(firebaseAuthException);

      // Act
      AuthenticationService authService = new AuthenticationService(firebaseService);

      // Assert
      assertThrows(
          IllegalArgumentException.class,
          () -> authService.authenticateUserWithToken(RAW_JWT_TOKEN));
    }

    @Test
    void when_authenticateUserWithToken_expiredToken_then_fail() throws Exception {
      // Arrange
      FirebaseAuthException firebaseAuthException =
          FirebaseAuthMock.mockFirebaseAuthException(AuthErrorCode.EXPIRED_ID_TOKEN);
      FirebaseAuthMock.mockVerifyIdToken(firebaseAuthException);

      // Act
      AuthenticationService authService = new AuthenticationService(firebaseService);

      // Assert
      assertThrows(
          ExpiredTokenException.class, () -> authService.authenticateUserWithToken(RAW_JWT_TOKEN));
    }

    @Test
    void when_authenticateUserWithToken_invalidToken_then_fail() throws Exception {
      // Arrange
      FirebaseAuthException firebaseAuthException =
          FirebaseAuthMock.mockFirebaseAuthException(AuthErrorCode.INVALID_ID_TOKEN);
      FirebaseAuthMock.mockVerifyIdToken(firebaseAuthException);

      // Act
      AuthenticationService authService = new AuthenticationService(firebaseService);

      // Assert
      assertThrows(
          InvalidTokenException.class, () -> authService.authenticateUserWithToken(RAW_JWT_TOKEN));
    }

    @Test
    void when_authenticateUserWithToken_revokedToken_then_fail() throws Exception {
      // Arrange
      FirebaseAuthException firebaseAuthException =
          FirebaseAuthMock.mockFirebaseAuthException(AuthErrorCode.REVOKED_ID_TOKEN);
      FirebaseAuthMock.mockVerifyIdToken(firebaseAuthException);

      // Act
      AuthenticationService authService = new AuthenticationService(firebaseService);

      // Assert
      assertThrows(
          InvalidTokenException.class, () -> authService.authenticateUserWithToken(RAW_JWT_TOKEN));
    }
  }

  @Nested
  class RevokeAuthentication {
    UserId testUserId = testUser.getId();

    @Test
    void when_revokeAuthentication_then_ok() throws Exception {
      // Arrange
      FirebaseAuthMock.mockRevokeRefreshTokens();

      // Act
      AuthenticationService authService = new AuthenticationService(firebaseService);

      // Assert
      assertDoesNotThrow(() -> authService.revokeAuthentication(testUserId));
    }

    @Test
    void when_revokeAuthentication_revokedToken_then_ok() throws Exception {
      // Arrange
      FirebaseAuthException firebaseAuthException =
          FirebaseAuthMock.mockFirebaseAuthException(AuthErrorCode.REVOKED_ID_TOKEN);
      FirebaseAuthMock.mockRevokeRefreshTokens(firebaseAuthException);

      // Act
      AuthenticationService authService = new AuthenticationService(firebaseService);

      // Assert
      assertDoesNotThrow(() -> authService.revokeAuthentication(testUserId));
    }

    @Test
    void when_revokeAuthentication_nonExistentUserInFirebase_then_ok() throws Exception {
      // Arrange
      FirebaseAuthException firebaseAuthException =
          FirebaseAuthMock.mockFirebaseAuthException(AuthErrorCode.USER_NOT_FOUND);
      FirebaseAuthMock.mockRevokeRefreshTokens(firebaseAuthException);

      // Act
      AuthenticationService authService = new AuthenticationService(firebaseService);

      // Assert
      assertDoesNotThrow(() -> authService.revokeAuthentication(testUserId));
    }

    @Test
    void when_revokeAuthentication_malformedToken_then_fail() throws Exception {
      // Arrange
      FirebaseAuthException firebaseAuthException =
          FirebaseAuthMock.mockFirebaseAuthException(AuthErrorCode.INVALID_ID_TOKEN);
      FirebaseAuthMock.mockRevokeRefreshTokens(firebaseAuthException);

      // Act
      AuthenticationService authService = new AuthenticationService(firebaseService);

      // Assert
      assertThrows(InvalidTokenException.class, () -> authService.revokeAuthentication(testUserId));
    }
  }
}
