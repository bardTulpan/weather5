package bard.wether.service;

import bard.wether.entity.Session;
import bard.wether.entity.User;
import bard.wether.exceptions.AlreadyExistsException;
import bard.wether.exceptions.InvalidCredException;
import bard.wether.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SessionService sessionService;
    @Value("${cookie.max.age}")
    private int cookieMaxAge;
    @Value("${cookie.default.name}")
    private String defaultCookieName;
    @Value("${server.servlet.session.cookie.secure}")
    private boolean isCookieSecure;

    public AuthService(UserRepository userRepository, SessionService sessionService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.sessionService = sessionService;
        this.passwordEncoder = passwordEncoder;
    }

    public void loginUser(String username, String password, HttpServletResponse response) {
        User user = userRepository.findByLogin(username)
                .filter(u -> passwordEncoder.matches(password, u.getPassword()))
                .orElseThrow(() -> new InvalidCredException("Invalid credentials"));

        Session session = sessionService.createSession(user);
        createSessionCookie(response, session.getId().toString());
    }

    public void registerUser(String username, String password) {
        userRepository.findByLogin(username)
                .ifPresent(user -> {
                    throw new AlreadyExistsException("User already exists");
                });

        User newUser = new User();
        newUser.setLogin(username);
        newUser.setPassword(passwordEncoder.encode(password));
        userRepository.save(newUser);
    }

    public void logoutUser(String sessionId, HttpServletResponse response) {
        if (sessionId != null) {
            sessionService.destroySession(UUID.fromString(sessionId));
        }
        clearSessionCookie(response);
    }

    private void createSessionCookie(HttpServletResponse response, String sessionId) {
        Cookie cookie = new Cookie(defaultCookieName, sessionId);
        cookie.setHttpOnly(true);
        cookie.setSecure(isCookieSecure);
        cookie.setPath("/");
        cookie.setMaxAge(cookieMaxAge);
        cookie.setAttribute("SameSite", "Strict");
        response.addCookie(cookie);
    }

    private void clearSessionCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie(defaultCookieName, "");
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }

}

