package com.booking.service.authen;

import com.booking.domain.models.entities.User;
import com.booking.model.dto.request.LogoutRequest;
import com.booking.model.dto.request.UserLoginRequest;
import com.booking.model.dto.request.ValidateTokenRequest;
import com.booking.model.dto.response.ValidateTokenResponse;

public interface IAuthenService {
    String login(UserLoginRequest userLoginDTO) throws Exception;

    User getUserDetailsFromToken(String token) throws Exception;

    User getUserDetailsFromRefreshToken(String refreshToken) throws Exception;

    ValidateTokenResponse validateToken(ValidateTokenRequest validateTokenDTO) throws Exception;

    void logout(LogoutRequest logoutRequest) throws Exception;
}
