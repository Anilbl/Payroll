package np.edu.nast.payroll.Payroll.security;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        // 1. Check if Authorization header is present and valid
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String token = authHeader.substring(7).trim();

        // 2. Handle cases where frontend sends "null" or "undefined" as a string
        if (token.isEmpty() || "null".equalsIgnoreCase(token) || "undefined".equalsIgnoreCase(token)) {
            log.warn("Malformed token detected: {}", token);
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String username = jwtUtils.getUsernameFromToken(token);

            // 3. Process authentication if user is found and not already authenticated in this request
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                if (jwtUtils.validateToken(token)) {
                    // CRITICAL FIX: Ensure userDetails.getAuthorities() contains the ADMIN/ACCOUNTANT roles
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities() // These are the roles used by SecurityConfig
                            );

                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // Set the security context
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    log.info("Authenticated user: {} with authorities: {}", username, userDetails.getAuthorities());
                } else {
                    log.warn("Invalid JWT token for user: {}", username);
                }
            }
            filterChain.doFilter(request, response);

        } catch (ExpiredJwtException ex) {
            log.warn("JWT Token expired: {}", ex.getMessage());
            sendUnauthorized(response, "TOKEN_EXPIRED", "Session expired. Please login again.");
        } catch (Exception ex) {
            log.error("JWT Authentication failed: {}", ex.getMessage());
            sendUnauthorized(response, "AUTH_ERROR", "Authentication failed: " + ex.getMessage());
        }
    }

    /**
     * Helper method to return a clean JSON error response for 401 Unauthorized cases
     */
    private void sendUnauthorized(HttpServletResponse response, String error, String message) throws IOException {
        SecurityContextHolder.clearContext();
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write(String.format("{\"error\": \"%s\", \"message\": \"%s\"}", error, message));
    }
}