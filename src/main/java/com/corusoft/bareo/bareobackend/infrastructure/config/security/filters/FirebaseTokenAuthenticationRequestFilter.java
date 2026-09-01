package com.corusoft.bareo.bareobackend.infrastructure.config.security.filters;

import static com.corusoft.bareo.bareobackend.infrastructure.config.security.SecurityConstants.PREFIX_BEARER_TOKEN;
import static com.corusoft.bareo.bareobackend.infrastructure.config.security.SecurityConstants.USER_ID_ATTRIBUTE_NAME;
import static com.corusoft.bareo.bareobackend.infrastructure.config.security.TokenClaim.ROLE_CLAIM;
import static java.util.Objects.isNull;

import com.corusoft.bareo.bareobackend.domain.shared.enums.UserRole;
import com.corusoft.bareo.bareobackend.domain.user.vo.UserId;
import com.corusoft.bareo.bareobackend.infrastructure.thirdparty.firebase.FirebaseAuthenticatedUserDetails;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Filter to validate and authenticate requests using Firebase Authentication.
 */
@Slf4j
@Component
public class FirebaseTokenAuthenticationRequestFilter extends OncePerRequestFilter {

  private final FirebaseAuth firebaseAuth;

  public FirebaseTokenAuthenticationRequestFilter(FirebaseAuth firebaseAuth) {
    this.firebaseAuth = firebaseAuth;
  }


  @Override
  protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
    return requestPathStartsWith(request, "/authentication/signup");
  }

  @Override
  protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain)
      throws ServletException, IOException {
    String authorizationHeaderToken = null;
    UsernamePasswordAuthenticationToken authToken;
    try {
      authorizationHeaderToken = extractTokenFromRequest(request);
      FirebaseToken firebaseToken = firebaseAuth.verifyIdToken(authorizationHeaderToken, true);
      authToken = buildAuthenticationToken(firebaseToken, request);
    } catch (FirebaseAuthException e) {
      logger.error("Could not verify Firebase token: {}", authorizationHeaderToken);
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
      String message = "Request does not contain the '%s' header".formatted(HttpHeaders.AUTHORIZATION);
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

  private UsernamePasswordAuthenticationToken buildAuthenticationToken(FirebaseToken firebaseToken, HttpServletRequest req)
      throws BadCredentialsException {
    if (isNull(firebaseToken)) {
      throw new BadCredentialsException("Firebase token is null");
    }

    // Add user related values from firebaseToken into security context
    req.setAttribute(USER_ID_ATTRIBUTE_NAME, new UserId(firebaseToken.getUid()));
    FirebaseAuthenticatedUserDetails userDetails = new FirebaseAuthenticatedUserDetails(firebaseToken);

    // Set user roles
    Set<GrantedAuthority> authorities = createAuthoritiesFromFirebaseToken(firebaseToken);

    return new UsernamePasswordAuthenticationToken(userDetails, firebaseToken, authorities);
  }

  private Set<GrantedAuthority> createAuthoritiesFromFirebaseToken(FirebaseToken token) {
    UserRole role = extractRoleFromToken(token);
    if (isNull(role)) {
      return Collections.emptySet();
    }

    logger.debug("Registering granted authorities for user '{}'", token.getUid());
    Set<GrantedAuthority> authorities = HashSet.newHashSet(1);
    authorities.add(new SimpleGrantedAuthority(role.name()));

    logger.debug("Granted authorities registered for user '{}'", token.getUid());
    return authorities;
  }

  private UserRole extractRoleFromToken(FirebaseToken token) {
    Map<String, Object> claims = token.getClaims();
    if (isNull(claims) || claims.isEmpty()) {
      logger.debug("User '{}' has no role assigned", token.getUid());
      return null;
    }

    return (UserRole) claims.get(ROLE_CLAIM.getClaimName());
  }
}

