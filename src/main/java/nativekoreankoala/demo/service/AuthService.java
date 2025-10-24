package nativekoreankoala.demo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nativekoreankoala.demo.config.JwtProperties;
import nativekoreankoala.demo.config.JwtTokenProvider;
import nativekoreankoala.demo.dto.TokenResponse;
import nativekoreankoala.demo.dto.UserResponse;
import nativekoreankoala.demo.entity.RefreshToken;
import nativekoreankoala.demo.entity.User;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final UserService userService;
    private final JwtProperties jwtProperties;

    /**
     * Refresh Token으로 새로운 Access Token 발급
     */
    public TokenResponse refreshAccessToken(String refreshToken) {
        // Refresh Token 유효성 검증
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new IllegalArgumentException("Invalid refresh token");
        }

        // DB에서 Refresh Token 확인
        Optional<RefreshToken> storedToken = refreshTokenService.findByToken(refreshToken);
        if (storedToken.isEmpty() || storedToken.get().isExpired()) {
            throw new IllegalArgumentException("Refresh token expired or not found");
        }

        // 사용자 정보 조회
        UUID userId = jwtTokenProvider.getUserId(refreshToken);
        User user = userService.getUserById(userId);

        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }

        // 새로운 Access Token 생성
        String newAccessToken = jwtTokenProvider.createAccessToken(
                user.getId(),
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );

        log.info("Access token refreshed for user: {}", user.getDiscordId());

        return TokenResponse.builder()
                .accessToken(newAccessToken)
                .tokenType("Bearer")
                .expiresIn(jwtProperties.getAccessTokenExpiration() / 1000)
                .build();
    }

    /**
     * 로그아웃 처리 (Refresh Token 삭제)
     */
    public void logout(String refreshToken) {
        if (refreshToken != null) {
            refreshTokenService.deleteByToken(refreshToken);
        }
    }

    /**
     * Access Token으로 현재 사용자 정보 조회
     */
    public UserResponse getCurrentUser(String accessToken) {
        if (!jwtTokenProvider.validateToken(accessToken)) {
            throw new IllegalArgumentException("Invalid or expired access token");
        }

        UUID userId = jwtTokenProvider.getUserId(accessToken);
        User user = userService.getUserById(userId);

        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }

        return UserResponse.builder()
                .id(user.getId())
                .discordId(user.getDiscordId())
                .username(user.getUsername())
                .email(user.getEmail())
                .avatar(user.getAvatar())
                .build();
    }
}