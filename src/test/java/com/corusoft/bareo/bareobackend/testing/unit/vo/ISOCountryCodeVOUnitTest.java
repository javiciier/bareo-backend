/*
 * Copyright (c) 2026 Bareo. All rights reserved.
 *
 * This software is the proprietary and confidential property of the author.
 * Unauthorized copying, distribution, or use is strictly prohibited.
 */
package com.corusoft.bareo.bareobackend.testing.unit.vo;

import com.corusoft.bareo.bareobackend.domain.shared.vo.ISOCountryCodeVO;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ISOCountryCodeVOUnitTest {

  @Test
  void when_isNull_then_fail() {
    NullPointerException exception =
        assertThrows(NullPointerException.class, () -> new ISOCountryCodeVO((null)));
    assertEquals("Country code cannot be null", exception.getMessage());
  }

  @Test
  void when_isEmptyS_then_fail() {
    IllegalArgumentException exception =
        assertThrows(IllegalArgumentException.class, () -> new ISOCountryCodeVO(""));
    assertEquals("Country code cannot be blank", exception.getMessage());
  }

  @Test
  void when_isBlankString_then_fail() {
    IllegalArgumentException exception =
        assertThrows(IllegalArgumentException.class, () -> new ISOCountryCodeVO(" "));
    assertEquals("Country code cannot be blank", exception.getMessage());
  }

  @ParameterizedTest
  @ValueSource(strings = {"ES", "PT", "FR", "GB", "US"})
  void when_toString_then_ok(String locale) {
    assertEquals(locale, new ISOCountryCodeVO(locale).toString());
  }

  @ParameterizedTest
  @ValueSource(strings = {"ES", "PT", "FR", "GB", "US"})
  void when_equals_then_ok(String locale) {
    assertEquals(new ISOCountryCodeVO(locale), new ISOCountryCodeVO(locale));
  }

  @ParameterizedTest
  @ValueSource(strings = {"ESP", "PRT", "FRA", "GBR", "USA"})
  void when_isNotAlpha2Format_then_fail(String locale) {
    IllegalArgumentException exception =
        assertThrows(IllegalArgumentException.class, () -> new ISOCountryCodeVO(locale));
    assertEquals("Country code must be in ISO3166-1 alpha-2 format", exception.getMessage());
  }

  @Test
  void when_isNotSupported_then_fail() {
    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> new ISOCountryCodeVO("A1"),
            "Invalid country code");
    assertEquals("Invalid country code", exception.getMessage());
  }
}
