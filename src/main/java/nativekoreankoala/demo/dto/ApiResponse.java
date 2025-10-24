package nativekoreankoala.demo.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nativekoreankoala.demo.common.exception.ErrorCode;
import nativekoreankoala.demo.common.exception.ErrorDetails;
import nativekoreankoala.demo.common.exception.SuccessCode;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;
    private ErrorDetails error;

    // 성공 응답 (메시지 + 데이터)
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .data(data)
                .build();
    }

    // 성공 응답 (SuccessCode + 데이터)
    public static <T> ApiResponse<T> success(SuccessCode successCode, T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(successCode.getMessage())
                .data(data)
                .build();
    }

    // 성공 응답 (SuccessCode만)
    public static <T> ApiResponse<T> success(SuccessCode successCode) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(successCode.getMessage())
                .build();
    }

    // 실패 응답
    public static <T> ApiResponse<T> error(String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .build();
    }

    // 실패 응답 (상세 에러 포함)
    public static <T> ApiResponse<T> error(ErrorCode errorCode) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(errorCode.getMessage())
                .error(ErrorDetails.of(errorCode.getCode(), errorCode.getMessage()))
                .build();
    }
}