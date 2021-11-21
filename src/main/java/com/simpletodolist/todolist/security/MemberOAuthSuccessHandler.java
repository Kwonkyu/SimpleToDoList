package com.simpletodolist.todolist.security;

import com.simpletodolist.todolist.domain.entity.Member;
import com.simpletodolist.todolist.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
@Transactional
public class MemberOAuthSuccessHandler implements AuthenticationSuccessHandler {
    private final MemberRepository memberRepository;
    private final JwtTokenUtil jwtTokenUtil;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private void setJwtCookieOnResponse(Member member, HttpServletResponse response) {
        String accessToken = jwtTokenUtil.generateAccessToken(member);
        String refreshToken = jwtTokenUtil.generateRefreshToken(member);
        Cookie accessTokenCookie = new Cookie("access_token", accessToken);
        accessTokenCookie.setPath("/");
        Cookie refreshTokenCookie = new Cookie("refresh_token", refreshToken);
        refreshTokenCookie.setPath("/");
        response.addCookie(accessTokenCookie);
        response.addCookie(refreshTokenCookie);
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        OAuth2AuthenticationToken oAuthAuthentication = (OAuth2AuthenticationToken) authentication;
        OAuth2User principal = oAuthAuthentication.getPrincipal();
        String email = principal.getAttribute("email");
        String alias = principal.getAttribute("given_name");
        String sub = principal.getName();
        memberRepository.findByUsername(email).ifPresentOrElse(
                member -> {
                    setJwtCookieOnResponse(member, response);
                    try {
                        response.sendRedirect("/login.html"); // redirect to client page?
                    } catch (IOException e) {
                        e.printStackTrace(); // TODO: logback.
                    }
                },
                () -> {
                    Member registeredByOAuth = memberRepository.save(Member.builder()
                            .username(email)
                            .alias(alias)
                            .password(passwordEncoder.encode(sub))
                            .locked(false).build());
                    setJwtCookieOnResponse(registeredByOAuth, response);
                    try {
                        response.sendRedirect("/login.html"); // redirect to client page?
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
    }
}
