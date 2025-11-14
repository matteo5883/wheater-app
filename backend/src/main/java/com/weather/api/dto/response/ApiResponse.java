package com.weather.api.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Standard API response wrapper
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Standard API response")
public class ApiResponse<T> {

  @Schema(description = "Response data", example = "{}")
  private T data;

  @Schema(description = "Response status", example = "success")
  private String status;

  @Schema(description = "Response message", example = "Operation completed successfully")
  private String message;

  @Schema(description = "Error details", example = "null")
  private String error;

  @Schema(description = "Response timestamp", example = "1234567890")
  private Long timestamp;

  public static <T> ApiResponse<T> success(T data) {
    return ApiResponse.<T>builder()
            .data(data)
            .status("success")
            .timestamp(System.currentTimeMillis())
            .build();
  }

  public static <T> ApiResponse<T> error(String error) {
    return ApiResponse.<T>builder()
            .status("error")
            .error(error)
            .timestamp(System.currentTimeMillis())
            .build();
  }

  public static <T> ApiResponse<T> error(String error, String message) {
    return ApiResponse.<T>builder()
            .status("error")
            .error(error)
            .message(message)
            .timestamp(System.currentTimeMillis())
            .build();
  }
}
