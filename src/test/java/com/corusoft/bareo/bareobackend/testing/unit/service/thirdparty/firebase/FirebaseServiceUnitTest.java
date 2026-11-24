/*
 * Copyright (c) 2026 Bareo. All rights reserved.
 *
 * This software is the proprietary and confidential property of the author.
 * Unauthorized copying, distribution, or use is strictly prohibited.
 */
package com.corusoft.bareo.bareobackend.testing.unit.service.thirdparty.firebase;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.corusoft.bareo.bareobackend.domain.entity.user.User;
import com.corusoft.bareo.bareobackend.domain.entity.user.vo.UserId;
import com.corusoft.bareo.bareobackend.domain.exception.auth.ExpiredTokenException;
import com.corusoft.bareo.bareobackend.domain.exception.auth.InvalidTokenException;
import com.corusoft.bareo.bareobackend.domain.exception.auth.TokenCustomizationException;
import com.corusoft.bareo.bareobackend.domain.exception.user.UserNotFoundException;
import com.corusoft.bareo.bareobackend.infrastructure.thirdparty.firebase.FirebaseService;
import com.corusoft.bareo.bareobackend.testing.config.annotation.UnitTest;
import com.corusoft.bareo.bareobackend.testing.dataset.TestUserDataset;
import com.corusoft.bareo.bareobackend.testing.mocking.firebase.FirebaseAuthMock;

import com.google.firebase.auth.AuthErrorCode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.auth.UserRecord;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static com.corusoft.bareo.bareobackend.application.usecase.authentication.GoogleLoginUseCase.DEFAULT_NEW_USER_CLAIMS;
import static com.corusoft.bareo.bareobackend.infrastructure.config.security.TokenClaim.ROLE_CLAIM;
import static com.corusoft.bareo.bareobackend.testing.dataset.TestUserDataset.RAW_JWT_TOKEN;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

@UnitTest
public class FirebaseServiceUnitTest {
  private final User testUser = TestUserDataset.getTestUser();
  UserId testUserId = testUser.getId();

  private FirebaseAuth firebaseAuthMock;

  @BeforeAll
  public void setUp() {
    firebaseAuthMock = FirebaseAuthMock.initializeMock();
  }

  @AfterEach
  public void resetMocks() {
    FirebaseAuthMock.resetMock();
  }

  @Nested
  class GetUser {

    @Test
    void when_getUser_then_ok() throws FirebaseAuthException {
      // Arrange
      UserRecord userRecord = FirebaseAuthMock.mockUserRecord();
      FirebaseAuthMock.mockGetUser(userRecord);

      // Act
      FirebaseService firebaseService = new FirebaseService(firebaseAuthMock);
      UserRecord result = firebaseService.getUser(testUserId);

      // Assert
      assertAll(() -> assertNotNull(result), () -> assertEquals(userRecord, result));
    }

    @Test
    void when_getUser_nonExistentUser_then_fail() throws FirebaseAuthException {
      // Arrange
      FirebaseAuthException firebaseAuthException = FirebaseAuthMock.mockFirebaseAuthException();
      FirebaseAuthMock.mockGetUser(firebaseAuthException);

      // Act
      FirebaseService firebaseService = new FirebaseService(firebaseAuthMock);

      // Assert
      assertThrows(UserNotFoundException.class, () -> firebaseService.getUser(testUserId));
    }
  }

  @Nested
  class VerifyToken {

    @Test
    void when_verifyToken_then_ok() throws FirebaseAuthException {
      // Arrange
      FirebaseToken firebaseTokenMock = FirebaseAuthMock.mockFirebaseToken(testUser);
      FirebaseAuthMock.mockVerifyIdToken(firebaseTokenMock);

      // Act
      FirebaseService firebaseService = new FirebaseService(firebaseAuthMock);
      FirebaseToken result = firebaseService.verifyToken(RAW_JWT_TOKEN);

      // Assert
      assertAll(
          () -> assertNotNull(result),
          () -> assertEquals(firebaseTokenMock.getUid(), testUser.getId().value()),
          () -> assertEquals(firebaseTokenMock.getEmail(), testUser.getEmail().value()));
    }

    @Test
    void when_verifyToken_nonExistentUser_then_fail() throws FirebaseAuthException {
      // Arrange
      FirebaseAuthException firebaseAuthException =
          FirebaseAuthMock.mockFirebaseAuthException(AuthErrorCode.USER_NOT_FOUND);
      FirebaseAuthMock.mockVerifyIdToken(firebaseAuthException);

      // Act
      FirebaseService firebaseService = new FirebaseService(firebaseAuthMock);

      // Assert
      assertThrows(
          IllegalArgumentException.class, () -> firebaseService.verifyToken(RAW_JWT_TOKEN));
    }

