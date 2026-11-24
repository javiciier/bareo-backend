/*
 * Copyright (c) 2026 Bareo. All rights reserved.
 *
 * This software is the proprietary and confidential property of the author.
 * Unauthorized copying, distribution, or use is strictly prohibited.
 */
package com.corusoft.bareo.bareobackend.presentation.api.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/** Represents a general exception. */
@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class ServiceException extends RuntimeException {

  /** Build the exception from the original exception. */
  public ServiceException(String message) {
    super(message);
  }

  /** Build the exception from the original exception. */
  public ServiceException(Exception ex) {
    super(ex);
  }

  /** Build the exception explaining the cause and the original exception. */
  public ServiceException(String message, Throwable cause) {
    super(message, cause);
  }
}
