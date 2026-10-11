/*
 * Copyright (c) 2026 Bareo. All rights reserved.
 *
 * This software is the proprietary and confidential property of the author.
 * Unauthorized copying, distribution, or use is strictly prohibited.
 */
package com.corusoft.bareo.bareobackend.infrastructure.config.security;

import com.corusoft.bareo.bareobackend.infrastructure.config.security.filters.FirebaseTokenAuthenticationRequestFilter;
import com.corusoft.bareo.bareobackend.infrastructure.config.security.filters.RequestTraceabilityFilter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@Configuration
public class WebSecurityConfiguration {

  private final FirebaseTokenAuthenticationRequestFilter firebaseTokenFilter;
  private final RequestTraceabilityFilter requestsTraceabilityFilter;

  public WebSecurityConfiguration(FirebaseTokenAuthenticationRequestFilter firebaseTokenFilter) {
    this.firebaseTokenFilter = firebaseTokenFilter;
    this.requestsTraceabilityFilter = new RequestTraceabilityFilter();
  }

  @Bean
  public AuthenticationManager authenticationManager(HttpSecurity http) {
    AuthenticationManagerBuilder authManagerBuilder =
        http.getSharedObject(AuthenticationManagerBuilder.class);

    return authManagerBuilder.build();
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) {
    secureEndpoints(http);
    configureFilters(http);
    configureCsrf(http);
    configureSessions(http);

    return http.build();
  }

  private void secureEndpoints(HttpSecurity http) {
    http.authorizeHttpRequests(requests -> requests.requestMatchers("/error").permitAll());

    // Deny requests to endpoints that are not explicitly registered
    http.authorizeHttpRequests(requests -> requests.anyRequest().authenticated());
  }

  private void configureFilters(HttpSecurity http) {
    http.addFilterBefore(firebaseTokenFilter, UsernamePasswordAuthenticationFilter.class);
    http.addFilterBefore(
        requestsTraceabilityFilter, FirebaseTokenAuthenticationRequestFilter.class);
  }

  private static void configureCsrf(HttpSecurity http) {
    http
        // Disable CSRF as it not being used (CSRF only affects Sessions and Cookies, not JWT)
        .csrf(AbstractHttpConfigurer::disable);
  }

  private static void configureSessions(HttpSecurity http) {
    http
        // Do not store user Sessions (Stateless API)
        .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
  }
}
