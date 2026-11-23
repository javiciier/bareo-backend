/*
 * Copyright (c) 2026 Bareo. All rights reserved.
 *
 * This software is the proprietary and confidential property of the author.
 * Unauthorized copying, distribution, or use is strictly prohibited.
 */
package com.corusoft.bareo.bareobackend.testing.unit.usecase.authentication;

import java.util.Collections;

import com.corusoft.bareo.bareobackend.application.service.authentication.AuthenticationService;
import com.corusoft.bareo.bareobackend.application.service.user.UserService;
import com.corusoft.bareo.bareobackend.application.usecase.authentication.GoogleLoginUseCase;
import com.corusoft.bareo.bareobackend.application.usecase.authentication.command.GoogleLoginUseCaseCommand;
import com.corusoft.bareo.bareobackend.application.usecase.authentication.result.GoogleLoginUseCaseResult;
import com.corusoft.bareo.bareobackend.domain.entity.user.User;
import com.corusoft.bareo.bareobackend.domain.entity.user.vo.UserId;
import com.corusoft.bareo.bareobackend.domain.exception.auth.ExpiredTokenException;
import com.corusoft.bareo.bareobackend.domain.exception.auth.InvalidTokenException;
import com.corusoft.bareo.bareobackend.domain.exception.user.UserNotFoundException;
import com.corusoft.bareo.bareobackend.infrastructure.thirdparty.firebase.FirebaseService;
import com.corusoft.bareo.bareobackend.testing.config.annotation.UnitTest;
import com.corusoft.bareo.bareobackend.testing.dataset.TestUserDataset;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static com.corusoft.bareo.bareobackend.application.usecase.authentication.GoogleLoginUseCase.DEFAULT_NEW_USER_CLAIMS;
import static com.corusoft.bareo.bareobackend.testing.dataset.TestUserDataset.RAW_JWT_TOKEN;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
public class GoogleLoginUseCaseUnitTest {
  private final User testUser = TestUserDataset.getTestUser();

  @Mock private AuthenticationService authServiceMock;
  @Mock private UserService userServiceMock;
  @Mock private FirebaseService firebaseServiceMock;

  private GoogleLoginUseCase loginUseCase;

  @AfterEach
  public void resetMocks() {
    loginUseCase = null;
  }

  @Test
  public void when_login_then_ok() {
    // Arrange
    when(authServiceMock.authenticateUserWithToken(any(String.class))).thenReturn(testUser);
    when(firebaseServiceMock.getClaimsForUser(any(UserId.class)))
        .thenReturn(DEFAULT_NEW_USER_CLAIMS);
    when(userServiceMock.existsById(any(UserId.class))).thenReturn(true);
    when(userServiceMock.findById(any(UserId.class))).thenReturn(testUser);

    // Act
    GoogleLoginUseCaseCommand command = new GoogleLoginUseCaseCommand(RAW_JWT_TOKEN);
    loginUseCase = new GoogleLoginUseCase(authServiceMock, userServiceMock, firebaseServiceMock);
    GoogleLoginUseCaseResult result = loginUseCase.execute(command);

    // Assert
    verify(authServiceMock).authenticateUserWithToken(RAW_JWT_TOKEN);
    verify(firebaseServiceMock).getClaimsForUser(testUser.getId());
    verify(userServiceMock).existsById(testUser.getId());
    assertAll(
        () -> assertNotNull(result),
        () -> assertNotNull(result.authenticatedUser()),
        () -> assertEquals(testUser, result.authenticatedUser()));
  }

  @Test
  public void when_loginNonExistenUserInFirebase_then_fail() {
    // Arrange
    when(authServiceMock.authenticateUserWithToken(any(String.class)))
        .thenThrow(new UserNotFoundException(testUser.getId()));

    // Act
    GoogleLoginUseCaseCommand command = new GoogleLoginUseCaseCommand(RAW_JWT_TOKEN);
    loginUseCase = new GoogleLoginUseCase(authServiceMock, userServiceMock, firebaseServiceMock);

    // Assert
    assertThrows(UserNotFoundException.class, () -> loginUseCase.execute(command));
    verify(authServiceMock).authenticateUserWithToken(RAW_JWT_TOKEN);
  }

