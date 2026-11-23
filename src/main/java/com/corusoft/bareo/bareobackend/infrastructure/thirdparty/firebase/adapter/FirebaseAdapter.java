/*
 * Copyright (c) 2026 Bareo. All rights reserved.
 *
 * This software is the proprietary and confidential property of the author.
 * Unauthorized copying, distribution, or use is strictly prohibited.
 */
package com.corusoft.bareo.bareobackend.infrastructure.thirdparty.firebase.adapter;

import java.time.Instant;

import com.corusoft.bareo.bareobackend.domain.entity.user.User;
import com.corusoft.bareo.bareobackend.domain.entity.user.vo.UserAvatarVO;
import com.corusoft.bareo.bareobackend.domain.entity.user.vo.UserId;
import com.corusoft.bareo.bareobackend.domain.shared.vo.EmailVO;

import com.google.firebase.auth.UserRecord;

import static java.util.Objects.isNull;

public class FirebaseAdapter {

  public static User toDomainUser(UserRecord userRecord) {
    if (isNull(userRecord)) {
      return null;
    }

    UserId id = new UserId(userRecord.getUid());
    EmailVO email = new EmailVO(userRecord.getEmail());
    UserAvatarVO avatar = UserAvatarVO.of(userRecord.getPhotoUrl());
    Instant creationDate =
        Instant.ofEpochMilli(userRecord.getUserMetadata().getCreationTimestamp());

    return User.builder().id(id).email(email).avatar(avatar).createdAt(creationDate).build();
  }
}
