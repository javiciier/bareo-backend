package com.corusoft.bareo.bareobackend.domain.shared.vo;

import java.util.Locale;
import java.util.Locale.IsoCountryCode;
import java.util.Objects;
import java.util.Set;

public record ISOCountryCodeVO(
    String value
) {

  private static final Set<String> ISO_COUNTRIES = Locale.getISOCountries(IsoCountryCode.PART1_ALPHA2);

  public ISOCountryCodeVO {
    Objects.requireNonNull(value, "Country code cannot be null");

    if (value.strip().isBlank()) {
      throw new IllegalArgumentException("Country code cannot be blank");
    }
    value = value.trim().toUpperCase();

    if (value.length() != 2) {
      throw new IllegalArgumentException("Country code must be in ISO3166-1 alpha-2 format");
    }

    if (!ISO_COUNTRIES.contains(value)) {
      throw new IllegalArgumentException("Invalid country code");
    }
  }

  @Override
  public String toString() {
    return value;
  }
}
