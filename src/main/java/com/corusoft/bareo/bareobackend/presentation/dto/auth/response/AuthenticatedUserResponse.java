/*
 * Copyright (c) 2026 Bareo. All rights reserved.
 *
 * This software is the proprietary and confidential property of the author.
 * Unauthorized copying, distribution, or use is strictly prohibited.
 */
package com.corusoft.bareo.bareobackend.presentation.dto.auth.response;

import com.corusoft.bareo.bareobackend.presentation.dto.user.response.UserSummaryResponse;

import com.fasterxml.jackson.annotation.JsonProperty;

import static com.corusoft.bareo.bareobackend.infrastructure.config.security.SecurityConstants.TOKEN_ATTRIBUTE_NAME;

public record AuthenticatedUserResponse(
    @JsonProperty(value = TOKEN_ATTRIBUTE_NAME) String token,
    @JsonProperty(value = "user") UserSummaryResponse userSummary) {}
