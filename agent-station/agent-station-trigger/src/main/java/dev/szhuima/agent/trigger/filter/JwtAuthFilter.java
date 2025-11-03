package dev.szhuima.agent.trigger.filter;

import dev.szhuima.agent.infrastructure.util.UserContext;
import dev.szhuima.agent.trigger.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil; // 你自己实现的JWT解析工具类

    public JwtAuthFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        try {

            String method = request.getMethod();
            if (method.equals("OPTIONS")) {
                filterChain.doFilter(request, response);
                return;
            }

            String requestURI = request.getRequestURI();
            if (requestURI.endsWith("/login")) {
                filterChain.doFilter(request, response);
                return;
            }

            String token = extractToken(request);
            if (token == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            String subject = jwtUtil.getSubject(token);
            if (subject == null) {
                log.warn("Token已失效");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            // 将用户ID放入上下文
            UserContext.setUserId(subject);

            // 放行
            filterChain.doFilter(request, response);

        } finally {
            // 防止内存泄漏
            UserContext.clear();
        }
    }

    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }
}
