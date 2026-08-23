package de.lernspiel.auth.security;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import de.lernspiel.auth.entity.UserType;
import de.lernspiel.auth.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Prüft eingehende HTTP-Anfragen auf gültige JWTs und setzt bei erfolgreicher
 * Validierung den Authentifizierungskontext für Spring Security.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtUtils jwtUtils;

    // Initialisiert den Filter mit den benötigten Abhängigkeiten.
    public JwtAuthenticationFilter(JwtUtils jwtUtils, UserService userService) {
        this.jwtUtils = jwtUtils;
    }

    // Prüft den Bearer-Token und setzt bei gültiger Authentifizierung den SecurityContext.
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        try {
            if (!jwtUtils.validateToken(token)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Token ist ungültig oder abgelaufen.");
                return;
            }

            Integer userID = jwtUtils.extractUserID(token);
            UserType userType = jwtUtils.extractRole(token);

            if (userID != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + userType.name());
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(userID, null, List.of(authority));

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        } catch (Exception e) {
            logger.error(
                    "Fehler bei der Tokenverarbeitung. IP: {}, Fehler: {}",
                    request.getRemoteAddr(),
                    e.getMessage()
            );

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Ungültiger Token.");
            return;
        }

        filterChain.doFilter(request, response);
    }
}