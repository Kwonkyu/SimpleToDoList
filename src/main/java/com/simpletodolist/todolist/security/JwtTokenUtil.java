package com.simpletodolist.todolist.security;

import com.simpletodolist.todolist.domain.entity.Member;
import com.simpletodolist.todolist.exception.member.NoMemberFoundException;
import com.simpletodolist.todolist.repository.MemberRepository;
import io.jsonwebtoken.Claims;
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
    private String JWT_SECRET;
    private String JWT_ISSUER = "SimpleTodoList";

    private enum TokenType {
        BEARER
    }


    public String generateAccessToken(String memberUserId) {
        Member member = memberRepository.findByUserId(memberUserId).orElseThrow(NoMemberFoundException::new);
        return generateAccessToken(member);
    }

    public String generateAccessToken(Member member) {
        return Jwts.builder()
                // Store authenticated member's user id, username to JWT.
                // TODO: jsonify?
                .setSubject(String.format("%s / %s / %s", member.getId(), member.getUserId(), member.getUsername()))
                // Made by JWT_ISSUER, which is "SimpleTodoList" at now.
                .setIssuer(JWT_ISSUER)
                .setIssuedAt(new Date())
                // Expiration date of generated JWT is 1 day.
                .setExpiration(new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000))
                // Signing algorithm is HS256.
                .signWith(SignatureAlgorithm.HS256, JWT_SECRET)
                .compact(); // build JWT.
    }


    public Claims validateBearerJWT(String token) {
        return Jwts.parser().setSigningKey(JWT_SECRET).parseClaimsJws(token.substring(TokenType.BEARER.name().length()+1)).getBody();
    }


    public long getIdFromClaims(Claims claims) { return Long.parseLong(claims.getSubject().split(" / ")[0]); }

    public String getUserIdFromClaims(Claims claims) {
        return claims.getSubject().split(" / ")[1];
    }

    public String getUsernameFromClaims(Claims claims) {
        return claims.getSubject().split(" / ")[2];
    }

    public Date getExpirationDateFromClaims(Claims claims) {
        return claims.getExpiration();
    }
}
