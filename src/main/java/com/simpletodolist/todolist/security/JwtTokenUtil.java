package com.simpletodolist.todolist.security;

import com.simpletodolist.todolist.domain.entity.Member;
import com.simpletodolist.todolist.exception.general.AuthenticationFailedException;
import com.simpletodolist.todolist.exception.general.AuthorizationFailedException;
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


    public Claims validateJwtToken(String token) {
        try {
            return Jwts.parser().setSigningKey(JWT_SECRET).parseClaimsJws(token).getBody();
        } catch (SignatureException | MalformedJwtException | ExpiredJwtException | UnsupportedJwtException | IllegalArgumentException ex) {
            throw new AuthenticationFailedException("JWT Validation Failed", ex.getLocalizedMessage());
        }
    }

    public void validateRequestedUserIdWithJwt(String requestedUserId, String token) {
        validateRequestedUserIdWithJwt(requestedUserId, token, AuthorizationFailedException.DEFAULT_MESSAGE);
    }

    /**
     * Validate if requested user id is equal with authenticated user id.
     * @param requestedUserId Requested user id.
     * @param token Authenticated JWT.
     * @param message AuthorizationFailedException's message when authorization failed.
     * @throws AuthorizationFailedException when requested user id not matched with authenticated user id.
     */
    public void validateRequestedUserIdWithJwt(String requestedUserId, String token, String message) throws AuthorizationFailedException {
        Claims claims = validateJwtToken(token);
        String tokenUserId = getUserIdFromClaims(claims);
        if(!tokenUserId.equals(requestedUserId)) {
            throw new AuthorizationFailedException(AuthorizationFailedException.DEFAULT_ERROR, message);
        }
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
