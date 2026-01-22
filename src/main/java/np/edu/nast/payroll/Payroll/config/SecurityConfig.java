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
<<<<<<< HEAD
        return NoOpPasswordEncoder.getInstance();
    }


=======
        // ⚠️ Reminder: Use BCryptPasswordEncoder for production environments
        return NoOpPasswordEncoder.getInstance();
    }

>>>>>>> 3214be41b790e5d207ff8a4a5185d56a25676df5
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
<<<<<<< HEAD
                           1. PUBLIC & PREFLIGHT ENDPOINTS (FIX FOR CORS ERRORS)
                           ============================================================ */
                        // Allow all OPTIONS requests (preflight) so the browser doesn't block PATCH/POST
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
=======
                           1. PUBLIC ENDPOINTS
                           ============================================================ */
>>>>>>> 3214be41b790e5d207ff8a4a5185d56a25676df5
                        .requestMatchers("/api/auth/**", "/error").permitAll()
                        .requestMatchers("/api/users/forgot-password/**",
                                "/api/users/reset-password/**").permitAll()
                        .requestMatchers("/api/dashboard/**").permitAll()

                        /* ============================================================
<<<<<<< HEAD
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
=======
                           2. LEAVE MANAGEMENT (FIXED: Added Employee Access)
                           ============================================================ */
                        // Allow employees to fetch types and their own balance
                        .requestMatchers(HttpMethod.GET, "/api/leave-types/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_EMPLOYEE")
                        .requestMatchers(HttpMethod.GET, "/api/leave-balance/employee/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_EMPLOYEE")

                        // Allow employees to view their history and submit requests
                        .requestMatchers("/api/employee-leaves/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_EMPLOYEE")

                        /* ============================================================
                           3. SHARED MODULES (ADMIN, ACCOUNTANT, EMPLOYEE)
>>>>>>> 3214be41b790e5d207ff8a4a5185d56a25676df5
                           ============================================================ */
                        .requestMatchers("/api/attendance/**")
                        .hasAnyAuthority("ROLE_ADMIN", "ROLE_ACCOUNTANT", "ROLE_EMPLOYEE")

                        /* ============================================================
<<<<<<< HEAD
                           5. ACCOUNTING & MANAGEMENT (ADMIN, ACCOUNTANT)
                           ============================================================ */
                        .requestMatchers("/api/payrolls/**", "/api/reports/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_ACCOUNTANT")

=======
                           4. ACCOUNTING & MANAGEMENT (ADMIN, ACCOUNTANT)
                           ============================================================ */
                        .requestMatchers("/api/payrolls/**", "/api/reports/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_ACCOUNTANT")

                        // Read-only access for Accountants on Employees, Depts, and Designations
>>>>>>> 3214be41b790e5d207ff8a4a5185d56a25676df5
                        .requestMatchers(HttpMethod.GET,
                                "/api/employees/**",
                                "/api/departments/**",
                                "/api/designations/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_ACCOUNTANT")

<<<<<<< HEAD
=======
                        // Read-only access for Accountants on Salary Components
>>>>>>> 3214be41b790e5d207ff8a4a5185d56a25676df5
                        .requestMatchers(HttpMethod.GET,
                                "/api/salary-components/**",
                                "/api/grade-salary-components/**",
                                "/api/employee-salary-components/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_ACCOUNTANT")

                        /* ============================================================
<<<<<<< HEAD
                           6. ROLE-SPECIFIC PANELS
=======
                           5. ROLE-SPECIFIC PANELS
>>>>>>> 3214be41b790e5d207ff8a4a5185d56a25676df5
                           ============================================================ */
                        .requestMatchers("/api/employee/**").hasAuthority("ROLE_EMPLOYEE")
                        .requestMatchers("/api/accountant/**").hasAuthority("ROLE_ACCOUNTANT")
                        .requestMatchers("/api/admin/**").hasAuthority("ROLE_ADMIN")

                        /* ============================================================
<<<<<<< HEAD
                           7. ADMIN ONLY (WRITE ACCESS & FALLBACK)
                           ============================================================ */
=======
                           6. ADMIN ONLY (WRITE ACCESS & FALLBACK)
                           ============================================================ */
                        // Any remaining POST/PUT/DELETE on core modules defaults to ADMIN
>>>>>>> 3214be41b790e5d207ff8a4a5185d56a25676df5
                        .requestMatchers("/api/employees/**",
                                "/api/departments/**",
                                "/api/designations/**",
                                "/api/salary-components/**").hasAuthority("ROLE_ADMIN")

<<<<<<< HEAD
                        .requestMatchers("/api/**").hasAuthority("ROLE_ADMIN")

                        /* ============================================================
                           8. FINAL FALLBACK
=======
                        // Global catch-all for /api/** (Admins)
                        .requestMatchers("/api/**").hasAuthority("ROLE_ADMIN")

                        /* ============================================================
                           7. FINAL FALLBACK
>>>>>>> 3214be41b790e5d207ff8a4a5185d56a25676df5
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
<<<<<<< HEAD
                "GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS" // Added PATCH here
=======
                "GET", "POST", "PUT", "DELETE", "OPTIONS"
>>>>>>> 3214be41b790e5d207ff8a4a5185d56a25676df5
        ));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

<<<<<<< HEAD
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
=======
        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();
>>>>>>> 3214be41b790e5d207ff8a4a5185d56a25676df5
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}