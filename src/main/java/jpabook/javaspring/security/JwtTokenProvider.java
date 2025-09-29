package jpabook.javaspring.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jpabook.javaspring.features.admin.domains.Admin;
import jpabook.javaspring.features.admin.domains.CustomAdminDetails;
import jpabook.javaspring.features.user.domains.CustomUserDetails;
import jpabook.javaspring.features.user.domains.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

/* JWT
 * 생성, 파싱, 검증
 */
@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpirationMs;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    /** ================== 토큰 생성 ================== */

    public String generateTokenForAdmin(CustomAdminDetails admin) {
        return Jwts.builder()
                .setSubject(String.valueOf(admin.getId()))
                .claim("type", "ADMIN")
                .claim("loginId", admin.getLoginId())             // 관리자 loginId
                .claim("role", admin.getRole())
                .setIssuedAt(new Date())
                .setExpiration(Date.from(Instant.now().plus(Duration.ofMillis(jwtExpirationMs))))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateTokenForUser(CustomUserDetails user) {
        return Jwts.builder()
                .setSubject(String.valueOf(user.getId()))         // PK (Long id)
                .claim("type", "USER")                            // 구분자
                .claim("email", user.getEmail())                  // 사용자 email
                .setIssuedAt(new Date())
                .setExpiration(Date.from(Instant.now().plus(Duration.ofMillis(jwtExpirationMs))))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /** ================== Claims 파싱 ================== */

    public Claims parseClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            // 만료된 토큰도 클레임 꺼낼 수 있도록 처리
            return e.getClaims();
        }
    }

    /** ================== Claims 조회 헬퍼 ================== */

    // PK (공통 id)
    public Long getUserIdFromToken(String token) {
        return Long.valueOf(parseClaims(token).getSubject());
    }

    // 사용자 타입: ADMIN / USER
    public String getTypeFromToken(String token) {
        return parseClaims(token).get("type", String.class);
    }

    // 사용자 email (USER 전용)
    public String getEmailFromToken(String token) {
        return parseClaims(token).get("email", String.class);
    }

    // 관리자 loginId (ADMIN 전용)
    public String getLoginIdFromToken(String token) {
        return parseClaims(token).get("loginId", String.class);
    }

    // roles
    public String getRolesFromToken(String token) {
        return parseClaims(token).get("roles", String.class);
    }

    /** ================== 토큰 유효성 검증 ================== */

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (SignatureException ex) {
            System.out.println("유효하지 않은 JWT 서명");
        } catch (MalformedJwtException ex) {
            System.out.println("유효하지 않은 JWT 토큰");
        } catch (ExpiredJwtException ex) {
            System.out.println("만료된 JWT 토큰");
        } catch (UnsupportedJwtException ex) {
            System.out.println("지원되지 않는 JWT 토큰");
        } catch (IllegalArgumentException ex) {
            System.out.println("JWT claims 문자열이 비어있음");
        }
        return false;
    }
}