/*
 * Copyright (c) 2026 Bareo. All rights reserved.
 *
 * This software is the proprietary and confidential property of the author.
 * Unauthorized copying, distribution, or use is strictly prohibited.
 */
package com.corusoft.bareo.bareobackend.infrastructure.config.i18n;

import java.util.List;
import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

@Configuration
public class LocalizationConfiguration {

  private static final List<Locale> SUPPORTED_LOCALES;
  private static final Locale ENGLISH_LOCALE = Locale.of("en");
  private static final Locale SPANISH_LOCALE = Locale.of("es");
  public static final Locale DEFAULT_LOCALE = ENGLISH_LOCALE;

  static {
    SUPPORTED_LOCALES = List.of(DEFAULT_LOCALE, SPANISH_LOCALE);
  }

  @Bean
  public LocaleResolver localeResolver() {
    AcceptHeaderLocaleResolver headerLocaleResolver = new AcceptHeaderLocaleResolver();
    headerLocaleResolver.setSupportedLocales(SUPPORTED_LOCALES);
    headerLocaleResolver.setDefaultLocale(SPANISH_LOCALE);

    return headerLocaleResolver;
  }

  @Bean
  public MessageSource messageSource() {
    ReloadableResourceBundleMessageSource source = new ReloadableResourceBundleMessageSource();
    source.setDefaultLocale(DEFAULT_LOCALE);
    source.setDefaultEncoding("UTF-8");
    source.addBasenames("classpath:l10n/messages", "classpath:l10n/spring");

    return source;
  }
}
