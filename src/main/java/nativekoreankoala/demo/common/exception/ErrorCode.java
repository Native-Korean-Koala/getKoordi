package nativekoreankoala.demo.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // 인증 관련
    MISSING_ACCESS_TOKEN("AUTH001", "Access token is required", HttpStatus.UNAUTHORIZED),
    INVALID_ACCESS_TOKEN("AUTH002", "Invalid or expired access token", HttpStatus.UNAUTHORIZED),
    MISSING_REFRESH_TOKEN("AUTH003", "Refresh token is required", HttpStatus.UNAUTHORIZED),
    INVALID_REFRESH_TOKEN("AUTH004", "Invalid or expired refresh token", HttpStatus.UNAUTHORIZED),

    // 사용자 관련
    USER_NOT_FOUND("USER001", "User not found", HttpStatus.NOT_FOUND),

    // 일반
    INTERNAL_SERVER_ERROR("SYS001", "Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;
}