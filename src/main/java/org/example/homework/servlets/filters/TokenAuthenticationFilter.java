package org.example.homework.servlets.filters;

import jakarta.servlet.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.example.homework.servlets.storage.TokensAndUserStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

@Component
public class TokenAuthenticationFilter extends GenericFilterBean {

    private final TokensAndUserStorage tokensAndUserStorage;

    @Autowired
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
