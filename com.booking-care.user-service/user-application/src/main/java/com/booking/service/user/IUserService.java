package com.booking.service.user;

import com.booking.domain.models.entities.User;
import com.booking.exceptions.DataNotFoundException;
import com.booking.exceptions.ExistsException;
import com.booking.exceptions.InvalidPasswordException;
import com.booking.model.dto.request.ResetPasswordRequest;
import com.booking.model.dto.request.UpdateUserRequest;
import com.booking.model.dto.request.UserCreateRequest;
import com.booking.model.dto.request.UserRegisterRequest;
import com.booking.model.dto.response.UserPageResponse;
import org.springframework.web.multipart.MultipartFile;

public interface IUserService {
    User createUser(UserRegisterRequest userRegisterRequest) throws Exception;

    User updateUser(String userId, UpdateUserRequest updateUserRequest) throws DataNotFoundException;
    User createUserByAdmin(UserCreateRequest request) throws ExistsException, DataNotFoundException;
    UserPageResponse findAllUsers(Integer pageNumber, Integer pageSize, String sortBy, String dir);

    void resetPassword(String userId, ResetPasswordRequest resetPasswordRequest)
            throws InvalidPasswordException, DataNotFoundException;

    void blockOrEnable(String userId, Boolean active) throws DataNotFoundException;

    void changeProfileImage(String userId, String imageName) throws Exception;

    void forgotPassword(String email) throws DataNotFoundException;

    boolean verifyOtpAndResetPassword(String email, String otp, String newPassword) throws DataNotFoundException;

    void uploadAvatar(String userId, MultipartFile file) throws Exception;
}
