/*
 * Copyright (c) 2026 Bareo. All rights reserved.
 *
 * This software is the proprietary and confidential property of the author.
 * Unauthorized copying, distribution, or use is strictly prohibited.
 */
package com.corusoft.bareo.bareobackend.infrastructure.config.security;

import lombok.Getter;

@Getter
public enum TokenClaim {
  ROLE_CLAIM("role");

  private final String claimName;

  TokenClaim(String claimName) {
    this.claimName = claimName;
  }
}
