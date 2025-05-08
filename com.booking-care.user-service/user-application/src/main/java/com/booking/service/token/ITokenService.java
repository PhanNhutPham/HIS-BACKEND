package com.booking.service.token;

import com.booking.domain.models.entities.Token;
import com.booking.domain.models.entities.User;

public interface ITokenService {
    Token addToken(User user, String token, boolean isMobileDevice);
    Token refreshToken(String refreshToken, User user) throws Exception;
}
