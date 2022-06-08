package com.okta.example.ra;

import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.support.DatabaseStartupValidator;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Map;
import java.util.stream.Stream;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests((authz) -> authz
                .anyRequest().authenticated()
            )
            .formLogin();
        return http.build();
    }

    @Bean
    UserDetailsManager users(DataSource dataSource) {
        JdbcUserDetailsManager userManager = new JdbcUserDetailsManager(dataSource);
        userManager.setEnableGroups(true);
        userManager.setEnableAuthorities(false);

        return userManager;
    }

    /*
     * The docker DB in this example might not start in time, this bean will make it wait.
     */
    @Bean
    public DatabaseStartupValidator databaseStartupValidator(DataSource dataSource) {
        var dsv = new DatabaseStartupValidator();
        dsv.setDataSource(dataSource);
        return dsv;
    }

    /*
     * Make the EntityManagerFactory depend on the above db validator
     */
    @Bean
    public static BeanFactoryPostProcessor dependsOnPostProcessor() {
        return beanFactory -> {
            String[] jpa = beanFactory.getBeanNamesForType(EntityManagerFactory.class);
            Stream.of(jpa)
                    .map(beanFactory::getBeanDefinition)
                    .forEach(it -> it.setDependsOn("databaseStartupValidator"));
        };
    }

    @Controller
    static class ProfileController {

        @GetMapping("/")
        public String home() {
            return "home";
        }

        @GetMapping("/profile")
        public ModelAndView userDetails(Authentication authentication) {
            return new ModelAndView("userProfile" , Map.of(
                    "user", authentication.getPrincipal(),
                    "simpleAuth", authentication instanceof UsernamePasswordAuthenticationToken));
        }
    }
}
