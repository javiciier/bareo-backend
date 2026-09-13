/*
 * Copyright (c) 2026 Bareo. All rights reserved.
 *
 * This software is the proprietary and confidential property of the author.
 * Unauthorized copying, distribution, or use is strictly prohibited.
 */
package com.corusoft.bareo.bareobackend.domain.user.model;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import com.corusoft.bareo.bareobackend.domain.shared.enums.UserGender;
import com.corusoft.bareo.bareobackend.domain.shared.vo.EmailVO;
import com.corusoft.bareo.bareobackend.domain.shared.vo.ISOCountryCodeVO;
import com.corusoft.bareo.bareobackend.domain.user.vo.UserAvatar;
import com.corusoft.bareo.bareobackend.domain.user.vo.UserId;
import com.corusoft.bareo.bareobackend.domain.user.vo.UsernameVO;

@Getter
@AllArgsConstructor
@Builder(toBuilder = true)
public class User {

  private final UserId id;
  private final UsernameVO username;
  private final EmailVO email;
  private final UserGender gender;
  private final String city;
  private final ISOCountryCodeVO countryCode;
  private final UserAvatar avatar;
  private final Instant createdAt;
  private final Instant updatedAt;
}
