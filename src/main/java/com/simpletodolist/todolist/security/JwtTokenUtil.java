package com.simpletodolist.todolist.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.simpletodolist.todolist.domain.bind.JWT;
import com.simpletodolist.todolist.domain.bind.MemberDTO;
import com.simpletodolist.todolist.domain.entity.Member;
import com.simpletodolist.todolist.exception.member.NoMemberFoundException;
import com.simpletodolist.todolist.repository.MemberRepository;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtTokenUtil {
    private final MemberRepository memberRepository;
    private final ObjectMapper objectMapper;

    @Value("${JWT_SECRET}")
    private String jwtSecret;

    private enum TokenType {
        BEARER
    }


    public String generateAccessToken(String username) throws JsonProcessingException {
        Member member = memberRepository.findByUsername(username).orElseThrow(() -> new NoMemberFoundException(username));
        return generateAccessToken(member);
    }

    public JWT generateJWT(String username) throws JsonProcessingException {
        Member member = memberRepository.findByUsername(username).orElseThrow(() -> new NoMemberFoundException(username));
        String accessToken = generateAccessToken(member);
        String refreshToken = generateRefreshToken(member);
        return new JWT(accessToken, refreshToken);
    }

    private JwtBuilder buildUntilExpirationDate(Member member) throws JsonProcessingException {
        return Jwts.builder()
                .setSubject(objectMapper.writeValueAsString(new MemberDTO(member))) // Store authenticated member's user id, username to JWT.
                .setIssuer("SimpleTodoList") // Made by JWT_ISSUER, which is "SimpleTodoList" at now.
                .setIssuedAt(new Date()) // Issued at today.
                .signWith(SignatureAlgorithm.HS256, jwtSecret); // Signing algorithm is HS256.
    }

    public String generateAccessToken(Member member) throws JsonProcessingException {
        JwtBuilder jwtBuilder = buildUntilExpirationDate(member);
        return jwtBuilder
                .setExpiration(new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000)) // Expiration date of access token is 1 day.
                .compact();
    }

    public String generateRefreshToken(Member member) throws JsonProcessingException {
        JwtBuilder jwtBuilder = buildUntilExpirationDate(member);
        return jwtBuilder
                .setExpiration(new Date(System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000)) // Expiration date of refresh token is 1 week.
                .compact();
    }

    public Claims parseBearerJWTSubject(String token) {
        return Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token.substring(TokenType.BEARER.name().length()+1))
                .getBody();
    }

    private MemberDTO getUser(Claims claims) {
        try {
            return objectMapper.readValue(claims.getSubject(), MemberDTO.class);
        } catch (JsonProcessingException exception) {
            throw new JwtException("JWT subject is malformed.");
        }
    }

    public String getUsername(Claims claims) { return getUser(claims).getUsername(); }
}
