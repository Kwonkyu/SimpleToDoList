package com.simpletodolist.todolist.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.simpletodolist.todolist.exception.ExceptionResponseDTO;
import com.simpletodolist.todolist.exception.member.NoMemberFoundException;
import com.simpletodolist.todolist.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@EnableWebSecurity // customize web security.
@RequiredArgsConstructor
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final MemberRepository memberRepository;
    private final JwtTokenFilter jwtTokenFilter;
    private final ObjectMapper objectMapper;


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // register MemberService to authentication manager so dao authentication provider can use.
        // UPDATE: not using MemberService because of circular reference.
        auth.userDetailsService(username -> memberRepository.findByUserId(username).orElseThrow(NoMemberFoundException::new));
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // enable cors
        // disable csrf: don't need in api.
        http.cors().and().csrf().disable();

        // stateless session because api doesn't use session.
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        // unauthorized request handler. i.e. accessing not permitted method without authorization.
        http.exceptionHandling().authenticationEntryPoint((request, response, authException) -> {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
            // https://stackoverflow.com/questions/57194249/how-to-return-response-as-json-from-spring-filter
            objectMapper.writeValue(response.getWriter(), new ExceptionResponseDTO("Unauthorized Request", authException.getLocalizedMessage()));
//            throw new AuthenticationFailedException("Authentication Failed", authException.getMessage());
//            can't catch this because controller advice cannot catch filter exception.
        });

        http.authorizeRequests()
                // https://github.com/spring-projects/spring-security/issues/4368
                .antMatchers("/api/public/**").permitAll()
                .antMatchers("/api/**").authenticated();

        // auth JWT token before username and password authentication.
        http.addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);
    }


    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }

    @Bean
    public CorsFilter corsFilter(){
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
