/*
 * Copyright (c) 2026 Bareo. All rights reserved.
 *
 * This software is the proprietary and confidential property of the author.
 * Unauthorized copying, distribution, or use is strictly prohibited.
 */
package com.corusoft.bareo.bareobackend.domain.user.repository;

import java.util.Optional;

import com.corusoft.bareo.bareobackend.domain.user.model.User;
import com.corusoft.bareo.bareobackend.domain.user.vo.UserId;

public interface UserRepository {

  User save(User user);

  Optional<User> findById(UserId id);
}
