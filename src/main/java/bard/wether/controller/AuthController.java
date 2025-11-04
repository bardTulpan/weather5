package bard.wether.controller;

import bard.wether.dto.ApiResponse;
import bard.wether.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ApiResponse<Void> register(@RequestParam String username, @RequestParam String password) {
        authService.registerUser(username, password);
        return ApiResponse.success("User registered successfully", null);
    }

    @PostMapping("/login")
    public ApiResponse<Void> login(@RequestParam String username,
                        @RequestParam String password,
                        HttpServletResponse response) {
        authService.loginUser(username, password, response);
        return ApiResponse.success("User logged successfully", null);
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(@CookieValue(value = "SESSION_ID", required = false) String sessionId,
                                      HttpServletResponse response) {
        authService.logoutUser(sessionId, response);
        return ApiResponse.success("User logged out", null);
    }
}