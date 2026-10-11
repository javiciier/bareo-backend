/*
 * Copyright (c) 2026 Bareo. All rights reserved.
 *
 * This software is the proprietary and confidential property of the author.
 * Unauthorized copying, distribution, or use is strictly prohibited.
 */
package com.corusoft.bareo.bareobackend.domain.exception.auth;

import com.corusoft.bareo.bareobackend.domain.exception.DomainException;

public class InvalidTokenException extends DomainException {

  public InvalidTokenException() {
    super(InvalidTokenException.class);
  }
}