    @Test
    void when_verifyToken_expiredToken_then_fail() throws FirebaseAuthException {
      // Arrange
      FirebaseAuthException firebaseAuthException =
          FirebaseAuthMock.mockFirebaseAuthException(AuthErrorCode.EXPIRED_ID_TOKEN);
      FirebaseAuthMock.mockVerifyIdToken(firebaseAuthException);

      // Act
      FirebaseService firebaseService = new FirebaseService(firebaseAuthMock);

      // Assert
      assertThrows(ExpiredTokenException.class, () -> firebaseService.verifyToken(RAW_JWT_TOKEN));
    }

    @Test
    void when_verifyToken_invalidToken_then_fail() throws FirebaseAuthException {
      // Arrange
      FirebaseAuthException firebaseAuthException =
          FirebaseAuthMock.mockFirebaseAuthException(AuthErrorCode.INVALID_ID_TOKEN);
      FirebaseAuthMock.mockVerifyIdToken(firebaseAuthException);

      // Act
      FirebaseService firebaseService = new FirebaseService(firebaseAuthMock);

      // Assert
      assertThrows(InvalidTokenException.class, () -> firebaseService.verifyToken(RAW_JWT_TOKEN));
    }

    @Test
    void when_verifyToken_revokedToken_then_fail() throws FirebaseAuthException {
      // Arrange
      FirebaseAuthException firebaseAuthException =
          FirebaseAuthMock.mockFirebaseAuthException(AuthErrorCode.REVOKED_ID_TOKEN);
      FirebaseAuthMock.mockVerifyIdToken(firebaseAuthException);

      // Act
      FirebaseService firebaseService = new FirebaseService(firebaseAuthMock);

      // Assert
      assertThrows(InvalidTokenException.class, () -> firebaseService.verifyToken(RAW_JWT_TOKEN));
    }
  }

  @Nested
  class RevokeToken {

    @Test
    void when_revokeToken_then_ok() throws FirebaseAuthException {
      // Arrange
      FirebaseAuthMock.mockRevokeRefreshTokens();

      // Act
      FirebaseService firebaseService = new FirebaseService(firebaseAuthMock);

      // Assert
      assertDoesNotThrow(() -> firebaseService.revokeToken(testUserId));
    }

    @Test
    void when_revokeToken_nonExistentUser_then_fail() throws FirebaseAuthException {
      // Arrange
      FirebaseAuthMock.mockRevokeRefreshTokens();

      // Act
      FirebaseService firebaseService = new FirebaseService(firebaseAuthMock);

      // Assert
      assertDoesNotThrow(() -> firebaseService.revokeToken(testUserId));
    }

    @Test
    void when_revokeToken_revokedToken_then_fail() throws FirebaseAuthException {
      // Arrange
      FirebaseAuthMock.mockRevokeRefreshTokens();

      // Act
      FirebaseService firebaseService = new FirebaseService(firebaseAuthMock);

      // Assert
      assertDoesNotThrow(() -> firebaseService.revokeToken(testUserId));
    }

    @Test
    void when_revokeToken_invalidToken_then_fail() throws FirebaseAuthException {
      // Arrange
      FirebaseAuthException firebaseAuthException =
          FirebaseAuthMock.mockFirebaseAuthException(AuthErrorCode.INVALID_ID_TOKEN);
      FirebaseAuthMock.mockRevokeRefreshTokens(firebaseAuthException);

      // Act
      FirebaseService firebaseService = new FirebaseService(firebaseAuthMock);

      // Assert
      assertThrows(InvalidTokenException.class, () -> firebaseService.revokeToken(testUserId));
    }
  }

  @Nested
  class RemoveUser {

    @Test
    void when_removeUser_then_ok() throws FirebaseAuthException {
      // Arrange
      FirebaseAuthMock.mockDeleteUser();

      // Act
      FirebaseService firebaseService = new FirebaseService(firebaseAuthMock);

      // Assert
      assertDoesNotThrow(() -> firebaseService.removeUser(testUserId));
    }

    @Test
    void when_removeUser_nullUserId_then_fail() throws FirebaseAuthException {
      // Arrange
      IllegalArgumentException illegalArgumentException = mock(IllegalArgumentException.class);
      FirebaseAuthMock.mockDeleteUser(illegalArgumentException);

      // Act
      FirebaseService firebaseService = new FirebaseService(firebaseAuthMock);

      // Assert
      assertThrows(NullPointerException.class, () -> firebaseService.removeUser(null));
    }

    @Test
    void when_removeUser_blankUserId_then_fail() throws FirebaseAuthException {
      // Arrange
      IllegalArgumentException illegalArgumentException = mock(IllegalArgumentException.class);
      FirebaseAuthMock.mockDeleteUser(illegalArgumentException);

      // Act
      FirebaseService firebaseService = new FirebaseService(firebaseAuthMock);

      // Assert
      assertAll(
          () ->
              assertThrows(
                  IllegalArgumentException.class, () -> firebaseService.removeUser(new UserId(""))),
          () ->
              assertThrows(
                  IllegalArgumentException.class,
                  () -> firebaseService.removeUser(new UserId(" "))));
    }

