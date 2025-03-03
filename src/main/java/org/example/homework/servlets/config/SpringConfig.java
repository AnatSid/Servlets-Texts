package org.example.homework.servlets.config;


import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@ComponentScan("org.example.homework.servlets")
@EnableWebMvc
public class SpringConfig {

}
