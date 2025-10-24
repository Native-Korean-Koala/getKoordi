package nativekoreankoala.demo.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SuccessCode {

    // 인증 관련
    TOKEN_REFRESHED("AUTH_SUCCESS_001", "Token refreshed successfully"),
    LOGOUT_SUCCESS("AUTH_SUCCESS_002", "Logout successful"),

    // 사용자 관련
    USER_RETRIEVED("USER_SUCCESS_001", "User information retrieved successfully");

    private final String code;
    private final String message;
}