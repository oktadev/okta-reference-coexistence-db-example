package com.okta.example.ra.userimport;

import com.okta.sdk.client.Client;
import com.okta.sdk.resource.user.UserBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@SpringBootApplication
public class OktaUserImportApplication {

    private final Logger log = LoggerFactory.getLogger(OktaUserImportApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(OktaUserImportApplication.class, args);
    }

    @Bean
    CommandLineRunner importUsers(DataSource dataSource, Client client) { // <.>
        return args -> {
            new JdbcTemplate(dataSource).query(
                            "select username, first_name, last_name, phone " +
                                "from users where enabled is true", // <.>
                            (rs, rowNum) -> UserBuilder.instance() // <.>
                                    .setEmail(rs.getString("username"))
                                    .setFirstName(rs.getString("first_name"))
                                    .setLastName(rs.getString("last_name"))
                                    .setMobilePhone(rs.getString("phone"))
                                    .usePasswordHookForImport() // <.>
                                    .buildAndCreate(client)) // <.>
                    .forEach(user -> log.info("Created user: {}", user.getProfile().getEmail()));
        };
    }
}