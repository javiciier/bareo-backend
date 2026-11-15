/*
 * Copyright (c) 2026 Bareo. All rights reserved.
 *
 * This software is the proprietary and confidential property of the author.
 * Unauthorized copying, distribution, or use is strictly prohibited.
 */
package com.corusoft.bareo.bareobackend.testing.unit.usecase.authentication;

import com.corusoft.bareo.bareobackend.application.service.auth.AuthenticationService;
import com.corusoft.bareo.bareobackend.application.service.user.UserService;
import com.corusoft.bareo.bareobackend.infrastructure.thirdparty.firebase.FirebaseService;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class GoogleAuthenticationUseCaseUnitTest {

  // region TEST VARIABLES

  private AuthenticationService authenticationServiceMock;
  private UserService userServiceMock;
  private FirebaseService firebaseServiceMock;

  // endregion TEST VARIABLES

  // region TEST LIFECYCLE

  @BeforeEach
  void setupMocks() {
    authenticationServiceMock = Mockito.mock(AuthenticationService.class);
    userServiceMock = Mockito.mock(UserService.class);
    firebaseServiceMock = Mockito.mock(FirebaseService.class);
  }

  @AfterEach
  void resetMocks() {
    authenticationServiceMock = null;
    userServiceMock = null;
    firebaseServiceMock = null;
  }

  // endregion TEST LIFECYCLE

  //    @Test()
  //    void when_authenticate_then_success() {
  //        User expectedUser = createUser();
  //        when(authenticationServiceMock.authenticateUserWithToken(anyString()))
  //                .thenReturn(expectedUser);
  //    }

}
