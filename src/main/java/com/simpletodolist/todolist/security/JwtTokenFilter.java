package com.simpletodolist.todolist.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.simpletodolist.todolist.controller.bind.ApiResponse;
import com.simpletodolist.todolist.exception.member.NoMemberFoundException;
import com.simpletodolist.todolist.repository.MemberRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {
    private final JwtTokenUtil jwtTokenUtil;
    private final MemberRepository memberRepository;
    private final ObjectMapper objectMapper;


    private boolean isInvalidAuthorizationHeader(String header) {
        // https://developer.mozilla.org/en-US/docs/Web/HTTP/Authentication#authentication_schemes
        return header == null || header.isBlank() || !header.startsWith("Bearer ");
    }

    @Override
    // https://www.baeldung.com/spring-exclude-filter
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return request.getRequestURI().startsWith("/api/public")
                || request.getRequestURI().startsWith("/api/token/")
                || request.getRequestURI().startsWith("/docs")
                || request.getRequestURI().equals("/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if(isInvalidAuthorizationHeader(header)) { // if request has no 'authorization' header
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
            objectMapper.writeValue(response.getOutputStream(), ApiResponse.fail("Authorization header not valid. Please check your request header."));
            return;
        }

        Claims claims;
        try {
            claims = jwtTokenUtil.parseBearerJWTSubject(header);
        } catch (JwtException exception) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
            objectMapper.writeValue(response.getOutputStream(), ApiResponse.fail(String.format(
                    "%s. Please check your authorization value.", exception.getLocalizedMessage())));
            return;
        }

        // get user identification from token and set to spring security context.
        try {
            String username = jwtTokenUtil.getUsername(claims);
            UserDetails userDetails = memberRepository.findByUsername(username).orElseThrow(() -> new NoMemberFoundException(username));
            if(!userDetails.isAccountNonLocked()) throw new LockedException("Account is locked. Please contact account manager.");
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());

            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);
            filterChain.doFilter(request, response);
        } catch (RuntimeException e) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
            response.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
            objectMapper.writeValue(response.getOutputStream(), ApiResponse.fail(e.getLocalizedMessage()));
        }
    }
}
