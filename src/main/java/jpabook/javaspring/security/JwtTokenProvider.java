package jpabook.javaspring.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jpabook.javaspring.domain.user.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpirationMs;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(Authentication authentication) {
        User userPrincipal = (User) authentication.getPrincipal();

        Map<String, Object> claims = new HashMap<>();
        claims.put("id", userPrincipal.getId());
        claims.put("username", userPrincipal.getUsername());
        claims.put("name", userPrincipal.getName());
        claims.put("email", userPrincipal.getEmail());
        claims.put("role", userPrincipal.getRole().name());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userPrincipal.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(getSigningKey())
                .compact();
    }

    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

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
