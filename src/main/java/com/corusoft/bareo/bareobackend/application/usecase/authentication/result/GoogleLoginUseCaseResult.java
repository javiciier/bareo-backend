/*
 * Copyright (c) 2026 Bareo. All rights reserved.
 *
 * This software is the proprietary and confidential property of the author.
 * Unauthorized copying, distribution, or use is strictly prohibited.
 */
package com.corusoft.bareo.bareobackend.application.usecase.authentication.result;

import com.corusoft.bareo.bareobackend.domain.entity.user.User;

public record GoogleLoginUseCaseResult(User authenticatedUser) {}
