package com.store.storeapp.security;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@AllArgsConstructor
public class SpringSecurityConfig {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtAuthenticationEntryPoint authenticationEntryPoint;

    @Autowired
    private CustomAccessDeniedHandler accessDeniedHandler;

    @Autowired
    private JwtAuthenticationFilter authenticationFilter;

    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Stateless sessions
                .and()
                .authorizeRequests(authorize -> {
                    // Allow access to static resources
                    authorize.antMatchers("/assets/**", "/css/**", "/js/**", "/images/**").permitAll();
                    authorize.antMatchers("/", "/auth/**", "/products/list", "/errorPage").permitAll();
                    authorize.antMatchers("/category/new", "/products/new").hasRole("ADMIN");
                    authorize.antMatchers(HttpMethod.DELETE, "/api/**").hasRole("ADMIN");
                    authorize.antMatchers(HttpMethod.GET, "/api/**").hasAnyRole("ADMIN", "USER");
                    authorize.antMatchers(HttpMethod.PATCH, "/api/**").hasAnyRole("ADMIN", "USER");
                    authorize.anyRequest().authenticated(); // Tüm diğer isteklerde kimlik doğrulaması yapılır
                })
                .exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPoint) // 401 Unauthorized işlemi
                .accessDeniedHandler(accessDeniedHandler) // 403 Forbidden işlemi
                .and()
                .addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class); // JWT Filter ekleniyor

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}
