package org.example.servletsHomework.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServlet;
import org.example.servletsHomework.dao.TextDao;
import org.example.servletsHomework.dao.HashmapTextDao;
import org.example.servletsHomework.filters.TextFilterIfUserExist;
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
        TextDao textDao = new HashmapTextDao();
        TextService textService = new TextService(textDao);
        TokensAndUserStorage tokensAndUserStorage = new TokensAndUserStorage();


        HttpFilter filterIfUserExist = new TextFilterIfUserExist(tokensAndUserStorage);
        HttpFilter filterIfTokenValid = new TextFilterIfUserExist(tokensAndUserStorage);

        HttpServlet registerServlet = new RegisterServlet(tokensAndUserStorage);
        HttpServlet loginServlet = new LoginServlet(tokensAndUserStorage);
        HttpServlet textServlet = new TextsServlet(objectMapper, idGenerator, textService, tokensAndUserStorage);

        String url = "/texts/*";

        FilterRegistration.Dynamic textFilterUserExist = servletContext.addFilter("textFilterUserExist", filterIfUserExist);
        textFilterUserExist.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, url);

        FilterRegistration.Dynamic textFilterTokenValid = servletContext.addFilter("textFilterTokenValid", filterIfTokenValid);
        textFilterTokenValid.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, url);

        servletContext.addServlet("registerServlet", registerServlet).addMapping("/register");
        servletContext.addServlet("loginServlet", loginServlet).addMapping("/login");
        servletContext.addServlet("textServlet", textServlet).addMapping("url");

    }

}

