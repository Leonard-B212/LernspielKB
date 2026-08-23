package de.lernspiel.auth.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import de.lernspiel.auth.entity.User;
import de.lernspiel.auth.entity.UserType;
import de.lernspiel.auth.repository.UserRepository;
import de.lernspiel.auth.service.UserService;

/**
 * Stellt beim Anwendungsstart sicher, dass ein Administrator vorhanden ist.
 *
 * Falls noch kein Benutzer mit der Rolle ADMIN existiert, wird automatisch
 * ein Administrator mit dem konfigurierten Bootstrap-Passwort angelegt.
 */
@Configuration
public class AdminBootstrap {

    private static final Logger logger = LoggerFactory.getLogger(AdminBootstrap.class);

    @Value("${app.bootstrap-admin.password}")
    private String adminPassword;

    // Erstellt beim Anwendungsstart einen Administrator, sofern noch keiner existiert.
    @Bean
    public CommandLineRunner createAdmin(UserRepository userRepository, UserService userService) {
        return args -> {
            if (userRepository.existsByType(UserType.ADMIN)) {
                logger.info("Bootstrap admin already exists.");
                return;
            }

            User admin = new User();
            admin.setPassword(adminPassword);
            admin.setType(UserType.ADMIN);
            admin.setClassID(null);

            User savedAdmin = userService.addUser(admin);

            logger.info("Bootstrap admin created successfully (ID: {}).", savedAdmin.getUserID());
        };
    }
}