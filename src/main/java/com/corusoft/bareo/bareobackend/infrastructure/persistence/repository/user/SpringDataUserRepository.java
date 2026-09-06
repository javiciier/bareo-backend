package com.corusoft.bareo.bareobackend.infrastructure.persistence.repository.user;

import com.corusoft.bareo.bareobackend.infrastructure.persistence.entity.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringDataUserRepository extends JpaRepository<UserEntity, String> {

}
