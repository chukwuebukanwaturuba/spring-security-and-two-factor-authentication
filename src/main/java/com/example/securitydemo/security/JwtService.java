package com.example.securitydemo.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {
    @Value("${application.jwt.security.secretKey}")
    String secretKey;
    @Value("${application.jwt.security.expiration}") //30mins
    long expiration;

    //Method to extract Username
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    //Method to extractClaims using a functional interface
    public <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        final  Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }
    //Building the token
    public String buildToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails,
            long expiration,
            HttpServletRequest servletRequest) {
        Claims claims = Jwts.claims().setSubject(userDetails.getUsername());
        claims.putAll(extraClaims);
        claims.setIssuer(servletRequest.getRequestURL().toString());
        claims.put("iat", new Date(System.currentTimeMillis()));
        claims.put("exp", new Date(System.currentTimeMillis() + expiration));
        return Jwts
                .builder()
                .setClaims(claims)
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    //Helper method to help generate Tokens with extra claims
    public String generateToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails,HttpServletRequest servletRequest
    ){
        return buildToken(extraClaims, userDetails, expiration,servletRequest);
    }

    //Method to generate the JwtToken needed to Authenticate users
    public String generateToken(UserDetails userDetails, HttpServletRequest servletRequest){
        return generateToken(new HashMap<>(), userDetails, servletRequest);
    }
    //Method to extract the all claims related to the token
    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    //Method to check if token is valid
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String userName = extractUsername(token);
        return (userName.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    //Method to check if token is expired
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
        
    }

    //Method to extract the expiration of the Jwt
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // Helper method to get the signing key
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
