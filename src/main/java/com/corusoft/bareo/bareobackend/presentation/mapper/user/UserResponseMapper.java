/*
 * Copyright (c) 2026 Bareo. All rights reserved.
 *
 * This software is the proprietary and confidential property of the author.
 * Unauthorized copying, distribution, or use is strictly prohibited.
 */
package com.corusoft.bareo.bareobackend.presentation.mapper.user;

import com.corusoft.bareo.bareobackend.domain.entity.user.User;
import com.corusoft.bareo.bareobackend.domain.entity.user.vo.UserAvatar;
import com.corusoft.bareo.bareobackend.domain.entity.user.vo.UsernameVO;
import com.corusoft.bareo.bareobackend.presentation.dto.user.response.UserSummaryResponse;

import static com.corusoft.bareo.bareobackend.shared.utils.mapper.ResponseMapperUtils.nullable;

public class UserResponseMapper {

  public static UserSummaryResponse toUserSummaryResponse(User entity) {
    return new UserSummaryResponse(
        entity.getId().toString(),
        nullable(entity.getUsername(), UsernameVO::toString),
        entity.getCity(),
        nullable(entity.getAvatar(), UserAvatar::toString));
  }
}
