package org.example.servletsHomework.servlet;


import jakarta.servlet.*;
import jakarta.servlet.annotation.WebListener;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServlet;
import org.example.servletsHomework.SpringConfig;
import org.example.servletsHomework.filters.TokenAuthenticationFilter;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.EnumSet;

@WebListener
public class MyContextListener implements ServletContextListener {


    @Override
    public void contextInitialized(ServletContextEvent sce) {

        ServletContext servletContext = sce.getServletContext();
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(SpringConfig.class);

        HttpFilter tokenAuthenticationFilter = context.getBean(TokenAuthenticationFilter.class);
        HttpServlet registerServlet = context.getBean(RegisterServlet.class);
        HttpServlet loginServlet = context.getBean(LoginServlet.class);
        HttpServlet textServlet = context.getBean(TextsServlet.class);


        servletContext.addFilter("tokenAuthenticationFilter", tokenAuthenticationFilter)
                .addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, "/*");

        servletContext.addServlet("registerServlet", registerServlet).addMapping("/register");
        servletContext.addServlet("loginServlet", loginServlet).addMapping("/login");
        servletContext.addServlet("textServlet", textServlet).addMapping("/texts/*");

    }

}

