/*
 * Copyright (c) 2026 Bareo. All rights reserved.
 *
 * This software is the proprietary and confidential property of the author.
 * Unauthorized copying, distribution, or use is strictly prohibited.
 */
package com.corusoft.bareo.bareobackend.infrastructure.persistence;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import static java.util.Objects.isNull;

public abstract class AbstractJpaRepositoryAdapter<DOMAIN, ENTITY, ID> {

  protected final JpaRepository<ENTITY, ID> jpaRepository;
  protected final DatabaseEntityMapper<DOMAIN, ENTITY> databaseEntityMapper;

  public AbstractJpaRepositoryAdapter(
      JpaRepository<ENTITY, ID> jpaRepository,
      DatabaseEntityMapper<DOMAIN, ENTITY> databaseEntityMapper) {
    this.jpaRepository = jpaRepository;
    this.databaseEntityMapper = databaseEntityMapper;
  }

  public DOMAIN save(DOMAIN entity) {
    ENTITY entityToSave = databaseEntityMapper.mapToDatabase(entity);
    entityToSave = jpaRepository.save(entityToSave);

    return databaseEntityMapper.mapToDomain(entityToSave);
  }

  protected Optional<DOMAIN> findByPrimitiveId(ID id) {
    if (isNull(id)) {
      return Optional.empty();
    }

    return jpaRepository.findById(id).map(databaseEntityMapper::mapToDomain);
  }
}
