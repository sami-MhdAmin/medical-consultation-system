package com.grad.akemha.security;

import com.grad.akemha.entity.User;
import com.grad.akemha.exception.NotFoundException;
import com.grad.akemha.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

@Service
public class JwtService {
    private static final String SECRET_KEY = "97e74ee81f5206429721abf0cd87b2450299e2ba3be8feca9d85d3c2c18842e7";
    @Autowired
    private UserRepository userRepository;

    // one year number: 31556952000L
    public String getIdentifier(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(getSignInKey()) // TODO: check the deprecated
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

//        public String getId(String token){ //when we will use this method? when we receive the token from the client when client makes a request to the server and through this token we get the username
//        String claims = Jwts.parser()
//                .setSigningKey(getSignInKey())
//                .build()
//                .parseClaimsJws(token)
//                .getBody().getId();
//        return claims;
//    }

    public User extractUserFromToken(HttpHeaders httpHeaders) {
        String token = Objects.requireNonNull(httpHeaders.get("Authorization")).get(0);
        String jwt = token.replace("Bearer ", "");
        String userEmail = Jwts
                .parser()
                .verifyWith(getSignInKey()) //56:33
                .build()
                .parseSignedClaims(jwt)
                .getPayload()
                .getSubject();

        Optional<User> optionalUser = userRepository.findByEmail(userEmail);
        if (optionalUser.isPresent()) {
            return optionalUser.get();
        } else {
            throw new NotFoundException("Something Went Wrong When getting the User");
        }
    }

    public User extractUserFromJwtToken(String jwtToken) {
        String userEmail = Jwts
                .parser()
                .verifyWith(getSignInKey()) //56:33
                .build()
                .parseSignedClaims(jwtToken)
                .getPayload()
                .getSubject();

        Optional<User> optionalUser = userRepository.findByEmail(userEmail);
        if (optionalUser.isPresent()) {
            return optionalUser.get();
        } else {
            throw new NotFoundException("Something Went Wrong When getting the User");
        }
    }

    public String extractUserId(HttpHeaders httpHeaders) { //when we will use this method? when we receive the token from the client when client makes a request to the server and through this token we get the username
        // Assuming your token is in the "Authorization" header
        String token = httpHeaders.getFirst("Authorization");
        String jwt = token.replace("Bearer ", "");
        var id = extractAllClaims(jwt).get("id");
        return String.valueOf(id);
    }

    /*
    *
    public String extractUserId(HttpHeaders httpHeaders) { //when we will use this method? when we receive the token from the client when client makes a request to the server and through this token we get the username
        // Assuming your token is in the "Authorization" header
        String token = httpHeaders.getFirst("Authorization");
        String jwt = token.replace("Bearer ", "");
        var id = extractAllClaims(jwt);
        return String.valueOf(id);
    }*/

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generateToken(User user) {
        Map<String, Object> claims = Map.of(
                "id", user.getId(),
                "role", user.getRole()
        );
        return buildToken(claims, user);
    }

    public String buildToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails
    ) {
        return Jwts
                .builder()
                .claims(extraClaims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                // the token will live for one week not one year
//                .expiration(new Date(System.currentTimeMillis() + (1000 * 3600 * 24 * 7))) // 1 week
                .expiration(new Date(System.currentTimeMillis() + 31556952000L)) // 1 year
                .signWith(getSignInKey(), Jwts.SIG.HS256)
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = getIdentifier(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
