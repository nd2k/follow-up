package com.nd2k.follow_up.security.in.web;

import com.nd2k.follow_up.user.core.domain.InvalidTokenException;
import com.nd2k.follow_up.user.core.port.out.TokenPort;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final TokenPort tokenPort;

    public JwtAuthenticationFilter(TokenPort tokenPort) {
        this.tokenPort = tokenPort;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return path.equals("/health") || path.startsWith("/auth/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response); // pas de token : on laisse passer en anonyme, l'autorisation tranchera
            return;
        }
        try {
            String token = header.substring(7);
            Long userId = tokenPort.extractUserId(token);

            var authentication = new UsernamePasswordAuthenticationToken(
                    userId.toString(), null, List.of()
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (InvalidTokenException e) {
            // Token présent mais invalide : on ne bloque pas ici, on laisse l'autorisation
            // refuser plus loin (401 propre plutôt qu'une erreur bas niveau du filtre)
        }
        filterChain.doFilter(request, response);
    }
}
