package com.campospadilhaa.dscatalog.config;

import org.springframework.context.annotation.Configuration;

// classe temporária para desconsiderar o SpringSecurity, evitar o erro 401 para todos os endpoint's
@Configuration
public class SecurityConfig {

	/* Não mais necessário porque já temos ResourceServerConfig.java
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
        .csrf(csrf -> csrf.disable())

        .headers(headers ->
            headers.frameOptions(frame -> frame.disable())
        )

        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/h2-console/**").permitAll()
            .anyRequest().permitAll()
        );

        return http.build();
	}*/
}