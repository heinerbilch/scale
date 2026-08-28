package eu.bilch.scale.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    @GetMapping("/oauth2/authorization/google")
    public String redirectToGoogle() {
        return "redirect:/oauth2/authorization/google";
    }
}
