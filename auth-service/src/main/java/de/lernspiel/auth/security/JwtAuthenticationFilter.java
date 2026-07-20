package de.lernspiel.auth.security;


import de.lernspiel.auth.entity.UserType;

import de.lernspiel.auth.service.UserService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SecurityException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

/**
 * JwtAuthenticationFilter überprüft eingehende HTTP-Anfragen auf gültige JWT-Tokens und setzt den
 * Authentifizierungskontext für Spring Security.
 */

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    public JwtAuthenticationFilter(JwtUtils jwtUtils, UserService userService) {
        this.jwtUtils=jwtUtils;
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
            if (!jwtUtils.validateToken(token)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Token ist ungültig oder abgelaufen.");
                return;
            }

            Integer userID = jwtUtils.extractUserID(token);
            UserType userType = jwtUtils.extractRole(token);

            if (userID != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + userType.name());
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userID, null, List.of(authority));
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }

        } catch (Exception e) {

            logger.error(
                    "Fehler bei der Tokenverarbeitung. IP: {}, Fehler: {}", request.getRemoteAddr(), e.getMessage());

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Ungültiger Token.");
            return;
        }

        filterChain.doFilter(request, response);
    }
}
