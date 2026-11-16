/*
 * Copyright (c) 2026 Bareo. All rights reserved.
 *
 * This software is the proprietary and confidential property of the author.
 * Unauthorized copying, distribution, or use is strictly prohibited.
 */
package com.corusoft.bareo.bareobackend.testing.unit.utils;

import com.corusoft.bareo.bareobackend.domain.entity.user.User;
import com.corusoft.bareo.bareobackend.domain.entity.user.vo.UserAvatar;
import com.corusoft.bareo.bareobackend.domain.entity.user.vo.UserId;
import com.corusoft.bareo.bareobackend.domain.entity.user.vo.UsernameVO;
import com.corusoft.bareo.bareobackend.domain.shared.enums.UserGender;
import com.corusoft.bareo.bareobackend.domain.shared.vo.EmailVO;
import com.corusoft.bareo.bareobackend.domain.shared.vo.ISOCountryCodeVO;

public class TestDataFactory {

  public static User createTestUser(UserId userId, String username) {
    return User.builder()
        .id(userId)
        .username(new UsernameVO(username))
        .email(new EmailVO(username + "@bareo.com"))
        .gender(UserGender.MALE)
        .city("A Coruña")
        .countryCode(new ISOCountryCodeVO("ES"))
        .avatar(UserAvatar.of("https://example.com/avatar.png"))
        .build();
  }
}
