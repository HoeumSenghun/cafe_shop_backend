package com.cafe_shop.security;

import com.cafe_shop.user.model.RoleName;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtProperties props;

    public String generateAccessToken(CurrentUser user) {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(props.getAccessTokenTtlMinutes() * 60L);

        return Jwts.builder()
                .issuer(props.getIssuer())
                .subject(user.email())
                .issuedAt(Date.from(now))
                .expiration(Date.from(exp))
                .claim("uid", user.userId())
                .claim("roles", user.roles().stream().map(Enum::name).toList())
                .signWith(signingKey())
                .compact();
    }

    public Claims parseAndValidate(String token) {
        return Jwts.parser()
                .verifyWith(signingKey())
                .requireIssuer(props.getIssuer())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public CurrentUser toCurrentUser(Claims claims) {
        Long userId = claims.get("uid", Number.class).longValue();
        String email = claims.getSubject();
        List<String> rolesRaw = claims.get("roles", List.class);
        Set<RoleName> roles = rolesRaw == null
                ? Set.of()
                : (Set<RoleName>) rolesRaw.stream().map(r -> RoleName.valueOf(String.valueOf(r))).collect(Collectors.toSet());
        return new CurrentUser(userId, email, roles);
    }

    private SecretKey signingKey() {
        byte[] keyBytes = props.getSecret().getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}

