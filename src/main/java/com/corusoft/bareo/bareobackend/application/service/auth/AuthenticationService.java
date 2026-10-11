/*
 * Copyright (c) 2026 Bareo. All rights reserved.
 *
 * This software is the proprietary and confidential property of the author.
 * Unauthorized copying, distribution, or use is strictly prohibited.
 */
package com.corusoft.bareo.bareobackend.application.service.auth;

import com.corusoft.bareo.bareobackend.domain.entity.user.User;

public interface AuthenticationService {

  public User authenticateUserWithToken(String token);
}
