package com.banking.banking_app.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;



@Configuration
@EnableWebSecurity
public class SecurityConfig{

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers("/api/users/register", "/api/users/login").permitAll(); // Allow access to the register endpoint
                    auth.anyRequest().authenticated(); // Require authentication for any other request
                })
                .csrf().disable() // Disable CSRF protection if not using form-based login
                .formLogin().disable() // Disable form-based login
                .httpBasic(); // Use basic HTTP authentication

        return http.build();
    }


}