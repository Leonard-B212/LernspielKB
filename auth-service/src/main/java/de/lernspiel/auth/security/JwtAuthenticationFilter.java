package de.lernspiel.auth.security;

import de.lernspiel.auth.service.UserService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SecurityException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * JwtAuthenticationFilter überprüft eingehende HTTP-Anfragen auf gültige JWT-Tokens und setzt den
 * Authentifizierungskontext für Spring Security.
 *
 * Ablauf:
 * 1. Extrahiert den "Authorization"-Header aus der Anfrage.
 * 2. Prüft, ob der Header mit "Bearer " beginnt.
 *    - Falls nicht vorhanden, garkein Token oder kein Benutzertoken → Anfrage wird an die SecurityConfig weitergegeben.
 * 3. Falls ein gültiges Token vorhanden ist:
 *    - E-Mail wird extrahiert.
 *    - Falls kein SecurityContext vorhanden ist → erstellt ein Authentifizierungsobjekt und setzt es im SecurityContextHolder.
 *    - Falls es vorhanden ist -> direkte weiterleitung an Filterchain
 * 4. Fehlerbehandlung
 * 5. Filterchain gibt SecurityContextHolder an SecurtiyConfig.
 *
 * Der Filter sorgt durch `OncePerRequestFilter` dafür, dass dieser nur einmal pro Anfrage ausgeführt wird.
 */

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    @SuppressWarnings("unused")
    private final UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    public JwtAuthenticationFilter(JwtUtils jwtUtils, UserService userService) {
        this.jwtUtils=jwtUtils;
        this.userService = userService;
    }

    @Override 
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
                                     throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {          //Bearer :Best practice für benutzertoken
            filterChain.doFilter(request, response);
            return; 
        }    

        String token = authHeader.substring(7);

        try {
            String email = jwtUtils.extractEmail(token);
            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                if (jwtUtils.validateToken(token)) {
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(email, null, null);
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));// setzt metadaten also ip adresse usw. sinnvoll für logging und Fehlersuche & identifizieren für von verdächtigen zugriffen
                    SecurityContextHolder.getContext().setAuthentication(authToken); 
                }
            }
        } catch (ExpiredJwtException e) { 
            logger.warn("Token ist abgelaufen. Ablaufdatum: {}", e.getClaims().getExpiration());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Token ist abgelaufen.");
            return;
        } catch (SecurityException e) {
            logger.error("Tokensignatur ist ungültig. IP: {}", request.getRemoteAddr());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Tokensignatur ist ungültig.");
            return;
        } catch (Exception e)  {
            logger.error("Fehler bei der Tokenverarbeitung: {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Ungültiger Token.");
            return;
        }
        filterChain.doFilter(request, response);
    }
}
