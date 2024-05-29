package com.wahidassistant.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {
    private static final String SECRET_KEY = "75a68668f0266b0b8c0c6df3919c2ea2af9519a3ab2935bae50a254ba0857556";
    private final int expirationTimeInHours = 24; // with Wahid
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) { // returns claim from a token. Author Pehr Nortén.
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    } // returns String token generated with the help of only Userdetails. Author Pehr Nortén.

    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * expirationTimeInHours)) // 24 hours // Med Wahid
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    } //returns String token generatd with help of claims and userdetails. Author Pehr Nortén.

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    } // method returning a boolean that's true if the username from the token equals that of the userDetails username. Author Pehr Nortén.

    public boolean isTokenExpired(String token) {
        /*
        Wahids testlösning inte ännu klar måste hantera ifall token ä rexpired så den skickar till startsidan
        try {
            Claims claims = Jwts.parserBuilder().setSigningKey(SECRET_KEY).build().parseClaimsJws(token).getBody();
            Date expiration = claims.getExpiration();
            return expiration.before(new Date());
        }catch (ExpiredJwtException e){
            e.printStackTrace();
            return true;
        }

         */
        return extractExpiration(token).before(new Date());
    } // returns a true boolean if the token expiration date is before the date now. Author Pehr Nortén. Contributor Wahid Hassani.

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    } // returns expiration date from the token. Author Pehr Nortén.

    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build().parseClaimsJws(token)
                .getBody();
    } // returns all claims from the token. Author Pehr Nortén.

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    } // encodes the secret key and returns it. Author Pehr Nortén.

    public int getExpirationTimeInSeconds() {
        return expirationTimeInHours * 60 * 60;
    } // return the seconds of the expiration time. Author Pehr Nortén. Contributor Wahid Hassani.
}
