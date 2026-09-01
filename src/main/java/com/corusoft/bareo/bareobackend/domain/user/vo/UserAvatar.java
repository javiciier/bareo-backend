package com.corusoft.bareo.bareobackend.domain.user.vo;

import java.net.URI;
import java.util.Objects;

public record UserAvatar(URI value) {

  public UserAvatar {
    Objects.requireNonNull(value, "Avatar URL cannot be null");

    if (!value.isAbsolute()) {
      throw new IllegalArgumentException("Avatar URL must be absolute");
    }
    String scheme = value.getScheme();
    if (!"http".equalsIgnoreCase(scheme) && !"https".equalsIgnoreCase(scheme)) {
      throw new IllegalArgumentException("Avatar URL must be HTTP or HTTPS");
    }
  }

  public static UserAvatar of(String rawUrl) {
    if (rawUrl == null || rawUrl.isBlank()) {
      throw new IllegalArgumentException("Avatar URL cannot be blank");
    }

    try {
      return new UserAvatar(URI.create(rawUrl.trim()));
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Avatar URL is invalid " + rawUrl);
    }
  }

  @Override
  public String toString() {
    return value.toString();
  }
}
