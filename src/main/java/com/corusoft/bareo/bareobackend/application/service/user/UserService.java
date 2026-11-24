/*
 * Copyright (c) 2026 Bareo. All rights reserved.
 *
 * This software is the proprietary and confidential property of the author.
 * Unauthorized copying, distribution, or use is strictly prohibited.
 */
package com.corusoft.bareo.bareobackend.application.service.user;

import lombok.extern.slf4j.Slf4j;

import com.corusoft.bareo.bareobackend.domain.entity.user.User;
import com.corusoft.bareo.bareobackend.domain.entity.user.vo.UserId;
import com.corusoft.bareo.bareobackend.domain.exception.user.UserNotFoundException;
import com.corusoft.bareo.bareobackend.domain.repository.UserRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class UserService {

  private final UserRepository userRepository;

  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Transactional(readOnly = true)
  public User findById(UserId userId) throws UserNotFoundException {
    logger.trace("Finding user with id '{}'", userId.value());
    return userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
  }

  @Transactional(readOnly = true)
  public boolean existsById(UserId userId) {
    try {
      findById(userId);
    } catch (UserNotFoundException e) {
      return false;
    }

    return true;
  }

  @Transactional()
  public User saveUser(User user) {
    logger.trace("Saving user with id '{}'", user.getId());
    return userRepository.save(user);
  }

  @Transactional()
  public void deleteUser(UserId userId) throws IllegalArgumentException {
    if (!existsById(userId)) {
      throw new UserNotFoundException(userId);
    }

    logger.trace("Deleting user with id '{}'", userId);
    userRepository.delete(userId);
  }
}
