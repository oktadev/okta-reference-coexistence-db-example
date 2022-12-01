package com.okta.example.hook;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeRequests()
                .anyRequest().authenticated().and() // <.>
                .csrf().disable() // <.>
                .sessionManagement().sessionCreationPolicy(STATELESS).and() // <.>
                .httpBasic().and() // <.>
                .build();
    }
}