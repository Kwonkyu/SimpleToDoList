package com.simpletodolist.todolist.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.simpletodolist.todolist.exception.member.NoMemberFoundException;
import com.simpletodolist.todolist.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
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
    private final JwtTokenUtil jwtTokenUtil;
    private final ObjectMapper objectMapper;


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(username -> // get user details.
                        memberRepository.findByUsername(username).orElseThrow(() -> new NoMemberFoundException(username)))
                .passwordEncoder(passwordEncoder()); // compare user's password with password encoder.
    }

    @Override
    public void configure(WebSecurity web) {
        // ignore public api or resources.
        web.ignoring().antMatchers("/api/public/**", "/api/token/**", "/docs/**", "/");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // enable cors
        // disable csrf: don't need in api.
        http.cors().and().csrf().disable();

        // stateless session because api doesn't use session.
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        // secure api end-point.
        http.authorizeRequests()
                .antMatchers("/api/**").authenticated()
                .antMatchers("/login/oauth2/**", "/oauth2/**").anonymous();

        http.addFilterBefore(new JwtTokenFilter(jwtTokenUtil, memberRepository, objectMapper), UsernamePasswordAuthenticationFilter.class);

        http.oauth2Login()
                .successHandler((request, response, authentication) -> response.getWriter().write("OAUTH COMPLETED"));
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
