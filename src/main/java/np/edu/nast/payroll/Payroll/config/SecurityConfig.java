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
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration
    ) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authenticationProvider(authenticationProvider())

                .authorizeHttpRequests(auth -> auth

                        /* ============================================================
                           1. PUBLIC & PREFLIGHT ENDPOINTS (FIX FOR CORS ERRORS)
                           ============================================================ */
                        // Allow all OPTIONS requests (preflight) so the browser doesn't block PATCH/POST
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/api/auth/**", "/error").permitAll()
                        .requestMatchers("/api/users/forgot-password/**",
                                "/api/users/reset-password/**").permitAll()
                        .requestMatchers("/api/dashboard/**").permitAll()

                        /* ============================================================
                           2. ROLE & USER MANAGEMENT
                           ============================================================ */
                        .requestMatchers(HttpMethod.GET, "/api/roles/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_ACCOUNTANT")
                        .requestMatchers("/api/roles/**").hasAuthority("ROLE_ADMIN")

                        .requestMatchers(HttpMethod.GET, "/api/users/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_ACCOUNTANT")
                        .requestMatchers(HttpMethod.POST, "/api/users/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/users/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/users/**").hasAuthority("ROLE_ADMIN")

                        /* ============================================================
                           3. LEAVE MANAGEMENT (ADMIN & EMPLOYEE)
                           ============================================================ */
                        .requestMatchers(HttpMethod.GET, "/api/leave-types/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_EMPLOYEE")
                        .requestMatchers(HttpMethod.GET, "/api/leave-balance/employee/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_EMPLOYEE")
                        // Ensure ADMIN can perform PATCH/PUT/DELETE on leaves
                        .requestMatchers("/api/employee-leaves/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_EMPLOYEE")
                        .requestMatchers("/api/tax-slabs/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_ACCOUNTANT")
                        /* ============================================================
                           4. SHARED MODULES (ADMIN, ACCOUNTANT, EMPLOYEE)
                           ============================================================ */
                        .requestMatchers("/api/attendance/**")
                        .hasAnyAuthority("ROLE_ADMIN", "ROLE_ACCOUNTANT", "ROLE_EMPLOYEE")

                        /* ============================================================
                           5. ACCOUNTING & MANAGEMENT (ADMIN, ACCOUNTANT)
                           ============================================================ */
                        .requestMatchers("/api/payrolls/**", "/api/reports/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_ACCOUNTANT")

                        .requestMatchers(HttpMethod.GET,
                                "/api/employees/**",
                                "/api/departments/**",
                                "/api/designations/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_ACCOUNTANT")

                        .requestMatchers(HttpMethod.GET,
                                "/api/salary-components/**",
                                "/api/grade-salary-components/**",
                                "/api/employee-salary-components/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_ACCOUNTANT")

                        /* ============================================================
                           6. ROLE-SPECIFIC PANELS
                           ============================================================ */
                        .requestMatchers("/api/employee/**").hasAuthority("ROLE_EMPLOYEE")
                        .requestMatchers("/api/accountant/**").hasAuthority("ROLE_ACCOUNTANT")
                        .requestMatchers("/api/admin/**").hasAuthority("ROLE_ADMIN")

                        /* ============================================================
                           7. ADMIN ONLY (WRITE ACCESS & FALLBACK)
                           ============================================================ */
                        .requestMatchers("/api/employees/**",
                                "/api/departments/**",
                                "/api/designations/**",
                                "/api/salary-components/**").hasAuthority("ROLE_ADMIN")

                        .requestMatchers("/api/**").hasAuthority("ROLE_ADMIN")

                        /* ============================================================
                           8. FINAL FALLBACK
                           ============================================================ */
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of(
                "http://localhost:5173",
                "http://localhost:3000"
        ));
        config.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS" // Added PATCH here
        ));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}