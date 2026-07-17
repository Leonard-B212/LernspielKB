package de.lernspiel.auth.config;

import de.lernspiel.auth.security.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.http.SessionCreationPolicy;



/*
 * Dies Klasse steuert die Sicherheitskonfiguration der Anwendung:
 * Steuert welche Endpunkte geschützt und welche offen sind. 
 * JF ersetzt den UsernamePasswordAuthenticationFilter, da keine Session-Cookies verwendet werden. Dementsprechend ist auch der CSRF-Schutz aus.
 * Da wir mit generierten Token arbeiten gibt es keine Sessions und csrf schutz ist überflüssig.
 */

@Configuration
public class SecurityConfig {
 
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) { 
        this.jwtAuthenticationFilter=jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) 
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/css/**", "/js/**", "/Imgs/**", "/favicon.ico").permitAll()
                .requestMatchers("/", "/index.html", "/register.html", "/profile.html", "/static/**").permitAll()        // Freigabe für das testfrontend, zur übersicht aufgeteilt
                .requestMatchers("/api/benutzer/login", "/api/benutzer/register", "/api/token/generate-service-token", "/debug/db-info", "/debug/create-test-user", "/debug/list-users", "/debug/drop-user", "/debug/login").permitAll() // Öffentliche Endpunkte
                .anyRequest().authenticated()  // Alle anderen Endpunkte erfordern Authentifizierung
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)); 
        return http.build();
    }
}
