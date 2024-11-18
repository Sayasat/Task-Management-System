package com.programmingtechie.taskmanagementsystem.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JWTUtil {

    @Value("${jwt.secret}")
    private String secret;
    private static final String SUBJECT = "User Details";
    private static final String ISSUER = "techno";
    private static final long ACCESS_TOKEN_EXPIRATION = 60 * 60 * 1000;
    private static final long REFRESH_TOKEN_EXPIRATION = 7 * 24 * 60 * 60 * 1000;

    public String generateAccessToken(String username, String role) {
        return generateToken(username, role, ACCESS_TOKEN_EXPIRATION, "access");
    }

    public String generateRefreshToken(String username) {
        return generateToken(username, null, REFRESH_TOKEN_EXPIRATION, "refresh");
    }

    public String generateToken(String username, String role, long expirationTime, String tokenType) {
        Date expirationDate = new Date(System.currentTimeMillis() + expirationTime);

        var jwtBuilder = JWT.create()
                .withSubject(SUBJECT)
                .withClaim("username", username)
                .withClaim("token_type", tokenType)
                .withIssuedAt(new Date())
                .withIssuer(ISSUER)
                .withExpiresAt(expirationDate);

        if (role != null) {
            jwtBuilder.withClaim("role", role);
        }

        return jwtBuilder.sign(Algorithm.HMAC256(secret));
    }

    public String validateTokenAndRetrieveClaim(String token, String tokenType) throws JWTVerificationException {
        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(secret))
                .withSubject(SUBJECT)
                .withIssuer(ISSUER)
                .build();

        DecodedJWT jwt = verifier.verify(token);

        String type = jwt.getClaim("token_type").asString();
        if (!type.equals(tokenType)) {
            throw new JWTVerificationException("Invalid token type");
        }

        return jwt.getClaim("username").asString();
    }

    public String getRoleFromToken(String token) throws JWTVerificationException {
        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(secret))
                .withSubject(SUBJECT)
                .withIssuer(ISSUER)
                .build();

        DecodedJWT jwt = verifier.verify(token);
        return jwt.getClaim("role").asString();
    }
}
