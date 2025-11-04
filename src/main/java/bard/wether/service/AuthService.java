package bard.wether.service;

import bard.wether.entity.Session;
import bard.wether.entity.User;
import bard.wether.exceptions.AlreadyExistsException;
import bard.wether.exceptions.InvalidCredException;
import bard.wether.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final UserService userService;
    private final SessionService sessionService;

    public AuthService(UserRepository userRepository, UserService userService, SessionService sessionService) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.sessionService = sessionService;
    }

    public void registerUser(String username, String password) {
        boolean created = createUser(username, password);
        if (!created) throw new AlreadyExistsException("Username already exists");
    }


    public void loginUser(String username, String password, HttpServletResponse response) {
        Integer userId = validateUser(username, password);
        if (userId == null) {
            throw new InvalidCredException("Invalid credentials");
        }

        User user = userService.findByLogin(username);
        Session session = sessionService.createSession(user);
        createSessionCookie(response, session.getId().toString());
    }

    public boolean createUser(String username, String password) {
        User existingUser = userRepository.findByLogin(username);
        if (existingUser != null) {
            throw new AlreadyExistsException("User already exists");
        }

        User newUser = new User();
        newUser.setLogin(username);
        newUser.setPassword(passwordEncoder.encode(password));
        userRepository.save(newUser);
        return true;
    }

    public void logoutUser(String sessionId, HttpServletResponse response) {
        if (sessionId != null) {
            sessionService.destroySession(UUID.fromString(sessionId));
        }
        clearSessionCookie(response);
    }

    public Integer validateUser(String username, String password) {
        User user = userRepository.findByLogin(username);
        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            return user.getId();
        }
        return null;
    }

    private void createSessionCookie(HttpServletResponse response, String sessionId) {
        Cookie cookie = new Cookie("SESSION_ID", sessionId);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60 * 2);
        cookie.setAttribute("SameSite", "Strict");
        response.addCookie(cookie);
    }

    private void clearSessionCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie("SESSION_ID", "");
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }

}

