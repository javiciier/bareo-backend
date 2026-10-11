/*
 * Copyright (c) 2026 Bareo. All rights reserved.
 *
 * This software is the proprietary and confidential property of the author.
 * Unauthorized copying, distribution, or use is strictly prohibited.
 */
package com.corusoft.bareo.bareobackend.infrastructure.persistence;

import java.util.function.Function;

public interface DatabaseEntityMapper<DOMAIN, ENTITY> {

  DOMAIN mapToDomain(ENTITY entity);

  ENTITY mapToDatabase(DOMAIN domain);

  default <T, R> R nullable(T source, Function<T, R> getter) {
    return source != null ? getter.apply(source) : null;
  }
}
