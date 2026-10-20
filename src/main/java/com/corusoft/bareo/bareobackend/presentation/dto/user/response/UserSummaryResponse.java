/*
 * Copyright (c) 2026 Bareo. All rights reserved.
 *
 * This software is the proprietary and confidential property of the author.
 * Unauthorized copying, distribution, or use is strictly prohibited.
 */
package com.corusoft.bareo.bareobackend.presentation.dto.user.response;

import lombok.Builder;

import com.fasterxml.jackson.annotation.JsonInclude;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_ABSENT;

@Builder
public record UserSummaryResponse(
    String id,
    @JsonInclude(NON_ABSENT) String username,
    @JsonInclude(NON_ABSENT) String avatarUrl,
    @JsonInclude(NON_ABSENT) String city) {}
