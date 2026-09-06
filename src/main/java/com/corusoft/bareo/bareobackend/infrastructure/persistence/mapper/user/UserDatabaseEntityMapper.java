package com.corusoft.bareo.bareobackend.infrastructure.persistence.mapper.user;

import static java.util.Objects.isNull;

import com.corusoft.bareo.bareobackend.domain.shared.vo.EmailVO;
import com.corusoft.bareo.bareobackend.domain.shared.vo.ISOCountryCodeVO;
import com.corusoft.bareo.bareobackend.domain.user.model.User;
import com.corusoft.bareo.bareobackend.domain.user.vo.UserAvatar;
import com.corusoft.bareo.bareobackend.domain.user.vo.UserId;
import com.corusoft.bareo.bareobackend.domain.user.vo.UsernameVO;
import com.corusoft.bareo.bareobackend.infrastructure.persistence.DatabaseEntityMapper;
import com.corusoft.bareo.bareobackend.infrastructure.persistence.entity.user.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class UserDatabaseEntityMapper implements DatabaseEntityMapper<User, UserEntity> {

  @Override
  public User mapToDomain(UserEntity entity) {
    if (isNull(entity)) {
      return null;
    }
    UserId userId = new UserId(entity.getId());
    UsernameVO username = new UsernameVO(entity.getUsername());
    EmailVO emailVO = new EmailVO(entity.getEmail());
    ISOCountryCodeVO countryCode = new ISOCountryCodeVO(entity.getCountryCode());
    UserAvatar userAvatar = UserAvatar.of(entity.getAvatarUrl());

    return User.builder()
        .id(userId)
        .username(username)
        .email(emailVO)
        .city(entity.getCity())
        .countryCode(countryCode)
        .avatar(userAvatar)
        .createdAt(entity.getCreatedAt())
        .updatedAt(entity.getUpdatedAt())
        .build();
  }

  @Override
  public UserEntity mapToDatabase(User domain) {
    if (isNull(domain)) {
      return null;
    }
    UserEntity entity = new UserEntity();

    if (!isNull(domain.getId())) {
      entity.setId(domain.getId().value());
    }
    entity.setUsername(domain.getUsername().value());
    entity.setEmail(domain.getEmail().value());
    entity.setGender(domain.getGender());
    entity.setCity(domain.getCity());
    entity.setCountryCode(domain.getCountryCode().value());
    entity.setAvatarUrl(domain.getAvatar().value().toString());
    entity.setCreatedAt(domain.getCreatedAt());
    entity.setUpdatedAt(domain.getUpdatedAt());

    return entity;
  }
}
