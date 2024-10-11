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
    private final int expirationTimeInHours = 2140; // with Wahid
    /*
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }


     */
    public String extractUsername(String token) {
        try {
            return extractClaim(token, Claims::getSubject);
        } catch (ExpiredJwtException e) {
            System.out.println("Cannot extract username, token has expired.");
            return null;  // Handle it as needed, maybe return null or throw a custom exception weq
        }
    }


    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) { // returns claim from a token. Author Pehr Nortén.
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    } // returns String token generated with the help of only Userdetails. Author Pehr Nortén.

    /*
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * expirationTimeInHours)) // 24 hours // Med Wahid
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    } //returns String token generatd with help of claims and userdetails. Author Pehr Nortén.


     */
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        Date now = new Date(); // Current time
        long expirationTimeInMillis = 3600000L * expirationTimeInHours; // Convert hours to milliseconds
        Date expirationDate = new Date(now.getTime() + expirationTimeInMillis); // Set the expiration date

        // Log the issued and expiration time
        System.out.println("Generating token for user: " + userDetails.getUsername());
        System.out.println("Token issued at: " + now);
        System.out.println("Token expiration at: " + expirationDate);

        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(now) // Set issued time
                .setExpiration(expirationDate) // Set calculated expiration time
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }



    /*
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    } // method returning a boolean that's true if the username from the token equals that of the userDetails username. Author Pehr Nortén.


     */

    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            final String username = extractUsername(token);
            return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
        } catch (ExpiredJwtException e) {
            // Token has expired, handle it here
            System.out.println("Token has expired: " + e.getMessage());
            return false;  // Return false as the token is no longer valid
        }
    }

    /*
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
    /*
        return extractExpiration(token).before(new Date());
    } // returns a true boolean if the token expiration date is before the date now. Author Pehr Nortén. Contributor Wahid Hassani.


     */

    public boolean isTokenExpired(String token) {
        try {
            return extractExpiration(token).before(new Date());
        } catch (ExpiredJwtException e) {
            // Token has expired, return true
            System.out.println("Token has expired: " + e.getMessage());
            return true; // Token is expired
        }
    }


    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    } // returns expiration date from the token. Author Pehr Nortén.

    /*
    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build().parseClaimsJws(token)
                .getBody();
    } // returns all claims from the token. Author Pehr Nortén.


     */

    private Claims extractAllClaims(String token) {
        try {
            return Jwts
                    .parserBuilder()
                    .setSigningKey(getSignInKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            // Token is expired, handle it as per your need
            System.out.println("Token has expired: " + e.getMessage());
            throw e; // Rethrow or handle based on your logic
        } catch (Exception e) {
            // Any other JWT parsing exception
            System.out.println("Error parsing token: " + e.getMessage());
            throw e; // Optionally handle or log other parsing exceptions
        }
    }


    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    } // encodes the secret key and returns it. Author Pehr Nortén.

    public int getExpirationTimeInSeconds() {
        return expirationTimeInHours * 60 * 60;
    } // return the seconds of the expiration time. Author Pehr Nortén. Contributor Wahid Hassani.
}
