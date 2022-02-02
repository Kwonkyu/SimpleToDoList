package com.simpletodolist.todolist.common.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.simpletodolist.todolist.common.ApiResponse;
import com.simpletodolist.todolist.domains.user.adapter.repository.UserRepository;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@EnableWebSecurity // customize web security.
@RequiredArgsConstructor
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

	private final UserRepository userRepository;
	private final ObjectMapper objectMapper;

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth
			.userDetailsService(userRepository::findUserByUsername)
			.passwordEncoder(passwordEncoder());
	}

	@Override
	public void configure(WebSecurity web) {
		// ignore public api or resources.
		web.ignoring()
		   .antMatchers("/docs/**", "/", "/login.html");
	}

	private void writeJsonOnResponse(
		Object value,
		HttpServletResponse response
	) throws IOException {
		String failJsonResponse = objectMapper.writeValueAsString(value);
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		PrintWriter writer = response.getWriter();
		writer.write(failJsonResponse);
		writer.flush();
	}

	private final AuthenticationEntryPoint unauthorizedEntryPoint = (request, response, authException) -> {
		ApiResponse<Object> fail = ApiResponse.fail(authException.getMessage());
		response.setStatus(HttpStatus.UNAUTHORIZED.value());
		writeJsonOnResponse(fail, response);
	};

	private final AccessDeniedHandler accessDeniedHandler = (request, response, accessDeniedException) -> {
		ApiResponse<Object> fail = ApiResponse.fail(accessDeniedException.getMessage());
		response.setStatus(HttpStatus.FORBIDDEN.value());
		writeJsonOnResponse(fail, response);
	};

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.cors()
			.and()
			.csrf()
			.disable();

		http.logout()
			.logoutRequestMatcher(new AntPathRequestMatcher("/api/logout", HttpMethod.GET.name()))
			.logoutSuccessHandler((request, response, authentication) -> {
				PrintWriter writer = response.getWriter();
				writer.write("logged out.");
				writer.flush();
			});

		http.exceptionHandling()
			.authenticationEntryPoint(unauthorizedEntryPoint)
			.accessDeniedHandler(accessDeniedHandler);

		http.authorizeRequests()
			.antMatchers("/api/public/**", "/login/oauth2/**", "/oauth2/**")
			.permitAll()
			.anyRequest()
			.authenticated();
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
