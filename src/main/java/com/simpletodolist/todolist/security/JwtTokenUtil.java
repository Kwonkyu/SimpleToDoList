package com.simpletodolist.todolist.security;

import com.simpletodolist.todolist.domain.bind.JWT;
import com.simpletodolist.todolist.domain.entity.Member;
import com.simpletodolist.todolist.domain.entity.redis.UserJwt;
import com.simpletodolist.todolist.exception.member.NoMemberFoundException;
import com.simpletodolist.todolist.repository.MemberRepository;
import com.simpletodolist.todolist.repository.redis.JwtStatusRepository;
import com.simpletodolist.todolist.repository.redis.UserJwtRepository;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtTokenUtil {
    private final MemberRepository memberRepository;
    private final UserJwtRepository userJwtRepository;
    private final JwtStatusRepository jwtStatusRepository;

    @Value("${JWT_SECRET}")
    private String jwtSecret;


    public String stripHeader(String token, TokenType tokenType) {
        return token.startsWith(tokenType.name) ? token.substring(tokenType.name.length() + 1) : token;
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

    public Claims parseJWTSubject(String token) {
        return Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody();
    }

    public Claims parseBearerJWTSubject(String tokenWithType) {
        String token = stripHeader(tokenWithType, TokenType.BEARER);
        Claims claims = parseJWTSubject(token);
        String username = getUsername(claims);
        UserJwt userJwt = userJwtRepository.findById(username)
                .orElseThrow(() -> new JwtException(String.format("Not issued token for user %s", username)));
        jwtStatusRepository.findById(token).ifPresent(jwtStatus -> {
            throw new JwtException(String.format("Token %s for user %s is expired on %s because of %s",
                    jwtStatus.getToken(), username, jwtStatus.getInvalidatedDate(), jwtStatus.getInvalidatedReason()));
        });
        if(!token.equals(userJwt.getAccessToken()) && !token.equals(userJwt.getRefreshToken())) {
            throw new JwtException(String.format("Invalid token for user %s", username));
        }

        return claims;
    }

    public String getUsername(Claims claims) {
        return claims.getSubject();
    }
}
