package edu.eci.arep.util;

import com.auth0.jwk.JwkProvider;
import com.auth0.jwk.JwkProviderBuilder;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;

import java.net.URL;
import java.security.interfaces.RSAPublicKey;

/**
 * Validates Auth0 JWT Bearer tokens using the Auth0 JWKS endpoint.
 * Verifies signature, issuer, and audience offline via RSA public keys.
 *
 * @author David Velásquez, Jesús Pinzón, Santiago Díaz
 * @version 1.0
 * @since 2026-04-17
 */
public class Auth0TokenValidator {

    private final JwkProvider jwkProvider;
    private final String issuer;
    private final String audience;

    /**
     * @param issuer   Auth0 domain issuer URI (e.g. https://your-domain.auth0.com/)
     * @param audience Auth0 API audience identifier
     */
    public Auth0TokenValidator(String issuer, String audience) {
        this.issuer = issuer;
        this.audience = audience;
        try {
            this.jwkProvider = new JwkProviderBuilder(new URL(issuer + ".well-known/jwks.json")).build();
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid issuer URI: " + issuer, e);
        }
    }

    /**
     * Validates the given Bearer token string and returns the decoded JWT.
     *
     * @param bearerToken the raw Authorization header value (with or without "Bearer " prefix)
     * @return the validated and decoded JWT
     * @throws com.auth0.jwt.exceptions.JWTVerificationException if the token is invalid or expired
     */
    public DecodedJWT validate(String bearerToken) {
        try {
            String token = bearerToken.startsWith("Bearer ") ? bearerToken.substring(7) : bearerToken;
            DecodedJWT decoded = JWT.decode(token);
            RSAPublicKey publicKey = (RSAPublicKey) jwkProvider.get(decoded.getKeyId()).getPublicKey();
            Algorithm algorithm = Algorithm.RSA256(publicKey, null);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer(issuer)
                    .withAudience(audience)
                    .build();
            return verifier.verify(token);
        } catch (Exception e) {
            throw new RuntimeException("JWT validation failed: " + e.getMessage(), e);
        }
    }
}
