/*
 * Copyright (c) 2026 Bareo. All rights reserved.
 *
 * This software is the proprietary and confidential property of the author.
 * Unauthorized copying, distribution, or use is strictly prohibited.
 */
package com.corusoft.bareo.bareobackend.domain.entity.user.vo;

import java.util.Objects;

import com.corusoft.bareo.bareobackend.shared.ValueObject;

public record UserId(String value) implements ValueObject<String> {

  public UserId {
    Objects.requireNonNull(value, "User ID cannot be null");

    if (value.strip().isBlank()) {
      throw new IllegalArgumentException("User ID cannot be blank");
    }
  }

  @Override
  public String toString() {
    return ValueObject.asString(this);
  }
}