    @Test
    void when_removeUser_nonExistentUserInFirebase_then_ok() throws FirebaseAuthException {
      // Arrange
      FirebaseAuthException firebaseAuthException =
          FirebaseAuthMock.mockFirebaseAuthException(AuthErrorCode.USER_NOT_FOUND);
      FirebaseAuthMock.mockDeleteUser(firebaseAuthException);

      // Act
      FirebaseService firebaseService = new FirebaseService(firebaseAuthMock);

      // Assert
      assertDoesNotThrow(() -> firebaseService.removeUser(testUserId));
    }

    @Test
    void when_removeUser_errorRemovingUserInFirebase_then_fail() throws FirebaseAuthException {
      // Arrange
      IllegalArgumentException illegalArgumentException = mock(IllegalArgumentException.class);
      FirebaseAuthMock.mockDeleteUser(illegalArgumentException);

      // Act
      FirebaseService firebaseService = new FirebaseService(firebaseAuthMock);

      // Assert
      assertThrows(UserNotFoundException.class, () -> firebaseService.removeUser(testUserId));
    }
  }

  @Nested
  class GetClaimsForUser {

    @Test
    void when_getClaimsForUser_then_ok() throws FirebaseAuthException {
      // Arrange
      UserRecord mockedUserRecord =
          FirebaseAuthMock.mockUserRecord(testUser, DEFAULT_NEW_USER_CLAIMS);
      FirebaseAuthMock.mockGetUser(mockedUserRecord);

      // Act
      FirebaseService firebaseService = new FirebaseService(firebaseAuthMock);

      // Assert
      assertAll(
          () -> assertDoesNotThrow(() -> firebaseService.getClaimsForUser(testUserId)),
          () -> assertNotNull(firebaseService.getClaimsForUser(testUserId)),
          () ->
              assertEquals(DEFAULT_NEW_USER_CLAIMS, firebaseService.getClaimsForUser(testUserId)));
    }

    @Test
    void when_getClaimsForUser_emptyClaims_then_fail() throws FirebaseAuthException {
      // Arrange
      UserRecord mockedUserRecord = FirebaseAuthMock.mockUserRecord(testUser);
      FirebaseAuthMock.mockGetUser(mockedUserRecord);

      // Act
      FirebaseService firebaseService = new FirebaseService(firebaseAuthMock);

      // Assert
      assertAll(
          () -> assertDoesNotThrow(() -> firebaseService.getClaimsForUser(testUserId)),
          () -> assertTrue(firebaseService.getClaimsForUser(testUserId).isEmpty()));
    }

    @Test
    void when_getClaimsForUser_nonExistentUser_then_fail() throws FirebaseAuthException {
      // Arrange
      FirebaseAuthException firebaseAuthException =
          FirebaseAuthMock.mockFirebaseAuthException(AuthErrorCode.USER_NOT_FOUND);
      FirebaseAuthMock.mockGetUser(firebaseAuthException);

      // Act
      FirebaseService firebaseService = new FirebaseService(firebaseAuthMock);

      // Assert
      assertThrows(UserNotFoundException.class, () -> firebaseService.getClaimsForUser(testUserId));
    }
  }

  @Nested
  class AssignClaimsToUser {
    private final int MAX_CUSTOM_CLAIMS_ALLOWED_SIZE = 1000;

    @Test
    void when_assignClaimsToUser_then_ok() throws FirebaseAuthException {
      // Arrange
      UserRecord userRecordMock =
          FirebaseAuthMock.mockUserRecord(testUser, DEFAULT_NEW_USER_CLAIMS);
      FirebaseAuthMock.mockGetUser(userRecordMock);
      FirebaseAuthMock.mockSetCustomUserClaims();

      // Act
      FirebaseService firebaseService = new FirebaseService(firebaseAuthMock);

      // Assert
      assertDoesNotThrow(
          () -> firebaseService.assignClaimsToUser(testUserId, DEFAULT_NEW_USER_CLAIMS));
    }

    @Test
    void when_assignClaimsToUser_newClaims_then_ok() throws FirebaseAuthException {
      // Arrange
      UserRecord userRecordMock =
          FirebaseAuthMock.mockUserRecord(testUser, DEFAULT_NEW_USER_CLAIMS);
      FirebaseAuthMock.mockGetUser(userRecordMock);
      FirebaseAuthMock.mockSetCustomUserClaims();
      Map<String, Object> newClaims = new HashMap<>();
      newClaims.put("newClaim", "newClaimValue");

      // Act
      FirebaseService firebaseService = new FirebaseService(firebaseAuthMock);

      // Assert
      assertDoesNotThrow(() -> firebaseService.assignClaimsToUser(testUserId, newClaims));
    }

