/*
 * Copyright (c) 2026 Bareo. All rights reserved.
 *
 * This software is the proprietary and confidential property of the author.
 * Unauthorized copying, distribution, or use is strictly prohibited.
 */
package com.corusoft.bareo.bareobackend.testing.unit.domain.entity.user.vo;

import com.corusoft.bareo.bareobackend.domain.entity.user.vo.UserId;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserIdUnitTest {
  private static final String RAW_VALID_ID = "4lL2sVHVD0b9eqJIg4UpXmcZGYD3";

  @Test
  void when_isNull_then_fail() {
    NullPointerException exception =
        assertThrows(NullPointerException.class, () -> new UserId(null));
    assertEquals("User ID cannot be null", exception.getMessage());
  }

  @Test
  void when_isEmpty_then_fail() {
    IllegalArgumentException exception =
        assertThrows(IllegalArgumentException.class, () -> new UserId(""));
    assertEquals("User ID cannot be blank", exception.getMessage());
  }

  @Test
  void when_isBlankString_then_fail() {
    IllegalArgumentException exception =
        assertThrows(IllegalArgumentException.class, () -> new UserId("  "));
    assertEquals("User ID cannot be blank", exception.getMessage());
  }

  @Test
  void when_toString_then_ok() {
    assertEquals(RAW_VALID_ID, new UserId(RAW_VALID_ID).toString());
  }

  @Test
  void when_equals_then_ok() {
    assertEquals(new UserId(RAW_VALID_ID), new UserId(RAW_VALID_ID));
  }

  @Test
  void when_notEquals_then_fail() {
    assertNotEquals(new UserId("0000"), new UserId("1111"));
  }
}
