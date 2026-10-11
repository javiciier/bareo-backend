/*
 * Copyright (c) 2026 Bareo. All rights reserved.
 *
 * This software is the proprietary and confidential property of the author.
 * Unauthorized copying, distribution, or use is strictly prohibited.
 */
package com.corusoft.bareo.bareobackend.infrastructure.config.security.filters;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;

import com.corusoft.bareo.bareobackend.domain.entity.user.vo.UserId;
import com.corusoft.bareo.bareobackend.domain.exception.auth.InvalidTokenException;
import com.corusoft.bareo.bareobackend.domain.shared.enums.UserRole;
import com.corusoft.bareo.bareobackend.infrastructure.thirdparty.firebase.FirebaseService;
import com.corusoft.bareo.bareobackend.infrastructure.thirdparty.firebase.security.FirebaseAuthenticatedUserDetails;

import com.google.firebase.auth.FirebaseToken;

import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import static com.corusoft.bareo.bareobackend.infrastructure.config.security.SecurityConstants.PREFIX_BEARER_TOKEN;
import static com.corusoft.bareo.bareobackend.infrastructure.config.security.SecurityConstants.USER_ID_ATTRIBUTE_NAME;
import static com.corusoft.bareo.bareobackend.infrastructure.config.security.TokenClaim.ROLE_CLAIM;
import static java.util.Objects.isNull;

/** Filter to validate and authenticate requests using Firebase Authentication. */
@Slf4j
@Component
public class FirebaseTokenAuthenticationRequestFilter extends OncePerRequestFilter {

  private final FirebaseService firebaseService;

  public FirebaseTokenAuthenticationRequestFilter(FirebaseService firebaseService) {
    this.firebaseService = firebaseService;
  }

  @Override
  protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
    return requestPathStartsWith(request, "/users/signup");
  }

  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain)
      throws ServletException, IOException {
    String authorizationHeaderToken = null;
    UsernamePasswordAuthenticationToken authToken;
    try {
      authorizationHeaderToken = extractTokenFromRequest(request);
      FirebaseToken firebaseToken = firebaseService.verifyToken(authorizationHeaderToken);
      authToken = buildAuthenticationToken(firebaseToken, request);
    } catch (InvalidTokenException e) {
      logger.error("Could not verify token: {}", authorizationHeaderToken);
      throw new BadCredentialsException(e.getMessage());
    } catch (BadCredentialsException e) {
      logger.error("Unexpected error when extracting token from request: {}", e.getMessage());
      throw e;
    }

    SecurityContextHolder.getContext().setAuthentication(authToken);
    // Continue filtering requests
    filterChain.doFilter(request, response);
  }

  private boolean requestPathStartsWith(HttpServletRequest req, String prefix) {
    return req.getServletPath().startsWith(prefix);
  }

  private String extractTokenFromRequest(HttpServletRequest req) throws BadCredentialsException {
    // Check response contains Authorization header
    String authHeader = req.getHeader(HttpHeaders.AUTHORIZATION);
    if (isNull(authHeader)) {
      String message =
          "Request does not contain the '%s' header".formatted(HttpHeaders.AUTHORIZATION);
      logger.error(message);
      throw new BadCredentialsException(message);
    }
    if (authHeader.strip().isBlank()) {
      String message = "Request header '%s' is empty".formatted(HttpHeaders.AUTHORIZATION);
      logger.error(message);
      throw new BadCredentialsException(message);
    }
    if (!authHeader.startsWith(PREFIX_BEARER_TOKEN)) {
      String message = "Token must be prefixed with '%s'".formatted(PREFIX_BEARER_TOKEN);
      logger.error(message);
      throw new BadCredentialsException(message);
    }

    // Remove "Bearer " from header
    String token = authHeader.replace(PREFIX_BEARER_TOKEN, "").strip();
    if (token.strip().isBlank()) {
      String message = "Non existent or malformed token in request";
      logger.debug(message);
      throw new BadCredentialsException(message);
    }

    return token;
  }

  private UsernamePasswordAuthenticationToken buildAuthenticationToken(
      FirebaseToken token, HttpServletRequest req) throws BadCredentialsException {
    if (isNull(token)) {
      throw new BadCredentialsException("Firebase token is null");
    }

    // Add user related values from token into security context
    UserId userId = new UserId(token.getUid());
    req.setAttribute(USER_ID_ATTRIBUTE_NAME, userId);
    FirebaseAuthenticatedUserDetails userDetails = new FirebaseAuthenticatedUserDetails(token);

    // Assign role to user
    Set<GrantedAuthority> authorities = HashSet.newHashSet(1);
    UserRole role = getRoleForUser(userId);
    if (role != null) {
      authorities.add(new SimpleGrantedAuthority(role.name()));
    }

    return new UsernamePasswordAuthenticationToken(userDetails, token, authorities);
  }

  private UserRole getRoleForUser(UserId userId) {
    Map<String, Object> claims = firebaseService.getClaimsForUser(userId);
    if (claims.isEmpty()) {
      logger.debug("User '{}' has no roles assigned", userId);
      return null;
    }

    String claim = (String) claims.get(ROLE_CLAIM.getClaimName());
    return UserRole.valueOf(claim);
  }
}
