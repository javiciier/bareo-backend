/*
 * Copyright (c) 2026 Bareo. All rights reserved.
 *
 * This software is the proprietary and confidential property of the author.
 * Unauthorized copying, distribution, or use is strictly prohibited.
 */
package com.corusoft.bareo.bareobackend.presentation.mapper.auth;

import com.corusoft.bareo.bareobackend.domain.entity.user.User;
import com.corusoft.bareo.bareobackend.presentation.dto.auth.response.AuthenticatedUserResponse;
import com.corusoft.bareo.bareobackend.presentation.dto.user.response.UserSummaryResponse;
import com.corusoft.bareo.bareobackend.presentation.mapper.user.UserResponseMapper;

public class AuthenticationResponseMapper {

  public static AuthenticatedUserResponse toAuthenticatedUserResponse(String token, User user) {
    UserSummaryResponse userSummary = UserResponseMapper.toUserSummaryResponse(user);

    return new AuthenticatedUserResponse(token, userSummary);
  }
}
