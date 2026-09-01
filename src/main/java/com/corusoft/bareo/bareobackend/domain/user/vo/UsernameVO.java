package com.corusoft.bareo.bareobackend.domain.user.vo;

import java.util.Objects;

public record UsernameVO(String value) {

  public static final int USERNAME_MIN_LENGTH = 5;
  public static final int USERNAME_MAX_LENGTH = 50;

  public UsernameVO {
    Objects.requireNonNull(value, "Username cannot be null");

    if (value.strip().isBlank()) {
      throw new IllegalArgumentException("Username cannot be blank");
    }
    if (value.length() < USERNAME_MIN_LENGTH || value.length() > USERNAME_MAX_LENGTH) {
      throw new IllegalArgumentException(
          "Username length must be between " + USERNAME_MIN_LENGTH + " and " + USERNAME_MAX_LENGTH + " characters");
    }
  }

  @Override
  public String toString() {
    return value;
  }
}
