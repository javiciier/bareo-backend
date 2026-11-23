/*
 * Copyright (c) 2026 Bareo. All rights reserved.
 *
 * This software is the proprietary and confidential property of the author.
 * Unauthorized copying, distribution, or use is strictly prohibited.
 */
package com.corusoft.bareo.bareobackend.testing.unit.service.user;

import java.util.Optional;

import com.corusoft.bareo.bareobackend.application.service.user.UserService;
import com.corusoft.bareo.bareobackend.domain.entity.user.User;
import com.corusoft.bareo.bareobackend.domain.entity.user.vo.UserId;
import com.corusoft.bareo.bareobackend.domain.exception.user.UserNotFoundException;
import com.corusoft.bareo.bareobackend.domain.repository.UserRepository;
import com.corusoft.bareo.bareobackend.testing.config.annotation.UnitTest;
import com.corusoft.bareo.bareobackend.testing.dataset.TestUserDataset;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.mockito.Mock;

import static com.corusoft.bareo.bareobackend.testing.dataset.TestUserDataset.RAW_DEFAULT_USER_ID;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

@UnitTest
@TestInstance(Lifecycle.PER_CLASS)
public class UserServiceUnitTest {
  private final User testUser = TestUserDataset.getTestUser();

  @Mock private UserRepository userRepository;

  @AfterEach
  public void resetMocks() {
    reset(userRepository);
  }

  @Nested
  class FindById {
    UserId testUserId = testUser.getId();

    @Test
    void when_findById_then_ok() {
      // Arrange
      when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));

      // Act
      UserService userService = new UserService(userRepository);
      User result = userService.findById(testUserId);

      // Assert
      assertAll(() -> assertNotNull(result), () -> assertEquals(testUser, result));
    }

    @Test
    void when_findById_nonExistentUser__then_fail() {
      // Arrange
      when(userRepository.findById(testUserId)).thenReturn(Optional.empty());

      // Act
      UserService userService = new UserService(userRepository);

      // Assert
      assertThrows(UserNotFoundException.class, () -> userService.findById(testUserId));
    }
  }

  @Nested
  class ExistsById {
    UserId testUserId = new UserId(RAW_DEFAULT_USER_ID);

    @Test
    void when_existsById_then_ok() {
      // Arrange
      when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));

      // Act
      UserService userService = new UserService(userRepository);
      boolean result = userService.existsById(testUserId);

      // Assert
      assertAll(() -> assertTrue(result));
    }

    @Test
    void when_existsById_nonExistentUser__then_fail() {
      // Arrange
      when(userRepository.findById(testUserId)).thenReturn(Optional.empty());

      // Act
      UserService userService = new UserService(userRepository);

      // Assert
      assertFalse(userService.existsById(testUserId));
    }
  }

  @Nested
  class SaveUser {

    @Test
    void when_saveUser_then_ok() {
      // Arrange
      when(userRepository.save(testUser)).thenReturn(testUser);

      // Act
      UserService userService = new UserService(userRepository);
      User savedUser = userService.saveUser(testUser);

      // Assert
      assertAll(() -> assertNotNull(savedUser), () -> assertEquals(testUser, savedUser));
    }

    @Test
    void when_saveUser_and_alreadyExists__then_ok() {
      // Arrange
      when(userRepository.save(testUser)).thenReturn(testUser);

      // Act
      UserService userService = new UserService(userRepository);
      userService.saveUser(testUser);

      // Assert
      assertDoesNotThrow(() -> userService.saveUser(testUser));
    }
  }

  @Nested
  class DeleteUser {
    UserId testUserId = new UserId(RAW_DEFAULT_USER_ID);

    @Test
    void when_deleteUser_then_ok() {
      // Arrange
      when(userRepository.findById(testUserId)).thenReturn(Optional.ofNullable(testUser));
      doNothing().when(userRepository).delete(testUserId);
      // Act
      UserService userService = new UserService(userRepository);

      // Assert
      assertDoesNotThrow(() -> userService.deleteUser(testUserId));
    }

    @Test
    void when_deleteUser_and_notExists__then_fail() {
      // Arrange
      when(userRepository.findById(testUserId)).thenReturn(Optional.empty());

      // Act
      UserService userService = new UserService(userRepository);

      // Assert
      assertThrows(UserNotFoundException.class, () -> userService.deleteUser(testUserId));
    }
  }
}
