package com.booking.service.token;

import com.booking.domain.models.entities.Token;
import com.booking.domain.models.entities.User;
import com.booking.domain.repositories.TokenRepository;
import com.booking.enums.ResultCode;
import com.booking.exceptions.DataNotFoundException;
import com.booking.exceptions.ExpiredTokenException;
import com.booking.infrastructure.config.jwt.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TokenService implements ITokenService{
    private static final int MAX_TOKENS = 3;
    @Value("${jwt.expiration}")
    private int expiration;

    @Value("${jwt.expiration-refresh-token}")
    private int expirationRefreshToken;

    private  TokenRepository tokenRepository;
    private  JwtTokenUtils jwtTokenUtil;

    @Autowired
    public TokenService(TokenRepository tokenRepository, JwtTokenUtils jwtTokenUtil) {
        this.tokenRepository = tokenRepository;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Transactional
    @Override
    public Token addToken(User user, String token, boolean isMobileDevice) {
        List<Token> userTokens = tokenRepository.findByUser(user);
        int tokenCount = userTokens.size();
        if (tokenCount >= MAX_TOKENS) {
            boolean hasNonMobileToken = !userTokens.stream().allMatch(Token::isMobile);
            Token tokenToDelete;
            if (hasNonMobileToken) {
                tokenToDelete = userTokens.stream()
                        .filter(userToken -> !userToken.isMobile())
                        .findFirst()
                        .orElse(userTokens.get(0));
            } else {
                tokenToDelete = userTokens.get(0);
            }
            tokenRepository.delete(tokenToDelete);
        }
        long expirationInSeconds = expiration;
        LocalDateTime expirationDateTime = LocalDateTime.now().plusSeconds(expirationInSeconds);

        String jwtId = jwtTokenUtil.extractJwtId(token);

        Token newToken = Token.builder()
                .user(user)
                .jwtId(jwtId)
                .token(token)
                .revoked(false)
                .expired(false)
                .tokenType("Bearer")
                .expirationDate(expirationDateTime)
                .isMobile(isMobileDevice)
                .build();

        newToken.setRefreshToken(UUID.randomUUID().toString());
        newToken.setRefreshExpirationDate(LocalDateTime.now().plusSeconds(expirationRefreshToken));
        tokenRepository.save(newToken);
        return newToken;
    }

    @Transactional
    @Override
    public Token refreshToken(String refreshToken, User user) throws Exception {
        Token existingToken = tokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new DataNotFoundException(ResultCode.REFRESH_TOKEN_NOT_FOUND));

        if (existingToken.getRefreshExpirationDate().isBefore(LocalDateTime.now())
                || existingToken.isRevoked() || existingToken.isExpired()) {
            tokenRepository.delete(existingToken);
            throw new ExpiredTokenException("Refresh token is expired");
        }

        String token = jwtTokenUtil.generateToken(user);
        LocalDateTime expirationDateTime = LocalDateTime.now().plusSeconds(expiration);

        existingToken.setExpirationDate(expirationDateTime);
        existingToken.setToken(token);
        existingToken.setRefreshToken(UUID.randomUUID().toString());
        existingToken.setRefreshExpirationDate(LocalDateTime.now().plusSeconds(expirationRefreshToken));

        return existingToken;
    }
}
