package com.booking.infrastructure.config.jwt;

import com.booking.domain.models.entities.Role;
import com.booking.domain.models.entities.Token;
import com.booking.domain.models.entities.User;
import com.booking.domain.repositories.TokenRepository;

import com.booking.enums.ResultCode;
import com.booking.exceptions.InvalidParamException;
import com.booking.exceptions.ExpiredTokenException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.security.SecureRandom;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@Service
@RequiredArgsConstructor
public class JwtTokenUtils {
    @Value("${jwt.expiration}")
    private int expiration;

    @Value("${jwt.expiration-refresh-token}")
    private int expirationRefreshToken;

    @Value("${jwt.secretkey}")
    private String secretKey;

    private TokenRepository tokenRepository;


    @Autowired
    public JwtTokenUtils(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }
    private static final Logger logger = LoggerFactory.getLogger(JwtTokenUtils.class);

    public String generateToken(User user) throws Exception {
        try {
            Map<String, Object> claims = new HashMap<>();
            claims.put("userId", user.getUserId());
            claims.put("username", user.getUsername());
            claims.put("email", user.getEmail());

            List<String> roleNames = user.getRoles()
                    .stream()
                    .map(Role::getNameRole)
                    .collect(Collectors.toList());
            claims.put("roles", roleNames);

            String jwtId = UUID.randomUUID().toString();
            Date issuedAt = new Date();
            Date expirationDate = new Date(issuedAt.getTime() + expiration * 1000L);

            return Jwts.builder()
                    .setClaims(claims)
                    .setSubject(user.getUsername()) // subject là định danh chính (thường là username hoặc email)
                    .setId(jwtId)
                    .setIssuedAt(issuedAt)
                    .setExpiration(expirationDate)
                    .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                    .compact();

        } catch (Exception e) {
            throw new InvalidParamException(ResultCode.DATA_NOT_FOUND);
        }
    }

    private static String getSubject(User user) {
        String subject = user.getUsername();
        if (subject == null || subject.trim().isEmpty()) {
            subject = user.getEmail();
        }
        return subject;
    }

    private Key getSignInKey() {
        byte[] bytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(bytes);
    }

    private String generateSecretKey() {
        SecureRandom random = new SecureRandom();
        byte[] keyBytes = new byte[32]; // 256-bit key
        random.nextBytes(keyBytes);
        String secretKey = Encoders.BASE64.encode(keyBytes);
        return secretKey;
    }

    public String extractJwtId(String token) {
        return extractClaim(token, Claims::getId);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public  <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = this.extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    //check expiration
    public boolean isTokenExpired(String token) {
        Date expirationDate = this.extractClaim(token, Claims::getExpiration);
        return expirationDate.before(new Date());
    }

    public String getSubject(String token) {
        return  extractClaim(token, Claims::getSubject);
    }

    public boolean validateToken(String token, User userDetails) {
        try {
            String subject = extractClaim(token, Claims::getSubject);
            return isTokenValid(token, subject, userDetails);
        } catch (JwtException | IllegalArgumentException e) {
            logJwtException(e);
            return false;
        }
    }

    private boolean isTokenValid(String token, String subject, User userDetails) {
        Token existingToken = tokenRepository.findByToken(token).orElse(null);
        if (existingToken == null || existingToken.isRevoked() || !userDetails.isActive()) {
            return false;
        }
        return subject.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    private static final Map<Class<? extends Exception>, String> JWT_EXCEPTION_MESSAGES = new HashMap<>();

    static {
        JWT_EXCEPTION_MESSAGES.put(MalformedJwtException.class, "Invalid JWT token: ");
        JWT_EXCEPTION_MESSAGES.put(ExpiredJwtException.class, "JWT token is expired: ");
        JWT_EXCEPTION_MESSAGES.put(UnsupportedJwtException.class, "JWT token is unsupported: ");
        JWT_EXCEPTION_MESSAGES.put(IllegalArgumentException.class, "JWT claims string is empty: ");
    }

    private void logJwtException(Exception e) {
        String message = JWT_EXCEPTION_MESSAGES.getOrDefault(e.getClass(), "JWT exception: ");
        logger.error("{}{}", message, e.getMessage());
    }

    public Claims verifyToken(String token, boolean isRefresh) throws Exception {
        try {
            extractAllClaims(token);

            isTokenExpired(token);

            // Check token is revoked
            Token existingToken = tokenRepository.findByToken(token)
                    .orElseThrow(() -> new ExpiredTokenException("Token is missing or invalid"));

            if (existingToken.isRevoked() || existingToken.isExpired()) {
                throw new ExpiredTokenException("Token is revoked or expired");
            }

            return extractAllClaims(token);
        } catch (JwtException | IllegalArgumentException e) {
            throw new Exception("AUTHENTICATE", e);
        }
    }
}
