/*
 * Copyright (c) 2026 Bareo. All rights reserved.
 *
 * This software is the proprietary and confidential property of the author.
 * Unauthorized copying, distribution, or use is strictly prohibited.
 */
package com.corusoft.bareo.bareobackend.presentation.api;

import java.time.Instant;
import java.util.List;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import com.corusoft.bareo.bareobackend.presentation.api.error.ApiErrorDetails;
import com.corusoft.bareo.bareobackend.presentation.api.error.ApiErrorResponseBody;

import org.springframework.http.HttpStatus;

import static java.util.Objects.isNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ApiResponseBuilder {

  public static <T> ApiResponse<T> createResponse(boolean success, T body) {
    return ApiResponse.<T>builder().success(success).data(body).timestamp(Instant.now()).build();
  }

  // region Success responses

  /** Builds a successful response with empty body */
  public static ApiResponse<Void> success() {
    return createResponse(true, null);
  }

  /**
   * Builds a successful response with the given content in the body
   *
   * @param content response content
   * @param <T> Type of the response content
   */
  public static <T> ApiResponse<T> success(T content) {
    return createResponse(true, content);
  }

  // endregion Success responses

  // region Error responses

  /**
   * Builds an error response with the given HTTP status and message
   *
   * @param status HTTP Status that identifies the error
   * @param message Cause of the error
   */
  public static ApiResponse<ApiErrorResponseBody> error(HttpStatus status, String message) {
    return error(status, message, null);
  }

  /**
   * Builds an error response with the given HTTP status, a message and the exception cause
   *
   * @param status HTTP Status that identifies the error
   * @param message Cause of the error
   * @param exception Exception that caused the error
   */
  public static ApiResponse<ApiErrorResponseBody> error(
      HttpStatus status, String message, Exception exception) {
    ApiErrorResponseBody body = generateErrorResponseBody(status, message, exception);

    return createResponse(false, body);
  }

  /**
   * Builds an error response with the given HTTP status, a message, the exception cause and a list
   * of the error details
   *
   * @param status HTTP Status that identifies the error
   * @param message Cause of the error
   * @param exception Exception that caused the error
   * @param errorDetails List of details that explain the cause of the error response
   */
  public static ApiResponse<ApiErrorResponseBody> error(
      HttpStatus status,
      String message,
      Exception exception,
      List<? extends ApiErrorDetails> errorDetails) {
    ApiErrorResponseBody body = generateErrorResponseBody(status, message, exception, errorDetails);

    return createResponse(false, body);
  }

  // endregion Error responses

  // region Auxiliar methods

  private static <E extends Exception> ApiErrorResponseBody generateErrorResponseBody(
      HttpStatus status, String message, E exception) {
    return generateErrorResponseBody(status, message, exception, null);
  }

  private static <E extends Exception> ApiErrorResponseBody generateErrorResponseBody(
      HttpStatus status, String message, E exception, List<? extends ApiErrorDetails> errors) {
    boolean hasErrors = !(isNull(errors)) && (!errors.isEmpty());
    String debugMessage =
        (!(isNull(exception)) && !hasErrors) ? exception.getLocalizedMessage() : null;
    List<? extends ApiErrorDetails> errorDetails = hasErrors ? errors : null;

    return ApiErrorResponseBody.builder()
        .status(status.name())
        .statusCode(status.value())
        .message(message)
        .debugMessage(debugMessage)
        .errors(errorDetails)
        .build();
  }

  // endregion Auxiliar methods
}
