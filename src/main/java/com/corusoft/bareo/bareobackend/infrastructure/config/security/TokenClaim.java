/*
 * Copyright (c) 2026 Bareo. All rights reserved.
 *
 * This software is the proprietary and confidential property of the author.
 * Unauthorized copying, distribution, or use is strictly prohibited.
 */
package com.corusoft.bareo.bareobackend.infrastructure.config.security;

import java.util.Map;
import java.util.stream.Collectors;

import lombok.Getter;

@Getter
public enum TokenClaim {
  ROLE_CLAIM("role");

  private final String claimName;

  TokenClaim(String claimName) {
    this.claimName = claimName;
  }

  public static Map<String, Object> toMap(Map<TokenClaim, Object> enumMap) {
    if (enumMap == null) {
      return Map.of();
    }

    return enumMap.entrySet().stream()
        .collect(Collectors.toMap(e -> e.getKey().getClaimName(), Map.Entry::getValue));
  }
}
