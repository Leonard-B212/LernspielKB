package de.lernspiel.auth.config;

import de.lernspiel.auth.security.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;



/*
 * Dies Klasse steuert die Sicherheitskonfiguration der Anwendung:
 * Steuert welche Endpunkte geschützt und welche offen sind. 
 * JF ersetzt den UsernamePasswordAuthenticationFilter, da keine Session-Cookies verwendet werden. Dementsprechend ist auch der CSRF-Schutz aus.
 * Da wir mit generierten Token arbeiten gibt es keine Sessions und csrf schutz ist überflüssig.
 */
@EnableMethodSecurity
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
                .requestMatchers("/", "/index.html", "/register.html", "/profile.html", "/static/**", "/error", "/index.html", "/admin.html", "/teacher.html", "/student.html", "/css/**", "/js/**", "/favicon.ico", "/api/benutzer/login").permitAll()        // Freigabe für das testfrontend, zur übersicht aufgeteilt
                .requestMatchers("/api/levels/**").permitAll()
                .requestMatchers("/api/benutzer/login", "/api/benutzer/register", "/api/token/generate-service-token", "/debug/db-info", 
                "/debug/create-test-user", "/debug/list-users", "/debug/drop-user", "/debug/login", "/sandbox.html", "/game/interpreter/run").permitAll() // Öffentliche Endpunkte
                .anyRequest().authenticated()  // Alle anderen Endpunkte erfordern Authentifizierung
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)); 
        return http.build();
    }
}
