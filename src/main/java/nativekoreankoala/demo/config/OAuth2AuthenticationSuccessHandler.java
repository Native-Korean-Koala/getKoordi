package nativekoreankoala.demo.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nativekoreankoala.demo.service.RefreshTokenService;
import nativekoreankoala.demo.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final UserService userService;
    private final JwtProperties jwtProperties;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String discordId = oAuth2User.getAttribute("id");

        // DB에서 사용자 정보 조회
        nativekoreankoala.demo.entity.User user = userService.getUserByDiscordId(discordId);

        if (user == null) {
            log.error("User not found for discordId: {}", discordId);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "User not found");
            return;
        }

        // JWT 토큰 생성
        String accessToken = jwtTokenProvider.createAccessToken(
                user.getId(),
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );

        String refreshToken = jwtTokenProvider.createRefreshToken(user.getId());

        // Refresh Token을 DB에 저장
        refreshTokenService.saveRefreshToken(
                user.getId(),
                refreshToken,
                jwtProperties.getRefreshTokenExpiration()
        );

        // Refresh Token을 HttpOnly 쿠키에 저장
        Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(true); // HTTPS 환경에서만 전송
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(7 * 24 * 60 * 60); // 7일

        response.addCookie(refreshTokenCookie);

        // 프론트엔드로 리다이렉트 (Access Token을 쿼리 파라미터로 전달)
        String targetUrl = UriComponentsBuilder.fromUriString("http://localhost:3000/auth/callback") // 프론트엔드 콜백 URL
                .queryParam("accessToken", accessToken)
                .build()
                .toUriString();

        log.info("OAuth2 login success for user: {}", user.getDiscordId());
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}