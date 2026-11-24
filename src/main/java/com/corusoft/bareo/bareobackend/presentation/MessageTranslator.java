/*
 * Copyright (c) 2026 Bareo. All rights reserved.
 *
 * This software is the proprietary and confidential property of the author.
 * Unauthorized copying, distribution, or use is strictly prohibited.
 */
package com.corusoft.bareo.bareobackend.presentation;

import java.util.Locale;

import lombok.extern.slf4j.Slf4j;

import com.corusoft.bareo.bareobackend.domain.exception.DomainException;
import com.corusoft.bareo.bareobackend.infrastructure.config.i18n.LocalizationConfiguration;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import static java.util.Objects.isNull;

@Slf4j
@Component
public class MessageTranslator {

  private static final String NO_TRANSLATION_AVAILABLE_KEY = "no_translation_available";
  private static final String NO_TRANSLATION_AVAILABLE_MESSAGE = "no translation available";

  private final MessageSource messageSource;

  public MessageTranslator(MessageSource messageSource) {
    this.messageSource = messageSource;
  }

  /**
   * Returns the translation for the given {@link DomainException}.
   *
   * @param e Exception to translate
   * @return The translated message for the given exception
   */
  public String generateMessage(DomainException e) {
    return generateMessage(e.getI18nKey(), e.getI18nArgs());
  }

  /**
   * Returns the translation for the given {@code key}.
   *
   * @param key Key of the message to translate
   * @return The translated message
   */
  public String generateMessage(String key) {
    return generateMessage(key, null);
  }

  /**
   * Returns the translation for the given {@code key} using the given {@code locale}.
   *
   * @param key Key of the message to translate
   * @param args Values for the placeholders in the message template
   * @return The translated message
   */
  public String generateMessage(String key, Object[] args) {
    return generateMessage(key, args, null);
  }

  /**
   * Returns the translation for the given {@code key} using the given {@code locale}.<br>
   * Placeholders in the message template will be replaced in order by each item of {@code args}.
   *
   * @param key Key of the message to translate
   * @param locale Locale to use for the translation
   * @param args Values for the placeholders in the message template
   * @return The translated message
   */
  public String generateMessage(String key, Object[] args, Locale locale) {
    if (isNull(locale)) {
      locale = LocalizationConfiguration.DEFAULT_LOCALE;
    }

    // Translate the message with the given key
    String message = messageSource.getMessage(key, args, null, locale);

    // If no translation available, return a fallback message
    if (isNull(message)) {
      logger.warn(
          "No translation found for key '{}' using locale '{}'. Generating fallback message",
          key,
          locale);
      return generateFallbackMessage(key, locale);
    }

    return message;
  }

  private String generateFallbackMessage(String key, Locale locale) {
    Object[] args = new Object[] {key};

    String fallbackMessage =
        messageSource.getMessage(
            NO_TRANSLATION_AVAILABLE_KEY, args, NO_TRANSLATION_AVAILABLE_MESSAGE, locale);

    if (isNull(fallbackMessage)) {
      logger.warn("No fallback translation was found");
    }

    return fallbackMessage;
  }
}
