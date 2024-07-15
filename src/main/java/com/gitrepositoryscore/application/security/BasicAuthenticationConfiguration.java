package com.gitrepositoryscore.application.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.server.SecurityWebFilterChain;



@Configuration
@EnableWebFluxSecurity
public class BasicAuthenticationConfiguration {

    @Autowired
    ReactiveServerAuthenticationEntryPoint reactiveServerAuthenticationEntryPoint;
    @Bean
    public SecurityWebFilterChain filterChain(ServerHttpSecurity http) {
        http.authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/swagger-ui/**", "/swagger-resources/**", "/openapi.yaml", "/swagger-ui.html")
                        .permitAll()
                        .anyExchange().authenticated()
                )
                .csrf(Customizer.withDefaults())
                .httpBasic(httpBasicSpec -> httpBasicSpec.authenticationEntryPoint(reactiveServerAuthenticationEntryPoint));
        return http.build();
    }

    @Bean
    public MapReactiveUserDetailsService userDetailsService() {
        UserDetails user = User.builder().username("user")
                .password("{noop}user")
                .roles("ADMIN")
                .build();
        return new MapReactiveUserDetailsService(user);
    }
}