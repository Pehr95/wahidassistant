package com.wahidassistant.config;


import com.wahidassistant.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Component
@RequiredArgsConstructor
// A filter for JWT authentication. Author Pehr Nortén.
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final UserService userService;


    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        String jwt = null;
        String username = null;

        // Check for "Bearer" token in the Authorization header. If not found, check for "auth_token" cookie.
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    // Check for "auth_token" cookie by name
                    if ("auth_token".equals(cookie.getName())) {
                            username =  jwtService.extractUsername(cookie.getValue());
                            jwt = cookie.getValue();
                    }
                }
            }
            if (username == null) {
                filterChain.doFilter(request, response); // Continue to the next filter if no token is found
                return;
            }
        } else {
            jwt = authHeader.substring(7);
            username = jwtService.extractUsername(jwt);
        }

        // Check if the username is not null, the SecurityContext is not authenticated, and the user exists
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null && userService.findByUsername(username).isPresent()) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
            // Check if the token is valid
            if (jwtService.isTokenValid(jwt, userDetails)) {
                // Set the authentication token
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        // Continue to the next filter
        filterChain.doFilter(request, response);

    }
}
