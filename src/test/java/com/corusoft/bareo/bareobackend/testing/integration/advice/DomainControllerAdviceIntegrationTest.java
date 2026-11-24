/*
 * Copyright (c) 2026 Bareo. All rights reserved.
 *
 * This software is the proprietary and confidential property of the author.
 * Unauthorized copying, distribution, or use is strictly prohibited.
 */
package com.corusoft.bareo.bareobackend.testing.integration.advice;

import com.corusoft.bareo.bareobackend.presentation.controller.advice.DomainControllerAdvice;
import com.corusoft.bareo.bareobackend.testing.config.annotation.WebControllerTest;

@WebControllerTest(controllers = {DomainControllerAdvice.class})
public class DomainControllerAdviceIntegrationTest {}
