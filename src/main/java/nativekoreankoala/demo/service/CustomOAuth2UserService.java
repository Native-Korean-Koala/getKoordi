package nativekoreankoala.demo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nativekoreankoala.demo.entity.User;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserService userService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String discordId = oAuth2User.getAttribute("id");
        String username = oAuth2User.getAttribute("username");
        String email = oAuth2User.getAttribute("email");
        String avatar = oAuth2User.getAttribute("avatar");
        String discriminator = oAuth2User.getAttribute("discriminator");

        User user = userService.saveOrUpdateUser(discordId, username, email, avatar, discriminator);

        return oAuth2User;
    }
}