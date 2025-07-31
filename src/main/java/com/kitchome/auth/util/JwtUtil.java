package com.kitchome.auth.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {
    @Value("${jwt.secret}")
    private String SECRET;
    @Value("${jwt.expiration-ms")
    private long expieryDuration;

    public String extractUsernane(String token){
        return extractClaims(token,Claims::getSubject);
    }

    public <T> T extractClaims(String token, Function<Claims, T > claimResolver){
        final Claims cx =extractAllClaims(token);
        return claimResolver.apply(cx);
    }
    public Date extractExpiration(String token){
        return extractClaims(token,Claims::getExpiration);
    }
    public boolean isExpired(String token){
        return extractExpiration(token).before(new Date());
    }
    public boolean isValid(String token, UserDetails user){
        String userName=extractUsernane(token);
        return user.getUsername().equals(extractUsernane(token)) && !isExpired(token);

    }

    private Claims extractAllClaims(String token){
        return Jwts.
                parser()
                .setSigningKey(getSigningkey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    private String createToken(Map<String,String> claims,String username){
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()+expieryDuration))
                .signWith(getSigningkey(), SignatureAlgorithm.HS256).compact();
    }
    public String generateToken
    private Key getSigningkey(){
        byte[] keyBytes= Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
