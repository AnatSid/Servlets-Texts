package org.example.homework.servlets.filters;

import jakarta.servlet.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.example.homework.servlets.storage.TokenStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

@Component
public class TokenAuthenticationFilter extends GenericFilterBean {

    private final TokenStorage tokenStorage;

    @Autowired
    public TokenAuthenticationFilter(TokenStorage tokenStorage) {
        this.tokenStorage = tokenStorage;
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        String path = request.getRequestURI();
        if (path.equals("/register") || path.equals("/login")) {
            chain.doFilter(req, res);
            return;
        }

        String token = request.getHeader("token");

        if (token == null || !tokenStorage.isTokenExists(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Token does not exist. Need registration");
        } else if (!tokenStorage.isTokenValid(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Token expired. Need to log in");
        } else {
            Long userId = tokenStorage.getUserIdByToken(token);
            request.setAttribute("userId", userId);
            chain.doFilter(req, res);
        }
    }
}
