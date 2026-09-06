package com.corusoft.bareo.bareobackend.domain.user.repository;

import com.corusoft.bareo.bareobackend.domain.user.model.User;
import com.corusoft.bareo.bareobackend.domain.user.vo.UserId;
import java.util.Optional;

public interface UserRepository {

  User save(User user);

  Optional<User> findById(UserId id);

}
