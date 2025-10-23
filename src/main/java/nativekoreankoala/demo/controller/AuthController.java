package nativekoreankoala.demo.controller;

import lombok.RequiredArgsConstructor;
import nativekoreankoala.demo.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @GetMapping("/auth/discord")
    public String discordLogin() {
        return "redirect:/oauth2/authorization/discord";
    }
}