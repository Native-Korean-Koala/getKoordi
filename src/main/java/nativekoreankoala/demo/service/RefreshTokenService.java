package nativekoreankoala.demo.service;

import lombok.RequiredArgsConstructor;
import nativekoreankoala.demo.entity.RefreshToken;
import nativekoreankoala.demo.repository.RefreshTokenRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public void saveRefreshToken(UUID userId, String token, long expirationMillis) {
        // 기존 리프레시 토큰 삭제
        refreshTokenRepository.findByUserId(userId).ifPresent(refreshTokenRepository::delete);

        // 새로운 리프레시 토큰 저장
        RefreshToken refreshToken = RefreshToken.builder()
                .userId(userId)
                .token(token)
                .expiredAt(LocalDateTime.now().plusSeconds(expirationMillis / 1000))
                .build();

        refreshTokenRepository.save(refreshToken);
    }

    @Transactional(readOnly = true)
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    @Transactional
    public void deleteByToken(String token) {
        refreshTokenRepository.deleteByToken(token);
    }

    @Transactional
    public void deleteByUserId(UUID userId) {
        refreshTokenRepository.deleteByUserId(userId);
    }

    @Transactional(readOnly = true)
    public boolean validateRefreshToken(String token) {
        Optional<RefreshToken> refreshToken = refreshTokenRepository.findByToken(token);
        return refreshToken.isPresent() && !refreshToken.get().isExpired();
    }
}