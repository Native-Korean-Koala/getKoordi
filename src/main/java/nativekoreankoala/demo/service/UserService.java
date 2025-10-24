package nativekoreankoala.demo.service;

import lombok.RequiredArgsConstructor;
import nativekoreankoala.demo.entity.User;
import nativekoreankoala.demo.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public User saveOrUpdateUser(String discordId, String username, String email, String avatar, String discriminator) {
        return userRepository.findByDiscordId(discordId)
                .map(existingUser -> {
                    existingUser.setUsername(username);
                    existingUser.setEmail(email);
                    existingUser.setAvatar(avatar);
                    existingUser.setDiscriminator(discriminator);
                    existingUser.setLastLoginAt(LocalDateTime.now());
                    return userRepository.save(existingUser);
                })
                .orElseGet(() -> {
                    User newUser = User.builder()
                            .discordId(discordId)
                            .username(username)
                            .email(email)
                            .avatar(avatar)
                            .discriminator(discriminator)
                            .build();
                    return userRepository.save(newUser);
                });
    }

    public User getUserById(UUID userId) {
        return userRepository.findById(userId).orElse(null);
    }

    public User getUserByDiscordId(String discordId) {
        return userRepository.findByDiscordId(discordId).orElse(null);
    }
}