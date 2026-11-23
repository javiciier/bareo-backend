/*
 * Copyright (c) 2026 Bareo. All rights reserved.
 *
 * This software is the proprietary and confidential property of the author.
 * Unauthorized copying, distribution, or use is strictly prohibited.
 */
package com.corusoft.bareo.bareobackend.testing.unit.usecase.authentication;

import com.corusoft.bareo.bareobackend.application.service.authentication.AuthenticationService;
import com.corusoft.bareo.bareobackend.application.usecase.authentication.GoogleLogoutUseCase;
import com.corusoft.bareo.bareobackend.application.usecase.authentication.command.GoogleLogoutUseCaseCommand;
import com.corusoft.bareo.bareobackend.domain.entity.user.User;
import com.corusoft.bareo.bareobackend.domain.entity.user.vo.UserId;
import com.corusoft.bareo.bareobackend.domain.exception.auth.InvalidTokenException;
import com.corusoft.bareo.bareobackend.testing.config.annotation.UnitTest;
import com.corusoft.bareo.bareobackend.testing.dataset.TestUserDataset;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@UnitTest
public class GoogleLogoutUseCaseUnitTest {

  private final User testUser = TestUserDataset.getTestUser();

  @Mock private AuthenticationService authServiceMock;

  private GoogleLogoutUseCase logoutUseCase;

  @AfterEach
  public void resetMocks() {
    logoutUseCase = null;
  }

  @Test
  public void when_logout_then_ok() {
    // Arrange
    doNothing().when(authServiceMock).revokeAuthentication(any(UserId.class));

    // Act
    GoogleLogoutUseCaseCommand command = new GoogleLogoutUseCaseCommand(testUser.getId());
    logoutUseCase = new GoogleLogoutUseCase(authServiceMock);

    // Assert
    assertDoesNotThrow(() -> logoutUseCase.execute(command));
    verify(authServiceMock).revokeAuthentication(testUser.getId());
  }

  @Test
  public void when_logoutNonExistentUser_then_ok() {
    // Arrange
    doThrow(new InvalidTokenException())
        .when(authServiceMock)
        .revokeAuthentication(any(UserId.class));

    // Act
    GoogleLogoutUseCaseCommand command = new GoogleLogoutUseCaseCommand(testUser.getId());
    logoutUseCase = new GoogleLogoutUseCase(authServiceMock);

    // Assert
    assertThrows(InvalidTokenException.class, () -> logoutUseCase.execute(command));
    verify(authServiceMock).revokeAuthentication(testUser.getId());
  }
}
