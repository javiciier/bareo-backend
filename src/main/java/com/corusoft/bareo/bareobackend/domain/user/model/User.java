package com.corusoft.bareo.bareobackend.domain.user.model;

import com.corusoft.bareo.bareobackend.domain.shared.enums.UserGender;
import com.corusoft.bareo.bareobackend.domain.shared.vo.EmailVO;
import com.corusoft.bareo.bareobackend.domain.shared.vo.ISOCountryCodeVO;
import com.corusoft.bareo.bareobackend.domain.user.vo.UserAvatar;
import com.corusoft.bareo.bareobackend.domain.user.vo.UserId;
import com.corusoft.bareo.bareobackend.domain.user.vo.UsernameVO;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder(toBuilder = true)
public class User {

  private final UserId id;
  private final UsernameVO username;
  private final UserGender gender;
  private final EmailVO email;
  private final String city;
  private final ISOCountryCodeVO countryCode;
  private final String biography;
  private final UserAvatar avatar;
  private final Instant createdAt;

}
