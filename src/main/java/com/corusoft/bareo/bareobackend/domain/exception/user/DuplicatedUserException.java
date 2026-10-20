/*
 * Copyright (c) 2026 Bareo. All rights reserved.
 *
 * This software is the proprietary and confidential property of the author.
 * Unauthorized copying, distribution, or use is strictly prohibited.
 */
package com.corusoft.bareo.bareobackend.domain.exception.user;

import com.corusoft.bareo.bareobackend.domain.entity.user.vo.UserId;
import com.corusoft.bareo.bareobackend.domain.exception.DomainException;

public class DuplicatedUserException extends DomainException {

  public DuplicatedUserException(UserId userId) {
    super(DuplicatedUserException.class, userId);
  }
}
