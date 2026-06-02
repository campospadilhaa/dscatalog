package com.devsuperior.demo.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class SecurityConfig {

	/* código transferido para a classe ResourceServerConfig.java
	@Bean
	public PasswordEncoder getPasswordEncoder() {

		return new BCryptPasswordEncoder();
	}

	@Bean
	@Profile("test")
	@Order(1)
	public SecurityFilterChain h2SecurityFilterChain(HttpSecurity http) throws Exception {

		http.securityMatcher(PathRequest.toH2Console()).csrf(csrf -> csrf.disable())
				.headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable()));
		return http.build();
	}

	@Bean
	@Order(2)
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

		http.csrf(csrf -> csrf.disable()); // desabilitado controle de acesso à sessão da aplicação
		http.authorizeHttpRequests(auth -> auth.anyRequest().permitAll()); // permitido todos os acessos. As restrições serão configuradas por rota

		return http.build();
	}*/
}