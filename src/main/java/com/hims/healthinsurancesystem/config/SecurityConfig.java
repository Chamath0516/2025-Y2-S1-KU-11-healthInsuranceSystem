package com.hims.healthinsurancesystem.config;

import com.hims.healthinsurancesystem.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .userDetailsService(userDetailsService)
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/", "/login", "/register/**", "/about", "/contact", "/css/**", "/js/**", "/images/**").permitAll()
                        // Public plans pages
                        .requestMatchers(HttpMethod.GET, "/plans", "/plans/compare").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/users").permitAll()
                        // Allow agents and admins to read users (to fetch customers for messaging); mutations restricted to admins
                        .requestMatchers(HttpMethod.GET, "/api/users/**").hasAnyAuthority("ROLE_INSURANCE_AGENT", "ROLE_ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/users/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/users/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/users/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers("/api/customers").permitAll()
                        .requestMatchers("/api/healthcare-providers").permitAll()
                        .requestMatchers("/api/hospitals").permitAll()
                        .requestMatchers("/api/check-username").permitAll()
                        .requestMatchers("/api/check-nic").permitAll()
                        // Allow public read-only access to plans
                        .requestMatchers(HttpMethod.GET, "/api/insurance-plans/**").permitAll()
                        // Mutations restricted to agents/admin
                        .requestMatchers(HttpMethod.POST, "/api/insurance-plans/**").hasAnyAuthority("ROLE_INSURANCE_AGENT", "ROLE_ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/insurance-plans/**").hasAnyAuthority("ROLE_INSURANCE_AGENT", "ROLE_ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/insurance-plans/**").hasAnyAuthority("ROLE_INSURANCE_AGENT", "ROLE_ADMIN")
                        // Apply for policy: must be a logged-in customer
                        .requestMatchers(HttpMethod.POST, "/api/policies/apply").hasAuthority("ROLE_CUSTOMER")
                        .requestMatchers("/api/policies/**").hasAnyAuthority("ROLE_INSURANCE_AGENT", "ROLE_CUSTOMER", "ROLE_ADMIN")
                        .requestMatchers("/api/claims/**").hasAnyAuthority("ROLE_CLAIM_PROCESSING_OFFICER", "ROLE_CUSTOMER", "ROLE_ADMIN")
                        .requestMatchers("/api/medical-reports/**").hasAnyAuthority("ROLE_HEALTHCARE_PROVIDER", "ROLE_CLAIM_PROCESSING_OFFICER", "ROLE_ADMIN")
                        .requestMatchers("/api/payments/**").hasAuthority("ROLE_FINANCE_MANAGER")
                        .requestMatchers("/finance/**").hasAuthority("ROLE_FINANCE_MANAGER")
                        .requestMatchers("/api/tickets/**").hasAnyAuthority("ROLE_CUSTOMER_SUPPORT_OFFICER", "ROLE_CUSTOMER", "ROLE_ADMIN")
                        .requestMatchers("/api/notifications/**").hasAnyAuthority(
                                "ROLE_CUSTOMER", "ROLE_INSURANCE_AGENT", "ROLE_CLAIM_PROCESSING_OFFICER",
                                "ROLE_HEALTHCARE_PROVIDER", "ROLE_FINANCE_MANAGER",
                                "ROLE_CUSTOMER_SUPPORT_OFFICER", "ROLE_ADMIN")
                        .requestMatchers("/api/hospitals/**").hasAnyAuthority("ROLE_HEALTHCARE_PROVIDER", "ROLE_HOSPITAL", "ROLE_ADMIN")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .permitAll()
                        .successHandler((request, response, authentication) -> {
                            String role = authentication.getAuthorities().iterator().next().getAuthority();
                            switch (role) {
                                case "ROLE_ADMIN" -> response.sendRedirect("/admin/dashboard");
                                case "ROLE_INSURANCE_AGENT" -> response.sendRedirect("/agent/dashboard");
                                case "ROLE_CLAIM_PROCESSING_OFFICER" -> response.sendRedirect("/officer/dashboard");
                                case "ROLE_CUSTOMER_SUPPORT_OFFICER" -> response.sendRedirect("/support/dashboard");
                                case "ROLE_FINANCE_MANAGER" -> response.sendRedirect("/finance/dashboard");
                                case "ROLE_HEALTHCARE_PROVIDER" -> response.sendRedirect("/provider/dashboard");
                                case "ROLE_CUSTOMER" -> response.sendRedirect("/customer/dashboard");
                                case "ROLE_HOSPITAL" -> response.sendRedirect("/hospital/dashboard");
                                default -> response.sendRedirect("/");
                            }
                        })
                        .failureUrl("/login?error=true")
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout=true")
                        .permitAll()
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
