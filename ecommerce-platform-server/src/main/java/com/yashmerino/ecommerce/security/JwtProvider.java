package com.yashmerino.ecommerce.security;

/*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
 + MIT License
 +
 + Copyright (c) 2023 Artiom Bozieac
 +
 + Permission is hereby granted, free of charge, to any person obtaining a copy
 + of this software and associated documentation files (the "Software"), to deal
 + in the Software without restriction, including without limitation the rights
 + to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 + copies of the Software, and to permit persons to whom the Software is
 + furnished to do so, subject to the following conditions:
 +
 + The above copyright notice and this permission notice shall be included in all
 + copies or substantial portions of the Software.
 +
 + THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 + IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 + FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 + AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 + LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 + OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 + SOFTWARE.
 +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/

import com.yashmerino.ecommerce.utils.ApplicationProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

import static com.yashmerino.ecommerce.security.SecurityConstants.JWT_EXPIRATION;
import static io.jsonwebtoken.SignatureAlgorithm.HS512;

/**
 * Class that generates JWT tokens.
 */
@Component
@SuppressWarnings("java:S6437")
public class JwtProvider {

    /**
     * Application properties.
     */
    private final ApplicationProperties applicationProperties;

    /**
     * Constructor.
     *
     * @param applicationProperties is the application's properties.
     */
    public JwtProvider(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
    }

    /**
     * Generates a token.
     *
     * @param authentication is the authentication object.
     * @return JWT Token.
     */
    public String generateToken(Authentication authentication) {
        String username = authentication.getName();
        Date currentDate = new Date();
        Date expiringDate = new Date(currentDate.getTime() + JWT_EXPIRATION);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(currentDate)
                .setExpiration(expiringDate)
                .signWith(getSigningKey(), HS512)
                .compact();
    }

    /**
     * Returns the username from a JWT Token.
     *
     * @param token is the JWT token.
     * @return the username.
     */
    public String getUsernameFromJWT(final String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    /**
     * Validates a JWT Token.
     *
     * @param token is the JWT token.
     * @return <code>true</code> if JWT token is valid and <code>false</code> otherwise.
     */
    public boolean validateToken(final String token) {
        try {
            Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            throw new AuthenticationCredentialsNotFoundException("JWT Token is not valid, it could be because it's expired or incorrect.");
        }
    }

    /**
     * Gets signing key from JWT secret.
     *
     * @return <code>Key</code>.
     */
    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(applicationProperties.jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
