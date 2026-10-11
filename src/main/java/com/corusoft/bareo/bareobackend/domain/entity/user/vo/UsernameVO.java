/*
 * Copyright (c) 2026 Bareo. All rights reserved.
 *
 * This software is the proprietary and confidential property of the author.
 * Unauthorized copying, distribution, or use is strictly prohibited.
 */
package com.corusoft.bareo.bareobackend.domain.entity.user.vo;

import java.util.Objects;

import com.corusoft.bareo.bareobackend.shared.ValueObject;

public record UsernameVO(String value) implements ValueObject<String> {

  public static final int USERNAME_MIN_LENGTH = 5;
  public static final int USERNAME_MAX_LENGTH = 50;

  public UsernameVO {
    Objects.requireNonNull(value, "Username cannot be null");

    if (value.strip().isBlank()) {
      throw new IllegalArgumentException("Username cannot be blank");
    }
    if (value.length() < USERNAME_MIN_LENGTH || value.length() > USERNAME_MAX_LENGTH) {
      throw new IllegalArgumentException(
          "Username length must be between %d and %d characters"
              .formatted(USERNAME_MIN_LENGTH, USERNAME_MAX_LENGTH));
    }
  }

  @Override
  public String toString() {
    return ValueObject.asString(this);
  }
}
