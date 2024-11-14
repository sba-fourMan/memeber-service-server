package org.indoles.memberserviceserver.global.util;

import io.jsonwebtoken.*;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.indoles.memberserviceserver.core.domain.enums.Role;
import org.indoles.memberserviceserver.core.dto.SignInInfo;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * 액세스 토큰과 리프레시 생성하고, 리프레시 토큰을 redis에 저장하는 클래스
 */

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
     *
     */

    public String createAccessToken(SignInInfo signInInfo) {
        Claims claims = Jwts.claims().setSubject(signInInfo.id().toString());
        claims.put("role", signInInfo.role().name());
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

    public SignInInfo getSignInInfoFromToken(String token) {
        try {
            Claims claims = Jwts.parser().setSigningKey(secretKey.getBytes()).parseClaimsJws(token).getBody();
            Long userId = Long.valueOf(claims.getSubject());
            String roleStr = claims.get("role", String.class);
            Role role = Role.valueOf(roleStr);
            log.debug("Extracted userId: {}, role: {}", userId, roleStr);
            return new SignInInfo(userId, role);
        } catch (Exception e) {
            log.error("Error extracting SignInInfo from token: {}", e.getMessage());
            throw e;
        }
    }


    public void storeRefreshToken(String refreshToken, Long userId) {
        redisTemplate.opsForValue().set(refreshToken, userId.toString(), expiration * 2, TimeUnit.MILLISECONDS);
    }

    /**
     * 리프레시 토큰 생성
     *
     * @param userId
     * @return
     */

    public String createRefreshToken(Long userId) {
        String refreshToken = Jwts.builder()
                .setSubject(userId.toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration * 2)) // 만료 시간 두 배
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();

        // Redis에 리프레시 토큰 저장
        redisTemplate.opsForValue().set(refreshToken, userId.toString(), expiration * 2, TimeUnit.MILLISECONDS);
        return refreshToken;
    }

    public Long getUserIdFromToken(String token) {
        return Long.valueOf(Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject());
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secretKey.getBytes()).parseClaimsJws(token);
            return true;
        } catch (SignatureException e) {
            log.error("JWT signature verification failed: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("JWT token is expired: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.error("JWT token is malformed: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT token is empty: {}", e.getMessage());
        } catch (Exception e) {
            log.error("JWT validation error: {}", e.getMessage());
        }
        return false;
    }
}
