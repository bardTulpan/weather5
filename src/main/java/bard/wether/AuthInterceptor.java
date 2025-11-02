package bard.wether;

import bard.wether.entity.User;
import bard.wether.service.SessionService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;
import java.util.UUID;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    private final SessionService sessionService;

    public AuthInterceptor(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        // Разрешаем доступ к /auth/login и /auth/logout без проверки
        String path = request.getRequestURI();
        if (path.startsWith("/api/auth")) {
            return true;
        }

        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("No cookies found");
            return false;
        }

        Cookie sessionCookie = Arrays.stream(cookies)
                .filter(c -> c.getName().equals("SESSION_ID"))
                .findFirst()
                .orElse(null);

        if (sessionCookie == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("No session cookie");
            return false;
        }

        User user = sessionService.validateSession(UUID.fromString(sessionCookie.getValue()));
        if (user == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Invalid or expired session");
            return false;
        }
        return true;

    }
}
