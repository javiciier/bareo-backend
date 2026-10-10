/*
 * Copyright (c) 2026 Bareo. All rights reserved.
 *
 * This software is the proprietary and confidential property of the author.
 * Unauthorized copying, distribution, or use is strictly prohibited.
 */
package com.corusoft.bareo.bareobackend.presentation.api;

import java.time.Instant;

public record ApiResponse<T>(boolean success, Instant timestamp, T data) {

  public static <T> Builder<T> builder() {
    return new Builder<>();
  }

  public static class Builder<T> {

    private boolean success;
    private Instant timestamp;
    private T data;

    public Builder<T> success(boolean success) {
      this.success = success;
      return this;
    }

    public Builder<T> timestamp(Instant timestamp) {
      this.timestamp = timestamp;
      return this;
    }

    public Builder<T> data(T data) {
      this.data = data;
      return this;
    }

    public ApiResponse<T> build() {
      return new ApiResponse<>(success, timestamp, data);
    }
  }
}
