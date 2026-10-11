/*
 * Copyright (c) 2026 Bareo. All rights reserved.
 *
 * This software is the proprietary and confidential property of the author.
 * Unauthorized copying, distribution, or use is strictly prohibited.
 */
package com.corusoft.bareo.bareobackend.domain.exception;

import lombok.Getter;

/** Customized exception that can be thrown during the execution of the application. */
@Getter
public abstract class DomainException extends RuntimeException {

  /** Key to translate the exception message. */
  protected final String i18nKey;

  /** Arguments to replace in the exception message placeholder. */
  protected final transient Object[] i18nArgs;

  private DomainException(String i18nKey, Object[] i18nArgs) {
    this.i18nKey = i18nKey;
    this.i18nArgs = i18nArgs;
  }

  protected <E extends DomainException> DomainException(Class<E> clazz) {
    this(clazz.getName(), null);
  }

  protected <E extends DomainException> DomainException(Class<E> clazz, Object... i18nArgs) {
    this(clazz.getName(), i18nArgs);
  }
}