    @Test
    void when_assignClaimsToUser_updateEmptyUserClaimsMap_then_ok() throws FirebaseAuthException {
      // Arrange
      UserRecord userRecordMock = FirebaseAuthMock.mockUserRecord(testUser, Collections.emptyMap());
      FirebaseAuthMock.mockGetUser(userRecordMock);
      FirebaseAuthMock.mockSetCustomUserClaims();
      Map<String, Object> updatedClaimsMap = new HashMap<>();
      updatedClaimsMap.put(ROLE_CLAIM.getClaimName(), "newClaimValue");

      // Act
      FirebaseService firebaseService = new FirebaseService(firebaseAuthMock);

      // Assert
      assertDoesNotThrow(() -> firebaseService.assignClaimsToUser(testUserId, updatedClaimsMap));
    }

    @Test
    void when_assignClaimsToUser_updateExistentClaim_then_ok() throws FirebaseAuthException {
      // Arrange
      UserRecord userRecordMock =
          FirebaseAuthMock.mockUserRecord(testUser, DEFAULT_NEW_USER_CLAIMS);
      FirebaseAuthMock.mockGetUser(userRecordMock);
      FirebaseAuthMock.mockSetCustomUserClaims();
      Map<String, Object> updatedClaimsMap = new HashMap<>(Map.copyOf(DEFAULT_NEW_USER_CLAIMS));
      updatedClaimsMap.put(ROLE_CLAIM.getClaimName(), "newClaimValue");

      // Act
      FirebaseService firebaseService = new FirebaseService(firebaseAuthMock);

      // Assert
      assertDoesNotThrow(() -> firebaseService.assignClaimsToUser(testUserId, updatedClaimsMap));
    }

    @Test
    void when_assignClaimsToUser_nonExistentUser_then_ok() throws FirebaseAuthException {
      // Arrange
      FirebaseAuthException firebaseAuthException =
          FirebaseAuthMock.mockFirebaseAuthException(AuthErrorCode.USER_NOT_FOUND);
      FirebaseAuthMock.mockGetUser(firebaseAuthException);

      // Act
      FirebaseService firebaseService = new FirebaseService(firebaseAuthMock);

      // Assert
      assertThrows(
          UserNotFoundException.class,
          () -> firebaseService.assignClaimsToUser(testUserId, DEFAULT_NEW_USER_CLAIMS));
    }

    @Test
    void when_assignClaimsToUser_nullNewClaims_then_ok() throws FirebaseAuthException {
      // Arrange
      UserRecord userRecordMock =
          FirebaseAuthMock.mockUserRecord(testUser, DEFAULT_NEW_USER_CLAIMS);
      FirebaseAuthMock.mockGetUser(userRecordMock);

      // Act
      FirebaseService firebaseService = new FirebaseService(firebaseAuthMock);

      // Assert
      assertThrows(
          TokenCustomizationException.class,
          () -> firebaseService.assignClaimsToUser(testUserId, null));
    }

    @Test
    void when_assignClaimsToUser_emptyNewClaims_then_ok() throws FirebaseAuthException {
      // Arrange
      UserRecord userRecordMock =
          FirebaseAuthMock.mockUserRecord(testUser, DEFAULT_NEW_USER_CLAIMS);
      FirebaseAuthMock.mockGetUser(userRecordMock);

      // Act
      FirebaseService firebaseService = new FirebaseService(firebaseAuthMock);

      // Assert
      assertDoesNotThrow(
          () -> firebaseService.assignClaimsToUser(testUserId, Collections.emptyMap()));
    }

    @Test
    void when_assignClaimsToUser_tooLargeClaims_then_ok() throws FirebaseAuthException {
      // Arrange
      Map<String, Object> tooLargeClaimsMap = new HashMap<>();
      tooLargeClaimsMap.put("claim", "a".repeat(MAX_CUSTOM_CLAIMS_ALLOWED_SIZE));
      UserRecord userRecordMock = FirebaseAuthMock.mockUserRecord(testUser, tooLargeClaimsMap);
      FirebaseAuthMock.mockGetUser(userRecordMock);
      FirebaseAuthException firebaseAuthException =
          FirebaseAuthMock.mockFirebaseAuthException(AuthErrorCode.INVALID_ID_TOKEN);
      FirebaseAuthMock.mockSetCustomUserClaims(firebaseAuthException);

      // Act
      FirebaseService firebaseService = new FirebaseService(firebaseAuthMock);

      // Assert
      assertThrows(
          TokenCustomizationException.class,
          () -> firebaseService.assignClaimsToUser(testUserId, tooLargeClaimsMap));
    }
  }
}
