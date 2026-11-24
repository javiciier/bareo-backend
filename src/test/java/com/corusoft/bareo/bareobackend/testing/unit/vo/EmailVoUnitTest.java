/*
 * Copyright (c) 2026 Bareo. All rights reserved.
 *
 * This software is the proprietary and confidential property of the author.
 * Unauthorized copying, distribution, or use is strictly prohibited.
 */
package com.corusoft.bareo.bareobackend.testing.unit.vo;

import java.util.List;

import com.corusoft.bareo.bareobackend.domain.shared.vo.EmailVO;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class EmailVoUnitTest {

  private static List<String> validEmails() {
    return List.of(
        "user@example.com",
        "test.user@example.com",
        "user@sub.domain.org",
        "test.user@sub.domain.org",
        "test_user@sub.domain.org",
        "TEST_USER@SUB.DOMAIN.ORG",
        "user.name+tag+sorting@example.com",
        "user_name@example.co.uk",
        "1234567890@domain-name.org",
        "u%ser@sub-subdomain.org",
        "a-b-c@d-e-f.io");
  }

  private static List<String> malformedEmails() {
    return List.of(
        "plainaddress",
        "@no-username.com",
        "user@.com",
        "user@domain",
        "invalid_domain@domain.c",
        "user@example..com",
        "user@domain_name.com",
        "user@domain.c0m",
        "user name@example.com",
        "user@domain.c",
        "user@.sub.domain.com",
        "user@domain.com.");
  }

  @Test
  void when_isNull_then_fail() {
    NullPointerException exception =
        assertThrows(NullPointerException.class, () -> new EmailVO(null));
    assertEquals("Email cannot be null", exception.getMessage());
  }

  @Test
  void when_isEmptyS_then_fail() {
    IllegalArgumentException exception =
        assertThrows(IllegalArgumentException.class, () -> new EmailVO(""));
    assertEquals("Email cannot be blank", exception.getMessage());
  }

  @Test
  void when_isBlankString_then_fail() {
    IllegalArgumentException exception =
        assertThrows(IllegalArgumentException.class, () -> new EmailVO(" "));
    assertEquals("Email cannot be blank", exception.getMessage());
  }

  @ParameterizedTest
  @MethodSource(value = "validEmails")
  void when_toString_then_ok(String email) {
    assertEquals(email.toLowerCase(), new EmailVO(email).toString());
  }

  @ParameterizedTest
  @MethodSource(value = "validEmails")
  void when_equals_then_ok(String email) {
    assertEquals(new EmailVO(email).toString(), new EmailVO(email).toString());
  }

  @ParameterizedTest
  @MethodSource(value = "malformedEmails")
  void when_isNotValidFormat_then_fail(String email) {
    IllegalArgumentException exception =
        assertThrows(IllegalArgumentException.class, () -> new EmailVO(email));
    assertEquals("Invalid email format", exception.getMessage());
  }
}
