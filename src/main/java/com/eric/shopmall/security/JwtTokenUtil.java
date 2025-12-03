package com.eric.shopmall.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class JwtTokenUtil {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenUtil.class);

    @Value("${app.jwt.secret}")
    private String secretString;

    @Value("${app.jwt.expiration-time}")
    private long EXPIRATION_TIME;

    private Key secretKey;

    @PostConstruct
    public void init() {
        logger.info("DEBUG JWT: Loaded secretString length: {}", secretString.length());
        try {
            byte[] keyBytes = Decoders.BASE64.decode(secretString);
            this.secretKey = Keys.hmacShaKeyFor(keyBytes);
            logger.info("DEBUG JWT: Successfully initialized secretKey (Hashcode: {})", secretKey.hashCode());
        } catch (IllegalArgumentException e) {
            logger.error("ERROR JWT: Failed to decode JWT secret string. Check if it is a valid Base64 string.", e);
            throw new RuntimeException("Invalid JWT secret configuration", e);
        }
    }

    /**
     * 生成 Token，使用 userId 作為 Subject
     */
    public String generateToken(String userId, List<String> roles) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", roles);

        logger.debug("DEBUG JWT: Generating token using secretKey (Hashcode: {})", secretKey.hashCode());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userId) // 設定 userId 為 Subject
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(secretKey) // 使用更現代的 API 寫法
                .compact();
    }

    /**
     * 從 Token 中獲取使用者 ID
     */
    public String getUserIdFromToken(String token) {
        return extractAllClaims(token).getSubject();
    }

    /**
     * 驗證 Token 是否有效，比對 userId
     */
    public boolean validateToken(String token, UserDetails userDetails) {
        final String userId = getUserIdFromToken(token);
        // 這裡比對的是 userDetails.getUsername() (其實存的是 userId)
        return (userId.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    private Claims extractAllClaims(String token) {
        logger.debug("DEBUG JWT: Extracting claims using secretKey (Hashcode: {})", secretKey.hashCode());
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
