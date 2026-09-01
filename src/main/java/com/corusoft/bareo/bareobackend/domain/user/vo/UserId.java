package com.corusoft.bareo.bareobackend.domain.user.vo;

import java.util.Objects;

public record UserId(String value) {

  public static final int USER_ID_MIN_LENGTH = 36;
  public static final int USER_ID_MAX_LENGTH = 128;

  public UserId {
    Objects.requireNonNull(value, "User ID cannot be null");

    if (value.strip().isBlank()) {
      throw new IllegalArgumentException("User ID cannot be blank");
    }
    if (value.length() < USER_ID_MIN_LENGTH || value.length() > USER_ID_MAX_LENGTH) {
      throw new IllegalArgumentException(
          "User ID length must be between " + USER_ID_MIN_LENGTH + " and " + USER_ID_MAX_LENGTH + " characters");
    }
  }

  @Override
  public String toString() {
    return value;
  }
}
