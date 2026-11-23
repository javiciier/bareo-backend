/*
 * Copyright (c) 2026 Bareo. All rights reserved.
 *
 * This software is the proprietary and confidential property of the author.
 * Unauthorized copying, distribution, or use is strictly prohibited.
 */
package com.corusoft.bareo.bareobackend.testing.mocking.firebase;

import com.corusoft.bareo.bareobackend.domain.entity.user.User;

import com.google.firebase.auth.AuthErrorCode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.auth.UserMetadata;
import com.google.firebase.auth.UserRecord;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

public class FirebaseAuthMock {

  private static FirebaseAuth firebaseAuth;

  public static FirebaseAuth initializeMock() {
    firebaseAuth = mock(FirebaseAuth.class);
    return firebaseAuth;
  }

  public static void resetMock() {
    if (firebaseAuth == null) {
      return;
    }

    reset(firebaseAuth);
  }

  public static FirebaseAuthException mockFirebaseAuthException() {
    return mock(FirebaseAuthException.class);
  }

  public static FirebaseAuthException mockFirebaseAuthException(AuthErrorCode errorCode) {
    FirebaseAuthException mockedException = mockFirebaseAuthException();
    when(mockedException.getAuthErrorCode()).thenReturn(errorCode);

    return mockedException;
  }

  public static FirebaseToken mockFirebaseToken() {
    return mock(FirebaseToken.class);
  }

  public static FirebaseToken mockFirebaseToken(User expectedUser) {
    FirebaseToken mockedToken = mockFirebaseToken();
    when(mockedToken.getUid()).thenReturn(expectedUser.getId().value());

    return mockedToken;
  }

  public static UserRecord mockUserRecord() {
    return mock(UserRecord.class);
  }

  public static UserRecord mockUserRecord(User expectedUser) {
    UserRecord mockedUserRecord = mockUserRecord();

    UserMetadata mockedUserMetadata = mockUserMetadata(expectedUser);
    when(mockedUserRecord.getUserMetadata()).thenReturn(mockedUserMetadata);

    when(mockedUserRecord.getUid()).thenReturn(expectedUser.getId().value());
    when(mockedUserRecord.getEmail()).thenReturn(expectedUser.getEmail().value());
    when(mockedUserRecord.getPhotoUrl()).thenReturn(expectedUser.getAvatar().value().toString());

    return mockedUserRecord;
  }

  public static UserMetadata mockUserMetadata() {
    return mock(UserMetadata.class);
  }

  public static UserMetadata mockUserMetadata(User expectedUser) {
    UserMetadata mockedUserMetadata = mockUserMetadata();
    when(mockedUserMetadata.getCreationTimestamp())
        .thenReturn(expectedUser.getCreatedAt().toEpochMilli());

    return mockedUserMetadata;
  }

  // region createUser

  public static void mockCreateUser(UserRecord expectedUserRecord) throws FirebaseAuthException {
    doReturn(expectedUserRecord).when(firebaseAuth).createUser(any(UserRecord.CreateRequest.class));
  }

  public static void mockCreateUser(FirebaseAuthException exception) throws FirebaseAuthException {
    doThrow(exception).when(firebaseAuth).createUser(any(UserRecord.CreateRequest.class));
  }

  // endregion createUser

  // region getUser

  public static void mockGetUser(UserRecord expectedUserRecord) throws FirebaseAuthException {
    doReturn(expectedUserRecord).when(firebaseAuth).getUser(any(String.class));
  }

  public static void mockGetUser(FirebaseAuthException exception) throws FirebaseAuthException {
    doThrow(exception).when(firebaseAuth).getUser(any(String.class));
  }

  // endregion getUser

  // region verifyIdToken

  public static void mockVerifyIdToken(FirebaseToken expectedFirebaseToken)
      throws FirebaseAuthException {
    doReturn(expectedFirebaseToken)
        .when(firebaseAuth)
        .verifyIdToken(any(String.class), anyBoolean());
  }

  public static void mockVerifyIdToken(FirebaseAuthException exception)
      throws FirebaseAuthException {
    doThrow(exception).when(firebaseAuth).verifyIdToken(any(String.class), anyBoolean());
  }

  // endregion verifyIdToken

  // region setCustomUserClaims

  public static void mockSetCustomUserClaims() throws FirebaseAuthException {
    doNothing().when(firebaseAuth).setCustomUserClaims(any(String.class), anyMap());
  }

  public static void mockSetCustomUserClaims(FirebaseAuthException exception)
      throws FirebaseAuthException {
    doThrow(exception).when(firebaseAuth).setCustomUserClaims(any(String.class), anyMap());
  }

  // endregion setCustomUserClaims

  // region revokeRefreshTokens

  public static void mockRevokeRefreshTokens() throws FirebaseAuthException {
    doNothing().when(firebaseAuth).revokeRefreshTokens(any(String.class));
  }

  public static void mockRevokeRefreshTokens(FirebaseAuthException exception)
      throws FirebaseAuthException {
    doThrow(exception).when(firebaseAuth).revokeRefreshTokens(any(String.class));
  }

  // endregion revokeRefreshTokens

  // region deleteUser

  public static void mockDeleteUser() throws FirebaseAuthException {
    doNothing().when(firebaseAuth).deleteUser(any(String.class));
  }

  public static void mockDeleteUser(FirebaseAuthException exception) throws FirebaseAuthException {
    doThrow(exception).when(firebaseAuth).deleteUser(any(String.class));
  }

  // endregion deleteUser

}
