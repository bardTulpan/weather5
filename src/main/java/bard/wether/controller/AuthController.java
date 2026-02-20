package bard.wether.controller;

import bard.wether.dto.in.AuthRequest;
import bard.wether.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public void register(@ModelAttribute AuthRequest authRequest) {
        authService.registerUser(authRequest.getUsername(), authRequest.getPassword());
    }

    @PostMapping("/login")
    public void login(@ModelAttribute AuthRequest authRequest,
                      HttpServletResponse response) {
        authService.loginUser(authRequest.getUsername(), authRequest.getPassword(), response);
    }

    @PostMapping("/logout")
    public void logout(@CookieValue(value = "SESSION_ID", required = false) String sessionId,
                       HttpServletResponse response) {
        authService.logoutUser(sessionId, response);
    }
}