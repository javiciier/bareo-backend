/*
 * Copyright (c) 2026 Bareo. All rights reserved.
 *
 * This software is the proprietary and confidential property of the author.
 * Unauthorized copying, distribution, or use is strictly prohibited.
 */
package com.corusoft.bareo.bareobackend.testing.unit.domain.entity.user.vo;

import com.corusoft.bareo.bareobackend.domain.entity.user.vo.UsernameVO;

import org.junit.jupiter.api.Test;

import static com.corusoft.bareo.bareobackend.domain.entity.user.vo.UsernameVO.USERNAME_MAX_LENGTH;
import static com.corusoft.bareo.bareobackend.domain.entity.user.vo.UsernameVO.USERNAME_MIN_LENGTH;
import static com.corusoft.bareo.bareobackend.testing.dataset.TestUserDataset.RAW_DEFAULT_USER_NICKNAME;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UsernameVOUnitTest {
  private static final String INVALID_LENGTH_MESSAGE =
      "Username length must be between 5 and 50 characters";

  @Test
  void when_isNull_then_fail() {
    NullPointerException exception =
        assertThrows(NullPointerException.class, () -> new UsernameVO(null));
    assertEquals("Username cannot be null", exception.getMessage());
  }

  @Test
  void when_isEmptyS_then_fail() {
    IllegalArgumentException exception =
        assertThrows(IllegalArgumentException.class, () -> new UsernameVO(""));
    assertEquals("Username cannot be blank", exception.getMessage());
  }

  @Test
  void when_isBlankString_then_fail() {
    var exception = assertThrows(IllegalArgumentException.class, () -> new UsernameVO("  "));
    assertEquals("Username cannot be blank", exception.getMessage());
  }

  @Test
  void when_toString_then_ok() {
    assertEquals(RAW_DEFAULT_USER_NICKNAME, new UsernameVO(RAW_DEFAULT_USER_NICKNAME).toString());
  }

  @Test
  void when_equals_then_ok() {
    assertEquals(
        new UsernameVO(RAW_DEFAULT_USER_NICKNAME), new UsernameVO(RAW_DEFAULT_USER_NICKNAME));
  }

  @Test
  void when_notEquals_then_fail() {
    assertNotEquals(
        new UsernameVO(RAW_DEFAULT_USER_NICKNAME), new UsernameVO(RAW_DEFAULT_USER_NICKNAME + "_"));
  }

  @Test
  void when_tooShort_then_fail() {
    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> new UsernameVO("a".repeat(USERNAME_MIN_LENGTH - 1)));
    assertEquals(INVALID_LENGTH_MESSAGE, exception.getMessage());
  }

  @Test
  void when_matchesMinLength_then_ok() {
    assertDoesNotThrow(() -> new UsernameVO("a".repeat(USERNAME_MIN_LENGTH)));
  }

  @Test
  void when_tooLong_then_fail() {
    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> new UsernameVO("a".repeat(USERNAME_MAX_LENGTH + 1)));
    assertEquals(INVALID_LENGTH_MESSAGE, exception.getMessage());
  }

  @Test
  void when_matchesMaxLength_then_ok() {
    assertDoesNotThrow(() -> new UsernameVO("a".repeat(USERNAME_MAX_LENGTH)));
  }
}
