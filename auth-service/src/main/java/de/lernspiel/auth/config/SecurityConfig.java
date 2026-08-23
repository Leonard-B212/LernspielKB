package de.lernspiel.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import de.lernspiel.auth.security.JwtAuthenticationFilter;

/**
 * Definiert die zentrale Sicherheitskonfiguration der Anwendung.
 *
 * Die Konfiguration legt öffentliche und geschützte Endpunkte fest,
 * verwendet JWT-basierte Authentifizierung und arbeitet ohne serverseitige Sessions.
 * Der JwtAuthenticationFilter wird vor dem standardmäßigen Authentifizierungsfilter ausgeführt.
 */
@EnableMethodSecurity
@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    // Initialisiert die Sicherheitskonfiguration mit dem JWT-Authentifizierungsfilter.
    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    // Konfiguriert Endpunktberechtigungen, JWT-Filter und zustandslose Sessions.
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/css/**",
                                "/js/**",
                                "/Imgs/**",
                                "/favicon.ico"
                        ).permitAll()
                        .requestMatchers(
                                "/",
                                "/index.html",
                                "/register.html",
                                "/profile.html",
                                "/static/**",
                                "/error",
                                "/index.html",
                                "/admin.html",
                                "/teacher.html",
                                "/student.html",
                                "/css/**",
                                "/js/**",
                                "/favicon.ico",
                                "/api/benutzer/login"
                        ).permitAll()
                        .requestMatchers("/api/levels/**").permitAll()
                        .requestMatchers(
                                "/api/benutzer/login",
                                "/api/benutzer/register",
                                "/api/token/generate-service-token",
                                "/debug/db-info",
                                "/debug/create-test-user",
                                "/debug/list-users",
                                "/debug/drop-user",
                                "/debug/login",
                                "/sandbox.html",
                                "/game/interpreter/run",
                                "/level.html",
                                "/skilltree.html"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );

        return http.build();
    }
}