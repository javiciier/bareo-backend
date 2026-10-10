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

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Builder
public record ApiErrorResponseBody(
    int statusCode,
    String status,
    @JsonInclude(NON_NULL) String message,
    @JsonInclude(NON_NULL) String debugMessage,
    @JsonInclude(NON_NULL) List<? extends ApiErrorDetails> errors)
    implements ApiResponseBody {}
