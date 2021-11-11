package com.simpletodolist.todolist.security;

import com.simpletodolist.todolist.domain.bind.JWT;
import com.simpletodolist.todolist.domain.entity.Member;
import com.simpletodolist.todolist.exception.member.NoMemberFoundException;
import com.simpletodolist.todolist.repository.MemberRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtTokenUtil {
    private final MemberRepository memberRepository;

    @Value("${JWT_SECRET}")
    private String jwtSecret;

    private enum TokenType {
        BEARER("Bearer");

        final String name;

        TokenType(String name) {
            this.name = name;
        }
    }


    public String generateAccessToken(String username) {
        Member member = memberRepository.findByUsername(username).orElseThrow(() -> new NoMemberFoundException(username));
        return generateAccessToken(member);
    }

    public JWT generateJWT(String username) {
        Member member = memberRepository.findByUsername(username).orElseThrow(() -> new NoMemberFoundException(username));
        String accessToken = generateAccessToken(member);
        String refreshToken = generateRefreshToken(member);
        return new JWT(accessToken, refreshToken);
    }

    private JwtBuilder buildUntilExpirationDate(Member member) {
        return Jwts.builder()
                .setSubject(member.getUsername()) // Store authenticated member's username.
                .setIssuer("SimpleTodoList") // Made by JWT_ISSUER, which is "SimpleTodoList" at now.
                .setIssuedAt(new Date()) // Issued at today.
                .signWith(SignatureAlgorithm.HS256, jwtSecret); // Signing algorithm is HS256.
    }

    public String generateAccessToken(Member member) {
        JwtBuilder jwtBuilder = buildUntilExpirationDate(member);
        return jwtBuilder
                .setExpiration(new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000)) // Expiration date of access token is 1 day.
                .compact();
    }

    public String generateRefreshToken(Member member) {
        JwtBuilder jwtBuilder = buildUntilExpirationDate(member);
        return jwtBuilder
                .setExpiration(new Date(System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000)) // Expiration date of refresh token is 1 week.
                .compact();
    }

    public Claims parseBearerJWTSubject(String token) {
        String parsing = token.startsWith(TokenType.BEARER.name) ? token.substring(TokenType.BEARER.name.length() + 1) : token;
        return Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(parsing)
                .getBody();
    }

    public String getUsername(Claims claims) {
        return claims.getSubject();
    }
}
