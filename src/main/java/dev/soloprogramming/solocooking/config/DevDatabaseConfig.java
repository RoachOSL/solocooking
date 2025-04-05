package dev.soloprogramming.solocooking.config;

import org.flywaydb.core.Flyway;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class DevDatabaseConfig {

    @Bean
    public Flyway flyway(DataSource dataSource) {
        return Flyway
                .configure()
                .dataSource(dataSource)
                .cleanDisabled(false)
                .load();
    }

    @Bean
    public CommandLineRunner cleanAndMigrate(Flyway flyway) {
        return args -> {
            flyway.clean();
            flyway.migrate();
        };
    }
}

