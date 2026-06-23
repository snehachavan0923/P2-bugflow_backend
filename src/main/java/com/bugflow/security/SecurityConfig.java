package com.bugflow.security;

import java.util.List;


import org.springframework.context.annotation.*;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
        	.cors(cors -> {}) 
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            //  Disable default login (removes generated password)
            .formLogin(form -> form.disable())
            .httpBasic(httpBasic -> httpBasic.disable())

            .authorizeHttpRequests(auth -> auth

            	    .requestMatchers(HttpMethod.OPTIONS, "/**")
            	    .permitAll()

            	    .requestMatchers("/auth/**")
            	    .permitAll()
            	    
            	    .requestMatchers(
            	            HttpMethod.POST,
            	            "/organizations")
            	    .hasRole("Owner")

            	    .requestMatchers(
            	            HttpMethod.GET,
            	            "/organizations/me")
            	    .authenticated()
            	    
            	    .requestMatchers(
            	            HttpMethod.GET,
            	            "/organizations/members")
            	    .hasRole("Owner")
            	    
            	    .requestMatchers(
            	            HttpMethod.GET,
            	            "/organizations/settings")
            	    .hasRole("Owner")

            	    // OWNER
            	    .requestMatchers(HttpMethod.POST,
            	            "/projects")
            	    .hasRole("Owner")

            	    .requestMatchers(HttpMethod.POST,
            	            "/projects/*/team")
            	    .hasRole("Owner")

            	    .requestMatchers(HttpMethod.PUT,
            	            "/projects/*/team/*")
            	    .hasRole("Owner")

            	    .requestMatchers(HttpMethod.DELETE,
            	            "/projects/*/team/*")
            	    .hasRole("Owner")

            	    .requestMatchers(HttpMethod.GET,
            	            "/projects/*/team")
            	    .hasRole("Owner")

            	    .requestMatchers(HttpMethod.POST,
            	            "/projects/*/issues")
            	    .hasRole("Owner")

            	    .requestMatchers(HttpMethod.PUT,
            	            "/projects/*/issues/*")
            	    .hasRole("Owner")

            	    .requestMatchers(HttpMethod.DELETE,
            	            "/projects/*/issues/*")
            	    .hasRole("Owner")

            	    // DEVELOPER
            	    .requestMatchers(HttpMethod.GET,
            	            "/projects/*/issues/my-tasks")
            	    .hasRole("Developer")

            	    .requestMatchers(HttpMethod.PUT,
            	            "/projects/*/issues/*/status")
            	    .hasAnyRole("Developer", "Owner")

            	    .requestMatchers(HttpMethod.PUT,
            	            "/projects/*/issues/*/resolve")
            	    .hasRole("Developer")
            	    .requestMatchers(
            	            HttpMethod.GET,
            	            "/projects/*/issues/project-my-tasks")
            	    .hasRole("Developer")

            	    .requestMatchers(
            	            HttpMethod.GET,
            	            "/projects/*/issues/project-review")
            	    .hasRole("Tester")

            	    // TESTER
            	    .requestMatchers(HttpMethod.GET,
            	            "/projects/*/issues/review")
            	    .hasRole("Tester") 

            	    .requestMatchers(HttpMethod.PUT,
            	            "/projects/*/issues/*/approve")
            	    .hasRole("Tester")

            	    .requestMatchers(HttpMethod.PUT,
            	            "/projects/*/issues/*/reject")
            	    .hasRole("Tester")
            	    .requestMatchers(
            	            HttpMethod.GET,
            	            "/dashboard/**")
            	    .hasAnyRole(
            	            "Owner",
            	            "Developer",
            	            "Tester",
            	            "Viewer")

            	    // VIEW PROJECTS
            	    .requestMatchers(HttpMethod.GET,
            	            "/projects/**")
            	    .hasAnyRole(
            	            "Owner",
            	            "Developer",
            	            "Tester",
            	            "Viewer")

            	    .anyRequest().authenticated()
            	)
            //  JWT filter
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOrigins(List.of("http://localhost:3000"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }
}