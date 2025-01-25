package com.store.storeapp.Utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.SignatureException;

@Component
public class JwtUtils {
    private static String SECRET_KEY = "3cfa76ef14937c1c0ea519f8fc057a80fcd04a7420f8e8bcd0a7567c272e007b";
    SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET_KEY));
    public boolean isAuthenticated(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            return claims != null;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
