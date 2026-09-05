package com.corusoft.bareo.bareobackend.infrastructure.config.security.filters;

import static com.corusoft.bareo.bareobackend.infrastructure.config.security.SecurityConstants.USER_ID_ATTRIBUTE_NAME;
import static com.corusoft.bareo.bareobackend.infrastructure.config.security.SecurityConstants.X_REQUEST_ID_HEADER_NAME;
import static java.util.Objects.isNull;

import com.corusoft.bareo.bareobackend.domain.user.vo.UserId;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.apache.logging.log4j.ThreadContext;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Filter to allow tracing the operations done by a user during the request lifetime.
 */
@Component
public class UserTraceabilityRequestFilter extends OncePerRequestFilter {

  public static final String USER_ID_LOG_KEY = USER_ID_ATTRIBUTE_NAME;
  public static final String USER_ID_DEFAULT_LOG_VALUE = "ANONYMOUS";

  public static final String REQUEST_ID_LOG_KEY = "requestId";
  public static final String REQUEST_ID_DEFAULT_LOG_VALUE = "0";

  @Override
  protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain)
      throws ServletException, IOException {
    Object requestUserId = request.getAttribute(USER_ID_ATTRIBUTE_NAME);
    Object requestId = request.getAttribute(X_REQUEST_ID_HEADER_NAME);

    try {
      if (isNull(requestUserId)) {
        ThreadContext.put(USER_ID_LOG_KEY, USER_ID_DEFAULT_LOG_VALUE);
      } else {
        UserId userId = new UserId(requestUserId.toString());
        ThreadContext.put(USER_ID_LOG_KEY, userId.toString());
      }
      if (isNull(requestId)) {
        ThreadContext.put(REQUEST_ID_LOG_KEY, REQUEST_ID_DEFAULT_LOG_VALUE);
      } else {
        ThreadContext.put(REQUEST_ID_LOG_KEY, requestId.toString());
      }

      // Continue filtering requests
      filterChain.doFilter(request, response);
    } finally {
      // Clear keys from context to avoid leaks
      ThreadContext.remove(USER_ID_LOG_KEY);
      ThreadContext.remove(REQUEST_ID_LOG_KEY);
    }
  }
}
