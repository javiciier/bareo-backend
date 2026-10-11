/*
 * Copyright (c) 2026 Bareo. All rights reserved.
 *
 * This software is the proprietary and confidential property of the author.
 * Unauthorized copying, distribution, or use is strictly prohibited.
 */
package com.corusoft.bareo.bareobackend.domain.exception.user;

import com.corusoft.bareo.bareobackend.domain.entity.user.vo.UserId;
import com.corusoft.bareo.bareobackend.domain.exception.DomainException;

public class UserNotFoundException extends DomainException {

  private final UserId userId;

  public UserNotFoundException(UserId userId) {
    super(UserNotFoundException.class, userId);
    this.userId = userId;
  }
}
