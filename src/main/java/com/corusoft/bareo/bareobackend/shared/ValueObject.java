/*
 * Copyright (c) 2026 Bareo. All rights reserved.
 *
 * This software is the proprietary and confidential property of the author.
 * Unauthorized copying, distribution, or use is strictly prohibited.
 */
package com.corusoft.bareo.bareobackend.shared;

public interface ValueObject<T> {

  T value();

  static String asString(ValueObject<?> vo) {
    return "%s".formatted(vo.value());
  }
}
