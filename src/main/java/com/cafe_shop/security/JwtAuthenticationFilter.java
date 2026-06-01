package com.cafe_shop.security;

import com.cafe_shop.common.exception.UnauthorizedException;
import com.cafe_shop.user.model.Role;
import com.cafe_shop.user.repository.UserRepository;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring("Bearer ".length()).trim();
        try {
            var claims = jwtService.parseAndValidate(token);
            var tokenUser = jwtService.toCurrentUser(claims);

            var user = userRepository.findById(tokenUser.userId())
                    .orElseThrow(() -> new UnauthorizedException("User not found"));
            if (!user.isEnabled()) {
                throw new UnauthorizedException("User disabled");
            }

            var roles = user.getRoles().stream().map(Role::getName).collect(Collectors.toSet());
            var currentUser = new CurrentUser(user.getId(), user.getEmail(), roles);

            var authorities = currentUser.roles().stream()
                    .map(r -> new SimpleGrantedAuthority("ROLE_" + r.name()))
                    .toList();

            var authentication = new UsernamePasswordAuthenticationToken(currentUser, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(request, response);
        } catch (JwtException ex) {
            throw new UnauthorizedException("Invalid token");
        }
    }
}
