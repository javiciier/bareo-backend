/*
 * Copyright (c) 2026 Bareo. All rights reserved.
 *
 * This software is the proprietary and confidential property of the author.
 * Unauthorized copying, distribution, or use is strictly prohibited.
 */
package com.corusoft.bareo.bareobackend.testing.unit.service.thirdparty.firebase;

import com.corusoft.bareo.bareobackend.domain.entity.user.User;
import com.corusoft.bareo.bareobackend.testing.config.annotation.UnitTest;
import com.corusoft.bareo.bareobackend.testing.dataset.TestUserDataset;
import com.corusoft.bareo.bareobackend.testing.mocking.firebase.FirebaseAuthMock;

import com.google.firebase.auth.FirebaseAuth;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

@UnitTest
@TestInstance(Lifecycle.PER_CLASS)
public class FirebaseServiceUnitTest {

  private final User testUser = TestUserDataset.getTestUser();

  private FirebaseAuth firebaseAuthMock;

  @BeforeAll
  public void setUp() {
    FirebaseAuthMock.initializeMock();
  }

  @AfterEach
  public void resetMocks() {
    FirebaseAuthMock.resetMock();
  }

  @Nested
  class GetUser {}

  @Nested
  class VerifyToken {}

  @Nested
  class RevokeToken {}

  @Nested
  class RemoveUser {}

  @Nested
  class GetClaimsForUser {}

  @Nested
  class AssignClaimsToUser {}
}
