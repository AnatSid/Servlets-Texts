package org.example.servletsHomework;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.servletsHomework.dao.HashMapTextDao;
import org.example.servletsHomework.dao.TextDao;
import org.example.servletsHomework.filters.TokenAuthenticationFilter;
import org.example.servletsHomework.service.IdGenerator;
import org.example.servletsHomework.service.InMemoryIdGenerator;
import org.example.servletsHomework.service.TextService;
import org.example.servletsHomework.servlet.LoginServlet;
import org.example.servletsHomework.servlet.RegisterServlet;
import org.example.servletsHomework.servlet.TextsServlet;
import org.example.servletsHomework.storage.TokensAndUserStorage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("org.example.servletsHomework")
public class SpringConfig {

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    TokensAndUserStorage tokensAndUserStorage(){
        return new TokensAndUserStorage();
    }

    @Bean
    LoginServlet loginServlet(){
        return new LoginServlet(tokensAndUserStorage());
    }

    @Bean
    RegisterServlet registerServlet(){
        return new RegisterServlet(tokensAndUserStorage());
    }

    @Bean
    TokenAuthenticationFilter tokenAuthenticationFilter(){
        return new TokenAuthenticationFilter(tokensAndUserStorage());
    }

    @Bean
    TextDao textDao(){
        return new HashMapTextDao();
    }

    @Bean
    TextService textService(){
        return new TextService(textDao());
    }

    @Bean
    IdGenerator idGenerator(){
        return new InMemoryIdGenerator();
    }

    @Bean
    TextsServlet textsServlet(){
        return new TextsServlet(objectMapper(),idGenerator(),textService());
    }

}
