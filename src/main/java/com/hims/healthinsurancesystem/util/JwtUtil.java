package com.hims.healthinsurancesystem.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {
    private final String jwtSecret = "replace-this-with-a-strong-secret";
    private final long jwtExpirationMs = 86400000; // 1 day

    public String generateToken(Long userId, String userType) {
        return Jwts.builder()
                .setSubject(userId.toString())
                .claim("userType", userType)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }
}