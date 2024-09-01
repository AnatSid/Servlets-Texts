package org.example.servletsHomework.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.servletsHomework.storage.TokensAndUserStorage;

import java.io.IOException;

public class TokenAuthenticationFilter extends HttpFilter {

    private final TokensAndUserStorage tokensAndUserStorage;

    public TokenAuthenticationFilter(TokensAndUserStorage tokensAndUserStorage) {
        this.tokensAndUserStorage = tokensAndUserStorage;
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

        if (token == null || !tokensAndUserStorage.isTokenExists(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Token does not exist. Need registration");
        } else if (!tokensAndUserStorage.isTokenValid(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Token expired. Need to log in");
        } else {
            Long userId = tokensAndUserStorage.getUserIdByToken(token);
            request.setAttribute("userId", userId);
            chain.doFilter(req, res);
        }
    }
}
