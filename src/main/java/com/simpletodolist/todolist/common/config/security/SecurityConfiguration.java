package com.simpletodolist.todolist.common.config.security;

import com.simpletodolist.todolist.domains.user.adapter.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@EnableWebSecurity // customize web security.
@RequiredArgsConstructor
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

	private final UserRepository userRepository;

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth
			.userDetailsService(userRepository::findByUsernameUnwrapped)
			.passwordEncoder(passwordEncoder());
	}

	@Override
	public void configure(WebSecurity web) {
		// ignore public api or resources.
		web.ignoring()
		   .antMatchers("/api/public/**", "/api/token/**", "/docs/**", "/", "/login.html");
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// enable cors
		// disable csrf: don't need in api.
		http.cors()
			.and()
			.csrf()
			.disable();

		// secure api end-point.
		http.authorizeRequests()
			.antMatchers("/api/**")
			.authenticated()
			.antMatchers("/login/oauth2/**", "/oauth2/**")
			.permitAll();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	@Override
	public AuthenticationManager authenticationManager() throws Exception {
		return super.authenticationManager();
	}

	@Bean
	public CorsFilter corsFilter() {
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowCredentials(true);
		configuration.addAllowedOriginPattern("*");
		configuration.addAllowedHeader("*");
		configuration.addAllowedMethod("*");
		source.registerCorsConfiguration("/**", configuration);
		return new CorsFilter(source);
	}
}
