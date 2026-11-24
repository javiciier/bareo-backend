/*
 * Copyright (c) 2026 Bareo. All rights reserved.
 *
 * This software is the proprietary and confidential property of the author.
 * Unauthorized copying, distribution, or use is strictly prohibited.
 */
package com.corusoft.bareo.bareobackend.testing.unit.domain.entity.user.vo;

import java.net.URI;

import com.corusoft.bareo.bareobackend.domain.entity.user.vo.UserAvatarVO;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static com.corusoft.bareo.bareobackend.testing.dataset.TestUserDataset.RAW_DEFAULT_USER_AVATAR_URL;
import static com.corusoft.bareo.bareobackend.testing.dataset.TestUserDataset.RAW_OTHER_DEFAULT_USER_AVATAR_URL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserAvatarUnitTest {
  private static final URI VALID_AVATAR_URI = URI.create(RAW_DEFAULT_USER_AVATAR_URL);
  private static final URI ANOTHER_VALID_AVATAR_URI = URI.create(RAW_OTHER_DEFAULT_USER_AVATAR_URL);

  private static final String RAW_URI_WITH_SPACES_INVALID_URI = "https://domain with spaces.io/";

  @Test
  void when_isNull_then_fail() {
    NullPointerException exception =
        assertThrows(NullPointerException.class, () -> new UserAvatarVO(null));
    assertEquals("Avatar URL cannot be null", exception.getMessage());
  }

  @Test
  void when_isEmpty_then_fail() {
    IllegalArgumentException exception =
        assertThrows(IllegalArgumentException.class, () -> new UserAvatarVO(URI.create("")));
    assertEquals("Avatar URL cannot be blank", exception.getMessage());
  }

  @Test
  void when_isBlankString_then_fail() {
    IllegalArgumentException exception =
        assertThrows(IllegalArgumentException.class, () -> new UserAvatarVO(URI.create(" ")));
    assertTrue(exception.getMessage().contains("Illegal character in path"));
  }

  @Test
  void when_toString_then_ok() {
    assertEquals(RAW_DEFAULT_USER_AVATAR_URL, new UserAvatarVO(VALID_AVATAR_URI).toString());
  }

  @Test
  void when_equals_then_ok() {
    assertEquals(new UserAvatarVO(VALID_AVATAR_URI), new UserAvatarVO(VALID_AVATAR_URI));
  }

  @Test
  void when_notEquals_then_fail() {
    assertNotEquals(new UserAvatarVO(VALID_AVATAR_URI), new UserAvatarVO(ANOTHER_VALID_AVATAR_URI));
  }

  @Test
  void when_hasSpaces_then_fail() {
    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> new UserAvatarVO(URI.create(RAW_URI_WITH_SPACES_INVALID_URI)));
    assertTrue(exception.getMessage().contains("Illegal character in authority"));
  }

  @Nested
  class OfString {

    @Test
    void when_isNull_then_fail() {
      NullPointerException exception =
          assertThrows(NullPointerException.class, () -> UserAvatarVO.of(null));
      assertEquals("Avatar URL cannot be null", exception.getMessage());
    }

    @Test
    void when_isEmpty_then_fail() {
      IllegalArgumentException exception =
          assertThrows(IllegalArgumentException.class, () -> UserAvatarVO.of(""));
      assertEquals("Avatar URL cannot be blank", exception.getMessage());
    }

    @Test
    void when_isBlankString_then_fail() {
      IllegalArgumentException exception =
          assertThrows(IllegalArgumentException.class, () -> UserAvatarVO.of(" "));
      assertEquals("Avatar URL cannot be blank", exception.getMessage());
    }

    @Test
    void when_stringHasSpacesInBetween_then_fail() {
      IllegalArgumentException exception =
          assertThrows(
              IllegalArgumentException.class,
              () -> UserAvatarVO.of(RAW_URI_WITH_SPACES_INVALID_URI));
      assertTrue(
          exception
              .getMessage()
              .contains("Avatar URL is invalid: " + RAW_URI_WITH_SPACES_INVALID_URI));
    }

    @Test
    void when_equals_then_ok() {
      assertEquals(
          UserAvatarVO.of(RAW_DEFAULT_USER_AVATAR_URL),
          UserAvatarVO.of(RAW_DEFAULT_USER_AVATAR_URL));
    }

    @Test
    void when_notEquals_then_fail() {
      assertNotEquals(
          UserAvatarVO.of(RAW_DEFAULT_USER_AVATAR_URL),
          UserAvatarVO.of(RAW_OTHER_DEFAULT_USER_AVATAR_URL));
    }
  }
}
