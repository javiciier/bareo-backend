/*
 * Copyright (c) 2026 Bareo. All rights reserved.
 *
 * This software is the proprietary and confidential property of the author.
 * Unauthorized copying, distribution, or use is strictly prohibited.
 */
package com.corusoft.bareo.bareobackend.infrastructure.persistence;

public interface DatabaseEntityMapper<DOMAIN, ENTITY> {

  DOMAIN mapToDomain(ENTITY entity);

  ENTITY mapToDatabase(DOMAIN domain);
}
