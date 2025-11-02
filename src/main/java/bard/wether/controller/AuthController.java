package bard.wether.controller;

import bard.wether.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public String register(@RequestParam String username, @RequestParam String password) {
        return authService.registerUser(username, password);
    }

    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        HttpServletResponse response) {
        return authService.loginUser(username, password, response);
    }

    @PostMapping("/logout")
    public String logout(@CookieValue(value = "SESSION_ID", required = false) String sessionId,
                         HttpServletResponse response) {
        return authService.logoutUser(sessionId, response);
    }
}