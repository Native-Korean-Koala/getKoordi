package nativekoreankoala.demo.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nativekoreankoala.demo.common.exception.BusinessException;
import nativekoreankoala.demo.common.exception.ErrorCode;
import nativekoreankoala.demo.common.exception.SuccessCode;
import nativekoreankoala.demo.dto.ApiResponse;
import nativekoreankoala.demo.dto.TokenResponse;
import nativekoreankoala.demo.dto.UserResponse;
import nativekoreankoala.demo.service.AuthService;
import nativekoreankoala.demo.util.CookieUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @GetMapping("/discord/login")
    public String discordLogin() {
        return "redirect:/oauth2/authorization/discord";
    }

    @PostMapping("/discord/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @CookieValue(name = "refreshToken", required = false) String refreshToken,
            HttpServletResponse response) {

        authService.logout(refreshToken);
        CookieUtil.deleteCookie(response, "refreshToken");

        return ResponseEntity.ok(ApiResponse.success(SuccessCode.LOGOUT_SUCCESS));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<TokenResponse>> refreshAccessToken(
            @CookieValue(name = "refreshToken", required = false) String refreshToken) {

        if (refreshToken == null) {
            throw new BusinessException(ErrorCode.MISSING_REFRESH_TOKEN);
        }

        TokenResponse tokenResponse = authService.refreshAccessToken(refreshToken);
        return ResponseEntity.ok(ApiResponse.success(SuccessCode.TOKEN_REFRESHED, tokenResponse));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser(
            @RequestHeader(name = "Authorization", required = false) String authorizationHeader) {

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new BusinessException(ErrorCode.MISSING_ACCESS_TOKEN);
        }

        String accessToken = authorizationHeader.substring(7);
        UserResponse userResponse = authService.getCurrentUser(accessToken);

        return ResponseEntity.ok(ApiResponse.success(userResponse));
    }
}