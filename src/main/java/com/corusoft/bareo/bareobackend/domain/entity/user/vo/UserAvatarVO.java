/*
 * Copyright (c) 2026 Bareo. All rights reserved.
 *
 * This software is the proprietary and confidential property of the author.
 * Unauthorized copying, distribution, or use is strictly prohibited.
 */
package com.corusoft.bareo.bareobackend.domain.entity.user.vo;

import java.net.URI;
import java.util.Objects;

import com.corusoft.bareo.bareobackend.shared.ValueObject;

public record UserAvatarVO(URI value) implements ValueObject<URI> {

  public UserAvatarVO {
    Objects.requireNonNull(value, "Avatar URL cannot be null");

    if (value.toString().isBlank()) {
      throw new IllegalArgumentException("Avatar URL cannot be blank");
    }
  }

  public static UserAvatarVO of(String value) {
    Objects.requireNonNull(value, "Avatar URL cannot be null");

    if (value.isBlank()) {
      throw new IllegalArgumentException("Avatar URL cannot be blank");
    }

    try {
      return new UserAvatarVO(URI.create(value.trim()));
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Avatar URL is invalid: " + value);
    }
  }

  @Override
  public String toString() {
    return ValueObject.asString(this);
  }
}
