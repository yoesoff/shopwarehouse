package com.mhyusuf.auth.config;

import com.mhyusuf.auth.service.JwtAuthenticationEntryPoint;
import com.mhyusuf.auth.service.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SpringSecurityConfig {

    private final JwtAuthenticationEntryPoint authenticationEntryPoint;
    private final JwtAuthenticationFilter authenticationFilter;

    /**
     * Password encoder bean using BCrypt — secure and widely adopted.
     */
    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Main security configuration using modern lambda-based API (Spring Boot 3.x style).
     */
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.csrf(csrf -> csrf.disable());

        http.authorizeHttpRequests(auth -> auth
                // Public endpoints
                .requestMatchers("/", "/greeting", "/error").permitAll()
                .requestMatchers("/api/auth/login", "/api/auth/register").permitAll()
                // Swagger/OpenAPI endpoints
                .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                // Health check and monitoring
                .requestMatchers("/actuator/**").permitAll()
                // Static resources (Thymeleaf, JS, CSS, etc.)
                .requestMatchers("/css/**", "/js/**", "/images/**", "/webjars/**").permitAll()
                // Allow CORS preflight
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                // Allwow access to view templates
                .requestMatchers("/WEB-INF/views/**").permitAll()
                // Everything else requires authentication
                .anyRequest().authenticated()
        );

        // Handle unauthorized access gracefully
        http.exceptionHandling(ex -> ex.authenticationEntryPoint(authenticationEntryPoint));

        // Add JWT filter before UsernamePasswordAuthenticationFilter
        http.addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class);

        // Ensure app runs stateless (for REST API)
        http.sessionManagement(session ->
                session.sessionCreationPolicy(org.springframework.security.config.http.SessionCreationPolicy.STATELESS)
        );

        return http.build();
    }

    /**
     * Expose AuthenticationManager as a Spring bean.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}
