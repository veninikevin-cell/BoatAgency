package com.example.lago.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

// configurazione Spring Security - regole di autorizzazione, form login/logout, encoder password
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    public SecurityConfig() {}

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/register", "/login",
                     "/css/**", "/js/**", "/images/**",
                     "/ws/**", "/public/**", "/api/**").permitAll()
                .requestMatchers("/provider/**").hasRole("PROVIDER")
                // deve stare prima di /bookings/** sotto
                .requestMatchers("/bookings/confirm/**", "/bookings/reject/**")
                    .hasRole("PROVIDER")
                .requestMatchers("/bookings/**").hasAnyRole("USER", "PROVIDER")
                .requestMatchers("/chat/**").authenticated()
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/", true)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/")
                .permitAll()
            );
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }}