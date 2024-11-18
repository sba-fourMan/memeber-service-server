package org.indoles.memberserviceserver.global.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.*;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.indoles.memberserviceserver.core.domain.enums.Role;
import org.indoles.memberserviceserver.core.dto.response.SignInfoRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long expiration;

    private final RedisTemplate<String, String> redisTemplate;

    public JwtTokenProvider(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @PostConstruct
    protected void init() {
        log.debug("Encoded secret key: {}", secretKey);
    }

    /**
     * 액세스 토큰 생성
     */

    public String createAccessToken(SignInfoRequest signInfoRequest) {
        Claims claims = Jwts.claims().setSubject(signInfoRequest.id().toString());
        claims.put("role", signInfoRequest.role().name());
        Date now = new Date();

        String token = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + expiration))
                .signWith(SignatureAlgorithm.HS256, secretKey.getBytes())
                .compact();
        log.debug("Generated Access Token: {}", token);
        return token;
    }


    /**
     * 토큰에서 SignInInfo 추출
     */

    public SignInfoRequest getSignInInfoFromToken(String token) {
        try {
            Claims claims = Jwts.parser().setSigningKey(secretKey.getBytes()).parseClaimsJws(token).getBody();
            Long userId = Long.valueOf(claims.getSubject());
            String roleStr = claims.get("role", String.class);
            Role role = Role.valueOf(roleStr);
            log.debug("Extracted userId: {}, role: {}", userId, roleStr);
            return new SignInfoRequest(userId, role);
        } catch (Exception e) {
            log.error("Error extracting SignInInfo from token: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * 리프레시 토큰 생성
     *
     * @param userId
     * @return
     */

    public String createRefreshToken(Long userId, Role role) {
        Claims claims = Jwts.claims().setSubject(userId.toString());
        claims.put("role", role.name());

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String signInfoJson = objectMapper.writeValueAsString(new SignInfoRequest(userId, role));
            claims.put("signInInfo", signInfoJson);
        } catch (JsonProcessingException e) {
            log.error("Error converting SignInfoRequest to JSON: {}", e.getMessage());
        }

        String refreshToken = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration * 2))
                .signWith(SignatureAlgorithm.HS256, secretKey.getBytes())
                .compact();

        redisTemplate.opsForValue().set(refreshToken, userId.toString(), expiration * 2, TimeUnit.MILLISECONDS);
        return refreshToken;
    }


    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secretKey.getBytes()).parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            log.error("JWT validation error: {}", e.getMessage());
        }
        return false;
    }
}
