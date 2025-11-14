package com.jbx.econtract.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

/**
 * Security Configuration
 * Form-based Login mit Username/Password
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Für API-Zugriff
            .authorizeHttpRequests(auth -> auth
                // Public resources
                .requestMatchers("/login.html").permitAll()
                .requestMatchers("/css/**", "/js/**", "/images/**", "/fonts/**").permitAll()
                .requestMatchers("/api/public/**").permitAll()
                .requestMatchers("/health", "/actuator/health").permitAll()
                // All HTML pages require authentication (except login.html already permitted above)
                .requestMatchers("/*.html", "/reports/*.html").authenticated()
                .requestMatchers("/api/**").authenticated()
                // All other requests require authentication
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login.html")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/dashboard.html", true)
                .failureUrl("/login.html?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login.html?logout=true")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            .rememberMe(remember -> remember
                .key("econtract-ki-remember-me-key")
                .tokenValiditySeconds(86400 * 7) // 7 days
            );
        
        return http.build();
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        var admin = User.builder()
            .username("admin")
            .password(passwordEncoder.encode("admin123"))
            .roles("ADMIN", "USER")
            .build();
            
        return new InMemoryUserDetailsManager(admin);
    }
}
