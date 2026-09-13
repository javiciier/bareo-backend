/*
 * Copyright (c) 2026 Bareo. All rights reserved.
 *
 * This software is the proprietary and confidential property of the author.
 * Unauthorized copying, distribution, or use is strictly prohibited.
 */
package com.corusoft.bareo.bareobackend.infrastructure.thirdparty.firebase.security;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import lombok.Getter;

import com.google.firebase.auth.FirebaseToken;

import org.jspecify.annotations.NullMarked;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import static com.corusoft.bareo.bareobackend.infrastructure.config.security.TokenClaim.ROLE_CLAIM;
import static java.util.Objects.isNull;

@Getter
public class FirebaseAuthenticatedUserDetails implements UserDetails {

  private final String uid;
  private final String name;
  private final String email;
  private final String picture;
  private final transient Map<String, Object> claims;

  public FirebaseAuthenticatedUserDetails(FirebaseToken token) {
    this.uid = token.getUid();
    this.name = token.getName();
    this.email = token.getEmail();
    this.picture = token.getPicture();
    this.claims = token.getClaims();
  }

  @Override
  @NullMarked
  public Collection<? extends GrantedAuthority> getAuthorities() {
    if (isNull(getClaims())) {
      return Set.of();
    }

    Object roleClaim = getClaims().get(ROLE_CLAIM.getClaimName());
    if (isNull(roleClaim)) {
      return Set.of();
    }

    String principal = (String) roleClaim;
    return Set.of(new SimpleGrantedAuthority(principal));
  }

  @Override
  public String getPassword() {
    return null;
  }

  @Override
  @NullMarked
  public String getUsername() {
    return email;
  }
}
