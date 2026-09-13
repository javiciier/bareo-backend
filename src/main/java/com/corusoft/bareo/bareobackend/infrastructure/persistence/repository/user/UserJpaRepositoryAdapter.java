/*
 * Copyright (c) 2026 Bareo. All rights reserved.
 *
 * This software is the proprietary and confidential property of the author.
 * Unauthorized copying, distribution, or use is strictly prohibited.
 */
package com.corusoft.bareo.bareobackend.infrastructure.persistence.repository.user;

import java.util.Optional;

import com.corusoft.bareo.bareobackend.domain.user.model.User;
import com.corusoft.bareo.bareobackend.domain.user.repository.UserRepository;
import com.corusoft.bareo.bareobackend.domain.user.vo.UserId;
import com.corusoft.bareo.bareobackend.infrastructure.persistence.entity.user.UserEntity;
import com.corusoft.bareo.bareobackend.infrastructure.persistence.mapper.user.UserDatabaseEntityMapper;

import org.springframework.stereotype.Component;

import static java.util.Objects.isNull;

@Component
public class UserJpaRepositoryAdapter implements UserRepository {

  private final SpringDataUserRepository jpaRepository;
  private final UserDatabaseEntityMapper mapper;

  public UserJpaRepositoryAdapter(
      SpringDataUserRepository jpaRepository, UserDatabaseEntityMapper mapper) {
    this.jpaRepository = jpaRepository;
    this.mapper = mapper;
  }

  @Override
  public User save(User user) {
    UserEntity databaseEntity = mapper.mapToDatabase(user);
    databaseEntity = jpaRepository.save(databaseEntity);

    return mapper.mapToDomain(databaseEntity);
  }

  @Override
  public Optional<User> findById(UserId id) {
    if (isNull(id)) {
      return Optional.empty();
    }

    return jpaRepository.findById(id.value()).map(mapper::mapToDomain);
  }
}
