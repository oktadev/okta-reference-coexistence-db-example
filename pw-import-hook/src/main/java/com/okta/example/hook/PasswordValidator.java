package com.okta.example.hook;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;

@Service
public class PasswordValidator {

    private final Logger logger = LoggerFactory.getLogger(PasswordValidator.class);
    private final DaoAuthenticationProvider authenticationProvider;

    public PasswordValidator(DataSource dataSource) {
        // Creates an AuthenticationProvider internal to this classes use
        this.authenticationProvider = new DaoAuthenticationProvider();
        JdbcUserDetailsManager userManager = new JdbcUserDetailsManager(dataSource);
        userManager.setEnableGroups(true);
        userManager.setEnableAuthorities(false);
        authenticationProvider.setUserDetailsService(userManager);
    }

    boolean isPasswordValid(String username, String password) {
        try {
            // check if the password is valid, any invalid passwords or inactive users will throw an exception
            Authentication authentication = authenticationProvider
                .authenticate(new UsernamePasswordAuthenticationToken(username, password));
            return authentication.isAuthenticated();
        } catch (AuthenticationException e) {
            logger.debug("Invalid username or password", e);
            return false;
        }
    }
}