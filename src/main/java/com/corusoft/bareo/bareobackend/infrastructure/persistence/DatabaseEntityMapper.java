package com.corusoft.bareo.bareobackend.infrastructure.persistence;

public interface DatabaseEntityMapper<DOMAIN, ENTITY> {

  DOMAIN mapToDomain(ENTITY entity);

  ENTITY mapToDatabase(DOMAIN domain);

}
