package com.corusoft.bareo.bareobackend.infrastructure.config.security;

public class SecurityConstants {

  public static final int MIN_PASSWORD_LENGTH = 6;
  public static final int MAX_PASSWORD_LENGTH = 50;

  // region HTTP

  public static final String PREFIX_BEARER_TOKEN = "Bearer ";
  public static final String X_REQUEST_ID_HEADER_NAME = "X-Request-Id";

  // endregion HTTP

  // region AUTHENTICATION

  public static final String TOKEN_ATTRIBUTE_NAME = "token";
  public static final String USER_ID_ATTRIBUTE_NAME = "userId";

  // endregion AUTHENTICATION

}