  @Test
  public void when_loginUserExistentInFirebase_and_userNotExistentInDatabase_then_persistUser() {
    // Arrange
    when(authServiceMock.authenticateUserWithToken(any(String.class))).thenReturn(testUser);
    when(firebaseServiceMock.getClaimsForUser(any(UserId.class)))
        .thenReturn(DEFAULT_NEW_USER_CLAIMS);
    when(userServiceMock.existsById(any(UserId.class))).thenReturn(false);

    // Act
    GoogleLoginUseCaseCommand command = new GoogleLoginUseCaseCommand(RAW_JWT_TOKEN);
    loginUseCase = new GoogleLoginUseCase(authServiceMock, userServiceMock, firebaseServiceMock);

    // Assert
    assertDoesNotThrow(() -> loginUseCase.execute(command));
    verify(userServiceMock).existsById(testUser.getId());
    verify(userServiceMock).saveUser(testUser);
  }

  @Test
  public void when_userClaimsAreEmpty_then_fail() {
    // Arrange
    when(authServiceMock.authenticateUserWithToken(any(String.class))).thenReturn(testUser);
    when(firebaseServiceMock.getClaimsForUser(any(UserId.class)))
        .thenReturn(Collections.emptyMap());

    // Act
    GoogleLoginUseCaseCommand command = new GoogleLoginUseCaseCommand(RAW_JWT_TOKEN);
    loginUseCase = new GoogleLoginUseCase(authServiceMock, userServiceMock, firebaseServiceMock);

    // Assert
    assertDoesNotThrow(() -> loginUseCase.execute(command));
    verify(firebaseServiceMock).getClaimsForUser(testUser.getId());
    verify(firebaseServiceMock).assignClaimsToUser(testUser.getId(), DEFAULT_NEW_USER_CLAIMS);
  }

  @Test
  public void when_savingUserInDatabaseFails_then_fail() {
    // Arrange
    when(authServiceMock.authenticateUserWithToken(any(String.class))).thenReturn(testUser);
    when(firebaseServiceMock.getClaimsForUser(any(UserId.class)))
        .thenReturn(DEFAULT_NEW_USER_CLAIMS);
    when(userServiceMock.saveUser(any(User.class))).thenThrow(new RuntimeException());

    // Act
    GoogleLoginUseCaseCommand command = new GoogleLoginUseCaseCommand(RAW_JWT_TOKEN);
    loginUseCase = new GoogleLoginUseCase(authServiceMock, userServiceMock, firebaseServiceMock);

    // Assert
    assertThrows(RuntimeException.class, () -> loginUseCase.execute(command));
    verify(userServiceMock).saveUser(testUser);
    verify(userServiceMock).deleteUser(testUser.getId());
    verify(firebaseServiceMock).removeUser(testUser.getId());
  }

  @Test
  public void when_userHasInvalidToken_then_fail() {
    // Arrange
    when(authServiceMock.authenticateUserWithToken(any(String.class)))
        .thenThrow(new InvalidTokenException());

    // Act
    GoogleLoginUseCaseCommand command = new GoogleLoginUseCaseCommand(RAW_JWT_TOKEN);
    loginUseCase = new GoogleLoginUseCase(authServiceMock, userServiceMock, firebaseServiceMock);

    // Assert
    assertThrows(InvalidTokenException.class, () -> loginUseCase.execute(command));
    verify(authServiceMock).authenticateUserWithToken(RAW_JWT_TOKEN);
  }

  @Test
  public void when_userHasExpiredToken_then_fail() {
    // Arrange
    when(authServiceMock.authenticateUserWithToken(any(String.class)))
        .thenThrow(new ExpiredTokenException());

    // Act
    GoogleLoginUseCaseCommand command = new GoogleLoginUseCaseCommand(RAW_JWT_TOKEN);
    loginUseCase = new GoogleLoginUseCase(authServiceMock, userServiceMock, firebaseServiceMock);

    // Assert
    assertThrows(ExpiredTokenException.class, () -> loginUseCase.execute(command));
    verify(authServiceMock).authenticateUserWithToken(RAW_JWT_TOKEN);
  }
}
