package com.booking.service.authen;

import com.booking.domain.models.entities.Token;
import com.booking.domain.models.entities.User;
import com.booking.domain.repositories.TokenRepository;
import com.booking.domain.repositories.UserRepository;
import com.booking.enums.ResultCode;
import com.booking.exceptions.DataNotFoundException;
import com.booking.exceptions.ExpiredTokenException;
import com.booking.infrastructure.config.jwt.JwtTokenUtils;
import com.booking.model.dto.request.LogoutRequest;
import com.booking.model.dto.request.UserLoginRequest;
import com.booking.model.dto.request.ValidateTokenRequest;
import com.booking.model.dto.response.ValidateTokenResponse;
import com.booking.regex.RegexValidations;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthenService implements IAuthenService{

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final JwtTokenUtils jwtTokenUtil;
    private final AuthenticationManager authenticationManager;

    @Override
    public String login(UserLoginRequest userLoginRequest) throws Exception {
        Optional<User> optionalUser = Optional.empty();
        String subject = null;

        if (userLoginRequest.getUsername() != null && !userLoginRequest.getUsername().isBlank()) {
            optionalUser = userRepository.findByUsername(userLoginRequest.getUsername());
            subject = userLoginRequest.getUsername();
        }

        // If the user is not found by username, check by email
        if (optionalUser.isEmpty() && userLoginRequest.getEmail() != null) {
            optionalUser = userRepository.findByEmail(userLoginRequest.getEmail());
            subject = userLoginRequest.getEmail();
        }

        // If user is not found, throw an exception
        if (optionalUser.isEmpty()) {
            throw new DataNotFoundException(ResultCode.USER_NOT_FOUND);
        }

        // Get the existing user
        User existingUser = optionalUser.get();

        if(!existingUser.isActive()) {
            throw new DataNotFoundException(ResultCode.USER_NOT_FOUND);
        }

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                subject, userLoginRequest.getPassword(),
                existingUser.getAuthorities()
        );

        //authenticate with Java Spring security
        authenticationManager.authenticate(authenticationToken);
        return jwtTokenUtil.generateToken(existingUser);
    }

    @Override
    public User getUserDetailsFromToken(String token) throws Exception {
        if(jwtTokenUtil.isTokenExpired(token)) {
            throw new ExpiredTokenException("Token is expired");
        }
        String subject = jwtTokenUtil.getSubject(token);
        Optional<User> user;
        user = userRepository.findByUsername(subject);
        if (user.isEmpty() && RegexValidations.isValidEmail(subject)) {
            user = userRepository.findByEmail(subject);
        }
        return user.orElseThrow(() -> new DataNotFoundException(ResultCode.USER_NOT_FOUND));
    }

    @Override
    public User getUserDetailsFromRefreshToken(String refreshToken) throws Exception {
        // 1. Lấy Optional<Token>
        Optional<Token> optionalToken = tokenRepository.findByRefreshToken(refreshToken);

        // 2. Nếu không có thì ném exception
        Token existingToken = optionalToken
                .orElseThrow(() -> new DataNotFoundException(ResultCode.DATA_NOT_FOUND));

        // 3. Trả về User từ token gốc
        return getUserDetailsFromToken(existingToken.getToken());
    }

    @Override
    public ValidateTokenResponse validateToken(ValidateTokenRequest validateTokenRequest) {
        String token = validateTokenRequest.getToken();
        boolean isValid = true;
        List<String> roles = null;

        try {
            // Trích xuất danh sách vai trò từ token
            roles = jwtTokenUtil.extractClaim(token, claims -> {
                List<?> rawRoles = claims.get("roles", List.class);
                return rawRoles.stream()
                        .map(Object::toString)
                        .collect(Collectors.toList());
            });

            // Xác minh token
            jwtTokenUtil.verifyToken(token, false);
        } catch (Exception e) {
            isValid = false;
        }

        return ValidateTokenResponse.builder().valid(isValid).role(roles).build();
    }

    @Override
    public void logout(LogoutRequest logoutRequest) {
        String token = logoutRequest.getToken();

        try {
            // Trích xuất ID (jti) và thời gian hết hạn
            String jwtId = jwtTokenUtil.extractClaim(token, Claims::getId);
            Date expiryTime = jwtTokenUtil.extractClaim(token, Claims::getExpiration);

            // Lấy Optional<Token>
            Optional<Token> optionalToken = tokenRepository.findByJwtId(jwtId);

            // Nếu tìm thấy thì cập nhật, không thì bỏ qua
            optionalToken.ifPresent(existingToken -> {
                existingToken.setExpired(true);
                existingToken.setRevoked(true);
                existingToken.setRefreshExpirationDate(LocalDateTime.now());
                existingToken.setExpirationDate(
                        expiryTime.toInstant()
                                .atZone(ZoneId.systemDefault())
                                .toLocalDateTime()
                );
                tokenRepository.save(existingToken);
            });
            // Nếu không tìm thấy, nghĩa là token đã bị thu hồi hoặc không hợp lệ → có thể log, nhưng không throw

        } catch (ExpiredJwtException e) {
            // Token đã hết hạn → tùy bạn có log hoặc ignore
        }
    }
}
