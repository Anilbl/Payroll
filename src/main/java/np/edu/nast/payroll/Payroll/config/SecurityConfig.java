package np.edu.nast.payroll.Payroll.config;

import lombok.RequiredArgsConstructor;
import np.edu.nast.payroll.Payroll.security.CustomUserDetailsService;
import np.edu.nast.payroll.Payroll.security.JwtFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    private final CustomUserDetailsService userDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        // Warning: NoOpPasswordEncoder is only for development.
        // Use BCryptPasswordEncoder for production.
        return NoOpPasswordEncoder.getInstance();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .authorizeHttpRequests(auth -> auth
                        /* 1. PUBLIC & ESEWA CALLBACKS */
                        .requestMatchers("/api/auth/**", "/error").permitAll()
                        .requestMatchers("/api/esewa/success/**", "/api/esewa/failure/**").permitAll()

                        /* 2. DASHBOARD & PROFILE */
                        .requestMatchers("/api/employee/dashboard/**")
                        .hasAnyAuthority("ROLE_USER", "ROLE_EMPLOYEE", "ROLE_ADMIN", "ADMIN", "EMPLOYEE", "ROLE_ACCOUNTANT", "ACCOUNTANT")

                        /* 3. COMMON LOOKUPS */
                        .requestMatchers(HttpMethod.GET, "/api/departments/**", "/api/designations/**", "/api/payment-methods/**").authenticated()

                        /* 4. PAYROLL & PAYMENT (Accountant Specific Access) */
                        // Allow Accountant to access payroll logic, batch calculations, and payment initiation
                        .requestMatchers("/api/payrolls/batch-calculate", "/api/payrolls/preview")
                        .hasAnyAuthority("ROLE_ADMIN", "ADMIN", "ROLE_ACCOUNTANT", "ACCOUNTANT")
                        .requestMatchers("/api/payrolls/**")
                        .hasAnyAuthority("ROLE_ADMIN", "ADMIN", "ROLE_ACCOUNTANT", "ACCOUNTANT")
                        .requestMatchers("/api/esewa/initiate/**")
                        .hasAnyAuthority("ROLE_ADMIN", "ADMIN", "ROLE_ACCOUNTANT", "ACCOUNTANT")

                        /* 5. ATTENDANCE & LEAVE */
                        .requestMatchers("/api/attendance/**")
                        .hasAnyAuthority("ROLE_ADMIN", "ADMIN", "ROLE_EMPLOYEE", "EMPLOYEE", "ROLE_ACCOUNTANT", "ACCOUNTANT")
                        .requestMatchers("/api/employee-leaves/**", "/api/salary-analytics/**")
                        .hasAnyAuthority("ROLE_ADMIN", "ADMIN", "ROLE_EMPLOYEE", "EMPLOYEE", "ROLE_ACCOUNTANT", "ACCOUNTANT")

                        /* 6. GLOBAL WRITE PROTECTION RULES */
                        // POST/PUT/DELETE logic updated to include Accountant where business logic requires it
                        .requestMatchers(HttpMethod.POST, "/api/**")
                        .hasAnyAuthority("ROLE_ADMIN", "ADMIN", "ROLE_ACCOUNTANT", "ACCOUNTANT")
                        .requestMatchers(HttpMethod.PUT, "/api/**")
                        .hasAnyAuthority("ROLE_ADMIN", "ADMIN", "ROLE_ACCOUNTANT", "ACCOUNTANT")
                        .requestMatchers(HttpMethod.DELETE, "/api/**")
                        .hasAnyAuthority("ROLE_ADMIN", "ADMIN") // Keep Delete restricted to Admin for safety

                        /* 7. FALLBACK */
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        // Updated to allow your common frontend ports
        config.setAllowedOrigins(List.of("http://localhost:5173", "http://localhost:3000"));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept", "Origin", "X-Requested-With"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}