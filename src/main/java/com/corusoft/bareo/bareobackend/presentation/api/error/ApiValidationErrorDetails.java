/*
 * Copyright (c) 2026 Bareo. All rights reserved.
 *
 * This software is the proprietary and confidential property of the author.
 * Unauthorized copying, distribution, or use is strictly prohibited.
 */
package com.corusoft.bareo.bareobackend.presentation.api.error;

import org.springframework.validation.FieldError;

public record ApiValidationErrorDetails(
    String objectName, String fieldName, Object rejectedValue, String message)
    implements ApiErrorDetails {

  public ApiValidationErrorDetails(FieldError fieldError) {
    this(
        fieldError.getObjectName(),
        fieldError.getField(),
        fieldError.getRejectedValue(),
        fieldError.getDefaultMessage());
  }
}
