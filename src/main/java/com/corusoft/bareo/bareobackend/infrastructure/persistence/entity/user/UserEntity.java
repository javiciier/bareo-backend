package com.corusoft.bareo.bareobackend.infrastructure.persistence.entity.user;

import com.corusoft.bareo.bareobackend.domain.shared.enums.UserGender;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.Instant;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(
    schema = "users",
    name = "user_account",
    uniqueConstraints = {
        @UniqueConstraint(name = "UQ_UserAccount_username", columnNames = {"username"}),
        @UniqueConstraint(name = "UQ_UserAccount_email", columnNames = {"email"}),
    }
)
public class UserEntity {

  @EqualsAndHashCode.Include
  @Id
  @Column(name = "id", unique = true, nullable = false, length = 128)
  private String id;

  @Column(name = "username", unique = true, length = 50, nullable = false)
  private String username;

  @Column(name = "email", unique = true, nullable = false)
  private String email;

  @Enumerated(EnumType.STRING)
  @Column(name = "gender", length = 20, nullable = false)
  private UserGender gender;

  @Column(name = "city", length = 100, nullable = false)
  private String city;

  @Column(name = "iso_country_code", length = 2, nullable = false)
  private String countryCode;

  @Column(name = "avatar_url", columnDefinition = "text")
  private String avatarUrl;

  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  @Column(name = "updated_at")
  private Instant updatedAt;
}
