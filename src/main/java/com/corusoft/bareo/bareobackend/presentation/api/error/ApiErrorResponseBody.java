/*
 * Copyright (c) 2026 Bareo. All rights reserved.
 *
 * This software is the proprietary and confidential property of the author.
 * Unauthorized copying, distribution, or use is strictly prohibited.
 */
package com.corusoft.bareo.bareobackend.presentation.api.error;

import java.util.List;

import lombok.Builder;

import com.corusoft.bareo.bareobackend.presentation.api.ApiResponseBody;

import com.fasterxml.jackson.annotation.JsonInclude;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_ABSENT;

@Builder
public record ApiErrorResponseBody(
    int statusCode,
    String status,
    @JsonInclude(NON_ABSENT) String message,
    @JsonInclude(NON_ABSENT) String exception,
    @JsonInclude(NON_ABSENT) String debugMessage,
    @JsonInclude(NON_ABSENT) List<? extends ApiErrorDetails> errors)
    implements ApiResponseBody {}
