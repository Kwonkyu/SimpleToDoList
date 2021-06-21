package com.simpletodolist.todolist.security;

import com.simpletodolist.todolist.domain.entity.Member;
import com.simpletodolist.todolist.exception.general.AuthenticationFailedException;
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

    @Value("${JWT_SECRET}")
    private String JWT_SECRET;
    private String JWT_ISSUER = "SimpleTodoList";


    public String generateAccessToken(String memberUserId) {
        Member member = memberRepository.findByUserId(memberUserId).orElseThrow(NoMemberFoundException::new);
        return generateAccessToken(member);
    }

    public String generateAccessToken(Member member) {
        return Jwts.builder()
                // Store authenticated member's user id, username to JWT.
                .setSubject(String.format("%s / %s", member.getUserId(), member.getUsername()))
                // Made by JWT_ISSUER, which is "SimpleTodoList" at now.
                .setIssuer(JWT_ISSUER)
                .setIssuedAt(new Date())
                // Expiration date of generated JWT is 1 day.
                .setExpiration(new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000))
                // Signing algorithm is HS256.
                .signWith(SignatureAlgorithm.HS256, JWT_SECRET)
                .compact(); // build JWT.
    }

    public void validateJwtToken(String token) {
        try {
            Jwts.parser().setSigningKey(JWT_SECRET).parseClaimsJws(token);
        } catch (SignatureException | MalformedJwtException | ExpiredJwtException | UnsupportedJwtException | IllegalArgumentException ex) {
            throw new AuthenticationFailedException("JWT Validation Failed", ex.getLocalizedMessage());
        }
    }

    private Claims parseToken(String token) {
        return Jwts.parser()
                .setSigningKey(JWT_SECRET)
                .parseClaimsJws(token) // claims for signed, plaintext for unsigned.
                .getBody();
    }

    public String getUserIdFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.getSubject().split(" / ")[0];
    }

    public String getUsernameFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.getSubject().split(" / ")[1];
    }

    public Date getExpirationDateFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.getExpiration();
    }
}
