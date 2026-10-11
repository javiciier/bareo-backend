/*
 * Copyright (c) 2026 Bareo. All rights reserved.
 *
 * This software is the proprietary and confidential property of the author.
 * Unauthorized copying, distribution, or use is strictly prohibited.
 */
package com.corusoft.bareo.bareobackend.presentation.controller.advice;

import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import com.corusoft.bareo.bareobackend.domain.exception.auth.UnauthorizedActionException;
import com.corusoft.bareo.bareobackend.presentation.MessageTranslator;
import com.corusoft.bareo.bareobackend.presentation.api.ApiResponse;
import com.corusoft.bareo.bareobackend.presentation.api.error.ApiErrorResponseBody;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import static com.corusoft.bareo.bareobackend.presentation.api.ApiResponseBuilder.error;
import static java.util.Objects.isNull;

@ToString
@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class DomainControllerAdvice {

  private final MessageTranslator translator;

  public DomainControllerAdvice(MessageTranslator translator) {
    this.translator = translator;
  }

  private void logError(Exception ex) {
    logger.error(
        "A {} exception was thrown: {}",
        ex.getClass().getSimpleName(),
        (!(isNull(ex.getMessage()))) ? ex.getMessage() : "");
  }

  @ResponseBody
  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  @ExceptionHandler({UnauthorizedActionException.class})
  public ApiResponse<ApiErrorResponseBody> handleInsufficientPermissionsException(
      UnauthorizedActionException ex) {
    logError(ex);
    String errorMessage = translator.generateMessage(ex);

    return error(HttpStatus.UNAUTHORIZED, errorMessage, ex);
  }
}
