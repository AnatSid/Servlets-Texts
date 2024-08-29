package org.example.servletsHomework.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServlet;
import org.example.servletsHomework.dao.TextDao;
import org.example.servletsHomework.dao.HashMapTextDao;
import org.example.servletsHomework.filters.TokenAuthenticationFilter;
import org.example.servletsHomework.model.IdGenerator;
import org.example.servletsHomework.model.RealIdGenerator;
import org.example.servletsHomework.service.TextService;
import org.example.servletsHomework.storage.TokensAndUserStorage;


import java.util.EnumSet;


public class MyContextListener implements ServletContextListener {


    @Override
    public void contextInitialized(ServletContextEvent sce) {

        ServletContext servletContext = sce.getServletContext();

        ObjectMapper objectMapper = new ObjectMapper();
        IdGenerator idGenerator = new RealIdGenerator();
        TextDao textDao = new HashMapTextDao();
        TextService textService = new TextService(textDao);
        TokensAndUserStorage tokensAndUserStorage = new TokensAndUserStorage();

        HttpFilter tokenAuthenticationFilter = new TokenAuthenticationFilter(tokensAndUserStorage);
        HttpServlet registerServlet = new RegisterServlet(tokensAndUserStorage);
        HttpServlet loginServlet = new LoginServlet(tokensAndUserStorage);
        HttpServlet textServlet = new TextsServlet(objectMapper, idGenerator, textService);

        String urlFilter = "/*";
        String urlRegister = "/register";
        String urlLogin = "/login";
        String urlText = "/texts/*";

        FilterRegistration.Dynamic filterAuthentication = servletContext.addFilter("textFilterUserExist", tokenAuthenticationFilter);
        filterAuthentication.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, urlFilter);

        servletContext.addServlet("registerServlet", registerServlet).addMapping(urlRegister);
        servletContext.addServlet("loginServlet", loginServlet).addMapping(urlLogin);
        servletContext.addServlet("textServlet", textServlet).addMapping(urlText);

    }

}

