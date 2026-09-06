package com.corusoft.bareo.bareobackend.infrastructure.persistence.repository.user;

import static java.util.Objects.isNull;

import com.corusoft.bareo.bareobackend.domain.user.model.User;
import com.corusoft.bareo.bareobackend.domain.user.repository.UserRepository;
import com.corusoft.bareo.bareobackend.domain.user.vo.UserId;
import com.corusoft.bareo.bareobackend.infrastructure.persistence.entity.user.UserEntity;
import com.corusoft.bareo.bareobackend.infrastructure.persistence.mapper.user.UserDatabaseEntityMapper;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class UserJpaRepositoryAdapter implements UserRepository {

  private final SpringDataUserRepository jpaRepository;
  private final UserDatabaseEntityMapper mapper;

  public UserJpaRepositoryAdapter(
      SpringDataUserRepository jpaRepository,
      UserDatabaseEntityMapper mapper
  ) {
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
