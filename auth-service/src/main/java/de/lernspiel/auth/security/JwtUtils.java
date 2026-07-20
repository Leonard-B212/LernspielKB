package de.lernspiel.auth.security;

/*
 * Diese Klasse bietet Funktionen zur Erstellung, Validierung und Extraktion
 * von JWT-Benutzer- oder Servicetokens.
 */

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import de.lernspiel.auth.config.JwtConfig;
import de.lernspiel.auth.entity.User;     
import de.lernspiel.auth.entity.UserType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.Key;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtils {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);
    private final JwtConfig jwtConfig;

    public JwtUtils(JwtConfig jwtConfig) {
        this.jwtConfig = jwtConfig;
    }

    // Erstellung der Keys auf Basis der Masterkeys
    protected Key getSigningKey() {
        return Keys.hmacShaKeyFor(jwtConfig.getSecretKey().getBytes(StandardCharsets.UTF_8));
    }

    protected Key getServiceSigningKey() {
        return Keys.hmacShaKeyFor(jwtConfig.getServiceSecretKey().getBytes(StandardCharsets.UTF_8));
    }

    
     //Zentrale Methode zum Parsen eines Benutzertokens.
    private Claims parseUserClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Benutzertoken erstellen
    public String generateToken(User user) {
        return Jwts.builder()
                .setSubject(String.valueOf(user.getUserID()))
                .claim("role", user.getType().name())
                .setIssuedAt(new Date())
                .setExpiration(
                        new Date(
                                System.currentTimeMillis()
                                        + jwtConfig.getExpirationTime()))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // Servicetoken erstellen
    public String generateServiceToken(String serviceName) {
        return Jwts.builder()
                .setSubject(serviceName)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtConfig.getServiceExpirationTime()))
                .signWith(getServiceSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // Benutzertoken validieren mit dem Masterkey
    public boolean validateToken(String token) {
        try {
            parseUserClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            logger.warn("Token ist abgelaufen. Ablaufdatum: {}", e.getClaims().getExpiration());
            return false;
        } catch (SecurityException e) {
            logger.error("Token-Signatur ist ungültig oder manipuliert.");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Tokensignatur ungültig");
        } catch (MalformedJwtException e) {
            logger.error("Token ist falsch formatiert.");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Tokenformat ungültig");
        } catch (UnsupportedJwtException e) {
            logger.error("Tokenalgorithmus wird nicht unterstützt.");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Tokenalgorithmus ungültig");
        } catch (IllegalArgumentException e) {
            logger.error("Token ist leer oder null.");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token fehlt");
        } catch (Exception e) {
            logger.error("Fehler bei der Token-Validierung: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Fehler bei der Tokenvalidierung");
        }
    }

    // UserID extrahieren
    public Integer extractUserID(String token) {
        try {
            String subject = parseUserClaims(token).getSubject();

            return Integer.valueOf(subject);
        } catch (ExpiredJwtException e) {
            logger.warn("Token ist abgelaufen. Ablaufdatum: {}", e.getClaims().getExpiration());
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token ist abgelaufen");
        } catch (SecurityException e) {
            logger.error("Token-Signatur ist ungültig oder manipuliert.");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Tokensignatur ungültig");
        } catch (MalformedJwtException e) {
            logger.error("Token ist falsch formatiert.");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Tokenformat ungültig");
        } catch (UnsupportedJwtException e) {
            logger.error("Tokenalgorithmus wird nicht unterstützt.");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Tokenalgorithmus ungültig");
        } catch (IllegalArgumentException e) {
            logger.error("Token ist leer oder null.");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token fehlt oder ist ungültig");
        } catch (Exception e) {
            logger.error("Fehler bei der Tokenvalidierung: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Fehler bei der Tokenvalidierung");
        }
    }

    // Funktion zum Extrahieren der Rolle aus dem Token
    public UserType extractRole(String token) {
        try {
            String role = parseUserClaims(token)
                    .get("role", String.class);

            if (role == null) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Rolle fehlt im Token");}

            return UserType.valueOf(role);

        } catch (IllegalArgumentException e) {
            logger.error( "Ungültige oder fehlende Rolle im Token.");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Ungültige Rolle im Token");

        } catch (ExpiredJwtException e) {
            logger.warn( "Token ist abgelaufen. Ablaufdatum: {}", e.getClaims().getExpiration());
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token ist abgelaufen");

        } catch (SecurityException e) {
            logger.error( "Token-Signatur ist ungültig oder manipuliert.");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Tokensignatur ungültig");

        } catch (MalformedJwtException e) {
            logger.error("Token ist falsch formatiert.");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Tokenformat ungültig");

        } catch (UnsupportedJwtException e) {
            logger.error( "Tokenalgorithmus wird nicht unterstützt.");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Tokenalgorithmus ungültig");

        } catch (Exception e) {
            logger.error( "Fehler beim Extrahieren der Rolle: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Fehler bei der Tokenvalidierung");
        }
    }
}