/*
 * Copyright (c) 2026 Bareo. All rights reserved.
 *
 * This software is the proprietary and confidential property of the author.
 * Unauthorized copying, distribution, or use is strictly prohibited.
 */
package com.corusoft.bareo.bareobackend.testing.dataset;

import com.corusoft.bareo.bareobackend.domain.entity.user.User;
import com.corusoft.bareo.bareobackend.domain.entity.user.vo.UserAvatarVO;
import com.corusoft.bareo.bareobackend.domain.entity.user.vo.UserId;
import com.corusoft.bareo.bareobackend.domain.entity.user.vo.UsernameVO;
import com.corusoft.bareo.bareobackend.domain.shared.enums.UserGender;
import com.corusoft.bareo.bareobackend.domain.shared.vo.EmailVO;
import com.corusoft.bareo.bareobackend.domain.shared.vo.ISOCountryCodeVO;

public class TestUserDataset {

  // region DATA VALUES
  public static final String RAW_DEFAULT_USER_ID = "4lL2sVHVD0b9eqJIg4UpXmcZGYD3";
  public static final String RAW_DEFAULT_USER_NICKNAME = "supermario";
  public static final String RAW_DEFAULT_USER_EMAIL_DOMAIN = "@bareo.dev";
  public static final UserGender DEFAULT_USER_GENDER = UserGender.FEMALE;
  public static final String RAW_DEFAULT_USER_CITY = "A Coruña";
  public static final String RAW_DEFAULT_USER_COUNTRY = "ES";
  public static final String RAW_DEFAULT_USER_AVATAR_URL = "https://unavatar.io/github/mario";
  public static final String RAW_OTHER_DEFAULT_USER_AVATAR_URL = "https://unavatar.io/github/luigi";

  // endregion DATA VALUES

  public static User getTestUser(UserId userId) {
    return getTestuser(userId, new UsernameVO(RAW_DEFAULT_USER_NICKNAME));
  }

  public static User getTestuser(UserId userId, UsernameVO username) {
    return getTestuser(
        userId,
        new UsernameVO(RAW_DEFAULT_USER_NICKNAME),
        new EmailVO(username + RAW_DEFAULT_USER_EMAIL_DOMAIN));
  }

  public static User getTestuser(UserId userId, UsernameVO username, EmailVO email) {
    return getTestuser(
        userId, new UsernameVO(RAW_DEFAULT_USER_NICKNAME), email, DEFAULT_USER_GENDER);
  }

  public static User getTestuser(
      UserId userId, UsernameVO username, EmailVO email, UserGender gender) {
    return getTestuser(
        userId,
        new UsernameVO(RAW_DEFAULT_USER_NICKNAME),
        new EmailVO(username + RAW_DEFAULT_USER_EMAIL_DOMAIN),
        gender,
        RAW_DEFAULT_USER_CITY);
  }

  public static User getTestuser(
      UserId userId, UsernameVO username, EmailVO email, UserGender gender, String city) {
    return getTestuser(userId, username, email, gender, city, RAW_DEFAULT_USER_COUNTRY);
  }

  public static User getTestuser(
      UserId userId,
      UsernameVO username,
      EmailVO email,
      UserGender gender,
      String city,
      String countryCode) {
    ISOCountryCodeVO isoCountryCodeVO = new ISOCountryCodeVO(countryCode);

    return getTestuser(
        userId,
        username,
        email,
        gender,
        city,
        isoCountryCodeVO,
        UserAvatarVO.of(RAW_DEFAULT_USER_AVATAR_URL));
  }

  public static User getTestuser(
      UserId userId,
      UsernameVO username,
      EmailVO email,
      UserGender gender,
      String city,
      ISOCountryCodeVO countryCode,
      UserAvatarVO avatar) {
    return User.builder()
        .id(userId)
        .username(username)
        .email(email)
        .gender(gender)
        .city(city)
        .countryCode(countryCode)
        .avatar(avatar)
        .build();
  }
}
