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
                        /* 1. PUBLIC */
                        .requestMatchers("/api/auth/**", "/error").permitAll()
// Inside SecurityConfig.java .authorizeHttpRequests block
                                .requestMatchers("/api/employee/dashboard/**").hasAnyAuthority("ROLE_USER", "ROLE_EMPLOYEE", "ROLE_ADMIN", "ADMIN", "EMPLOYEE")
                        /* 2. METADATA & PROFILE */
                        .requestMatchers(HttpMethod.GET, "/api/departments/**", "/api/designations/**", "/api/employees/**", "/api/users/**").authenticated()

                        /* 3. ATTENDANCE (Specific Employee Allowances) */
                        // Allowing employees to POST (Check-in) and PUT (Check-out)
                        .requestMatchers(HttpMethod.POST, "/api/attendance/**").hasAnyAuthority("ROLE_ADMIN", "ADMIN", "ROLE_EMPLOYEE", "EMPLOYEE")
                        .requestMatchers(HttpMethod.PUT, "/api/attendance/**").hasAnyAuthority("ROLE_ADMIN", "ADMIN", "ROLE_EMPLOYEE", "EMPLOYEE")
                        .requestMatchers(HttpMethod.GET, "/api/attendance/**").hasAnyAuthority("ROLE_ADMIN", "ADMIN", "ROLE_ACCOUNTANT", "ACCOUNTANT", "ROLE_EMPLOYEE", "EMPLOYEE")

                        /* 4. LEAVE & ANALYTICS */
                        .requestMatchers(HttpMethod.GET, "/api/leave-types/**", "/api/leave-balance/**").permitAll()
                        .requestMatchers("/api/employee-leaves/**", "/api/salary-analytics/**")
                        .hasAnyAuthority("ROLE_ADMIN", "ADMIN", "ROLE_EMPLOYEE", "EMPLOYEE")

                        /* 5. PAYROLL & REPORTS */
                        .requestMatchers("/api/payrolls/**", "/api/reports/**")
                        .hasAnyAuthority("ROLE_ADMIN", "ADMIN", "ROLE_ACCOUNTANT", "ACCOUNTANT")

                        /* 6. GLOBAL ADMIN WRITE PROTECTION */
                        .requestMatchers(HttpMethod.POST, "/api/**").hasAnyAuthority("ROLE_ADMIN", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/**").hasAnyAuthority("ROLE_ADMIN", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/**").hasAnyAuthority("ROLE_ADMIN", "ADMIN")

                        /* 7. FALLBACK */
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:5173", "http://localhost:3000"));

        // UPDATE: Added "PATCH" to the list of allowed methods
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));

        // UPDATE: Added "Origin" and "X-Requested-With" to ensure all browsers are happy
        config.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept", "Origin", "X-Requested-With"));

        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}