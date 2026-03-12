package de.lernspiel.auth.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtConfig {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long expirationTime;

    @Value("${jwt.service.secret}")
    private String serviceSecretKey;

    @Value("${jwt.service.expiration}")
    private long serviceExpirationTime;

    public String getSecretKey() {
        return secretKey;
    }

    public long getExpirationTime() {
        return expirationTime;
    }

    public String getServiceSecretKey() {
        return serviceSecretKey;
    }

    public long getServiceExpirationTime() {
        return serviceExpirationTime;
    }

    public void setExpirationTime(long expirationTime) {
        this.expirationTime = expirationTime;
    } // kann für Tests sinnvoll bleiben

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    } 

    public void setServiceSecretKey(String serviceSecretKey) {
        this.serviceSecretKey = serviceSecretKey;
    } 

    public void setServiceExpirationTime(long serviceExpirationTime) {
        this.serviceExpirationTime = serviceExpirationTime;
    }
}