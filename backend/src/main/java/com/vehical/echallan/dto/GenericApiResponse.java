package com.vehical.echallan.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GenericApiResponse<T> {

    private boolean success;
    private String message;
    private T data;
    private String errorCode;
    private LocalDateTime timestamp;

    public static <T> GenericApiResponse<T> success(String message, T data) {
        return GenericApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> GenericApiResponse<T> error(String message, String errorCode) {
        return GenericApiResponse.<T>builder()
                .success(false)
                .message(message)
                .errorCode(errorCode)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> GenericApiResponse<T> error(String message) {
        return error(message, null);
    }
}
