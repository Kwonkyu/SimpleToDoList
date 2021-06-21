package com.simpletodolist.todolist.security;

import com.simpletodolist.todolist.exception.general.AuthenticationFailedException;
import com.simpletodolist.todolist.exception.member.NoMemberFoundException;
import com.simpletodolist.todolist.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
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
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {

    private final JwtTokenUtil jwtTokenUtil;
    private final MemberRepository memberRepository;


    private boolean validateAuthorizationHeader(String header) {
        // https://developer.mozilla.org/en-US/docs/Web/HTTP/Authentication#authentication_schemes
        return header != null && !header.isBlank()/* && header.startsWith("Bearer ")*/;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if(!validateAuthorizationHeader(header)) {
//            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Malformed / Blank Authorization Header Found.");
            // https://github.com/spring-projects/spring-security/issues/4368
            filterChain.doFilter(request, response);
            return;
        }

        try {
            jwtTokenUtil.validateJwtToken(header);
        } catch (AuthenticationFailedException exception) {
            try {
                filterChain.doFilter(request, response);
            } catch (AccessDeniedException ex) {
                // TODO: 언제 어떤 종류의 예외가 throw되고 처리되는지 조사. 지금 ExceptionHandler랑 sendError 등등 섞여 있어서 문제.
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, exception.getMessage());
            } // maybe handle with filter: https://samtao.tistory.com/48
            return;
        }

        // get user identification from token and set to spring security context.
        UserDetails userDetails = memberRepository.findByUserId(jwtTokenUtil.getUserIdFromToken(header)).orElseThrow(NoMemberFoundException::new);
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails == null ? List.of() : userDetails.getAuthorities());

        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authToken);
        filterChain.doFilter(request, response);
    }



}
