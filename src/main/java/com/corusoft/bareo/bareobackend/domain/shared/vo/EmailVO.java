package com.corusoft.bareo.bareobackend.domain.shared.vo;

import java.util.Objects;
import java.util.regex.Pattern;

public record EmailVO(String value) {

  private static final Pattern EMAIL_PATTERN = Pattern.compile(
      "^(?<user>[a-zA-Z0-9._%+-]+)"
          + "@"
          + "(?<domain>[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*)"
          + "\\."
          + "(?<tld>[a-zA-Z]{2,})$"
  );

  public EmailVO {
    Objects.requireNonNull(value, "Email must not be null");
    String normalizedValue = value.trim().toLowerCase();

    if (normalizedValue.strip().isBlank()) {
      throw new IllegalArgumentException("Email must not be blank");
    }

    if (!EMAIL_PATTERN.matcher(normalizedValue).matches()) {
      throw new IllegalArgumentException("Invalid email format");
    }

    value = normalizedValue;
  }

  @Override
  public String toString() {
    return value;
  }
}
