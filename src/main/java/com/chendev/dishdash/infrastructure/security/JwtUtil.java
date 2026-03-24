package com.chendev.dishdash.infrastructure.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;
import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtUtil {

    @Value("${app.jwt.secret}")
    private String secret;

    @Value("${app.jwt.expiration-ms}")
    private long expirationMs;

    private Key signingKey;

    @PostConstruct
    public void init() {
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String generateToken(UserPrincipal principal) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .setSubject(String.valueOf(principal.getId()))
                .claim("username", principal.getUsername())
                .claim("roles", principal.getAuthorities()
                        .stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList()))
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(signingKey)
                .compact();
    }

    //Returns true if the token has a valid signature and is not expired

    public boolean isTokenValid(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(signingKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.debug("JWT expired: {}", e.getMessage());
        } catch (UnsupportedJwtException | MalformedJwtException e) {
            log.debug("JWT malformed: {}", e.getMessage());
        } catch (Exception e) {
            log.debug("JWT validation failed: {}", e.getMessage());
        }
        return false;
    }

    //Call only after isTokenValid() returns true
    public UserPrincipal parsePrincipal(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

        Long id = Long.parseLong(claims.getSubject());
        String username = claims.get("username", String.class);

        @SuppressWarnings("unchecked")
        List<String> roles = (List<String>) claims.get("roles");
        String role = (roles != null && !roles.isEmpty())
                ? roles.get(0) : "ROLE_CUSTOMER";

        return UserPrincipal.of(id, username, null, role);
    }
}
