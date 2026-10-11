/*
 * Copyright (c) 2026 Bareo. All rights reserved.
 *
 * This software is the proprietary and confidential property of the author.
 * Unauthorized copying, distribution, or use is strictly prohibited.
 */
package com.corusoft.bareo.bareobackend.presentation.controller.advice;

import java.util.List;

import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import com.corusoft.bareo.bareobackend.presentation.MessageTranslator;
import com.corusoft.bareo.bareobackend.presentation.api.ApiResponse;
import com.corusoft.bareo.bareobackend.presentation.api.error.ApiErrorResponseBody;
import com.corusoft.bareo.bareobackend.presentation.api.error.ApiValidationErrorDetails;

import com.google.firebase.auth.FirebaseAuthException;

import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import static com.corusoft.bareo.bareobackend.presentation.api.ApiResponseBuilder.error;

/** Controller to handle exceptions thrown by the JVM or Spring framework */
@ToString
@ControllerAdvice
@Order()
@Slf4j
public class GenericControllerAdvice {

  private final MessageTranslator translator;

  public GenericControllerAdvice(MessageTranslator translator) {
    this.translator = translator;
  }

  private void logError(Exception ex) {
    logger.error("A {} exception was thrown", ex.getClass().getSimpleName(), ex);
    logger.error(ex.getMessage());
  }

  /**
   * Default exception handler to manage uncaught exceptions on runtime.
   *
   * @param ex Exception that is not managed by another {@link ExceptionHandler}
   */
  @ResponseBody
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ExceptionHandler({RuntimeException.class})
  public ApiResponse<ApiErrorResponseBody> handleUnexpectedExceptions(Exception ex) {
    logger.error("An uncaught exception was thrown on runtime: {}", ex.getClass());
    String errorMessage = translator.generateMessage(ex.getClass().getName());

    return error(HttpStatus.INTERNAL_SERVER_ERROR, errorMessage, ex);
  }

  @ResponseBody
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ExceptionHandler({NullPointerException.class})
  public ApiResponse<ApiErrorResponseBody> handleNullPointerExceptions(NullPointerException ex) {
    logError(ex);
    String errorMessage = translator.generateMessage(ex.getClass().getName());

    return error(HttpStatus.INTERNAL_SERVER_ERROR, errorMessage, ex);
  }

  @ResponseBody
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler({IllegalArgumentException.class})
  public ApiResponse<ApiErrorResponseBody> handleIllegalArgumentException(
      IllegalArgumentException ex) {
    logError(ex);
    String errorMessage = translator.generateMessage(ex.getClass().getName());

    return error(HttpStatus.BAD_REQUEST, errorMessage, ex);
  }

  // region Spring exception handlers

  @ResponseBody
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ApiResponse<ApiErrorResponseBody> handleMethodArgumentNotValid(
      MethodArgumentNotValidException ex) {
    logError(ex);

    List<ApiValidationErrorDetails> errorsList =
        ex.getBindingResult().getFieldErrors().stream()
            .map(ApiValidationErrorDetails::new)
            .toList();
    String errorMessage = translator.generateMessage(ex.getClass().getName());

    return error(HttpStatus.BAD_REQUEST, errorMessage, ex, errorsList);
  }

  @ResponseBody
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(MissingServletRequestParameterException.class)
  public ApiResponse<ApiErrorResponseBody> handleMissingServletRequestParameterException(
      MissingServletRequestParameterException ex) {
    logError(ex);

    Object[] args = {ex.getParameterName(), ex.getParameterType()};
    String errorMessage = translator.generateMessage(ex.getClass().getName(), args);

    return error(HttpStatus.BAD_REQUEST, errorMessage);
  }

  @ResponseBody
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ApiResponse<ApiErrorResponseBody> handleHttpMessageNotReadableException(
      HttpMessageNotReadableException ex) {
    logError(ex);
    String errorMessage = translator.generateMessage(ex.getClass().getName());

    return error(HttpStatus.BAD_REQUEST, errorMessage);
  }

  // endregion Spring exception handlers

  // region Firebase exception handlers

  @ResponseBody
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(FirebaseAuthException.class)
  public ApiResponse<ApiErrorResponseBody> handleFirebaseAuthException(FirebaseAuthException ex) {
    logError(ex);
    String errorMessage = translator.generateMessage(ex.getClass().getName());

    return error(HttpStatus.BAD_REQUEST, errorMessage, ex);
  }

  // endregion Firebase exception handlers
}
