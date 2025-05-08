package com.booking.service.user;

import com.booking.domain.models.entities.Role;
import com.booking.domain.models.entities.Token;
import com.booking.domain.models.entities.User;
import com.booking.domain.repositories.RoleRepository;
import com.booking.domain.repositories.TokenRepository;
import com.booking.domain.repositories.UserRepository;
import com.booking.enums.ResultCode;
import com.booking.exceptions.DataNotFoundException;
import com.booking.exceptions.ExistsException;
import com.booking.exceptions.InvalidPasswordException;
import com.booking.model.dto.request.*;

import com.booking.model.dto.response.UserPageResponse;
import com.booking.utils.RoleConstant;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService{
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final TokenRepository tokenRepository;
    private final WebClient.Builder webClientBuilder;

    @Transactional
    @Override
    public User createUser(UserRegisterRequest userRegisterRequest) throws ExistsException {
        if(!userRegisterRequest.getEmail().isBlank() && userRepository.existsByEmail(userRegisterRequest.getEmail())) {
            throw new ExistsException(ResultCode.EMAIL_ALREADY_EXISTS);
            //Email already exists
        }

        if(!userRegisterRequest.getUsername().isBlank() && userRepository.existsByUsername(userRegisterRequest.getUsername())) {
            throw new ExistsException(ResultCode.USERNAME_ALREADY_EXISTS);
            //Username already exists
        }

        Set<Role> roles = new HashSet<>();
        roleRepository.findByName(RoleConstant.USER_ROLE)
                .ifPresent(roles::add);

        User user = User.builder()
                .username(userRegisterRequest.getUsername())
                .email(userRegisterRequest.getEmail())
                .password(passwordEncoder.encode(userRegisterRequest.getPassword()))
                .roles(roles)
                .active(true)
                .create_at(LocalDateTime.now())
                .build();

        return userRepository.save(user);
    }

    @Override
    public User updateUser(String userId, UpdateUserRequest updateUserRequest) throws DataNotFoundException {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException(ResultCode.USER_NOT_FOUND));


        Optional.ofNullable(updateUserRequest.getUsername())
                .filter(name -> !name.isEmpty())
                .ifPresent( existingUser::setUsername);

        Optional.ofNullable(updateUserRequest.getFirstName())
                .filter(firstName -> !firstName.isEmpty())
                .ifPresent( existingUser::setFirstName);

        Optional.ofNullable(updateUserRequest.getLastName())
                .filter(lastName -> !lastName.isEmpty())
                .ifPresent(existingUser::setLastName);

        Optional.ofNullable(updateUserRequest.getEmail())
                .filter(email -> !email.isEmpty())
                .ifPresent(existingUser::setEmail);

        Optional.ofNullable(updateUserRequest.getPhoneNumber())
                .filter(phoneNumber -> !phoneNumber.isEmpty())
                .ifPresent(existingUser::setPhoneNumber);

        return userRepository.save(existingUser);
    }

    @Override
    public User createUserByAdmin(UserCreateRequest request) throws ExistsException, DataNotFoundException {
        // 1. Kiểm tra xem username hoặc email đã tồn tại chưa
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ExistsException(ResultCode.USERNAME_ALREADY_EXISTS);
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ExistsException(ResultCode.EMAIL_ALREADY_EXISTS);
        }

        // 2. Lấy Role từ DB
        Role role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new DataNotFoundException(ResultCode.ROLE_NOT_FOUND));

        // 3. Tạo User mới
        User newUser = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .active(true)
                .roles(Set.of(role))
                .create_at(LocalDateTime.now())
                .build();

        // 4. Lưu vào DB
        return userRepository.save(newUser);
    }



    @Override
    public UserPageResponse findAllUsers(Integer pageNumber, Integer pageSize, String sortBy, String dir) {
        // Danh sách các field hợp lệ đúng theo tên thuộc tính trong class User
        List<String> allowedFields = List.of(
                "userId",
                "username",
                "email",
                "firstName",
                "lastName",
                "phoneNumber",
                "address",
                "profileImage",
                "active",
                "create_at",
                "update_at"
        );
        // Kiểm tra sortBy có hợp lệ không
        if (sortBy == null || !allowedFields.contains(sortBy)) {
            sortBy = "userId"; // fallback mặc định nếu sai hoặc null
        }

        Sort sort = dir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

        Page<User> userPage = userRepository.findAll(pageable);
        return getUsersPageResponse(pageNumber, pageSize, userPage);
    }


    private UserPageResponse getUsersPageResponse(Integer pageNumber, Integer pageSize, Page<User> userPage) {
        List<User> users = userPage.getContent();

        if(users.isEmpty()) {
            return new UserPageResponse(null, 0, 0, 0, 0, true);
        }

        List<User> listUser = new ArrayList<>(users);

        int totalPages = userPage.getTotalPages();
        int totalElements = (int) userPage.getTotalElements();
        boolean isLast = userPage.isLast();

        return new UserPageResponse(listUser, pageNumber, pageSize, totalElements, totalPages, isLast);
    }

    @Override
    public void resetPassword(String userId, ResetPasswordRequest resetPasswordRequest) throws InvalidPasswordException, DataNotFoundException {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException(ResultCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(resetPasswordRequest.getOldPassword(), existingUser.getPassword())) {
            throw new InvalidPasswordException("Old password is incorrect");
        }

        String encodedPassword = passwordEncoder.encode(resetPasswordRequest.getNewPassword());
        existingUser.setPassword(encodedPassword);

        userRepository.save(existingUser);

        List<Token> tokens = tokenRepository.findByUser(existingUser);
        tokenRepository.deleteAll(tokens);
    }

    @Override
    public void blockOrEnable(String userId, Boolean active) throws DataNotFoundException {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException(ResultCode.USER_NOT_FOUND));
        existingUser.setActive(active);
        userRepository.save(existingUser);
    }

    @Override
    public void changeProfileImage(String userId, String imageName) throws Exception {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException(ResultCode.USER_NOT_FOUND));
        existingUser.setProfileImage(imageName);
        userRepository.save(existingUser);
    }

    @Override
    public void forgotPassword(String email) throws DataNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new DataNotFoundException(ResultCode.USER_NOT_FOUND));

        String otp = generateOtp();
        LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(1);

        user.setOtp(passwordEncoder.encode(otp));
        user.setExpiryOtp(expiryTime);
        userRepository.save(user);

        sendOtpToNotificationService(user.getEmail());
    }

    @Override
    public boolean verifyOtpAndResetPassword(String email, String otp, String newPassword) throws DataNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new DataNotFoundException(ResultCode.USER_NOT_FOUND));

        if (passwordEncoder.matches(otp, user.getOtp()) && user.getExpiryOtp().isAfter(LocalDateTime.now())) {
            user.setPassword(passwordEncoder.encode(newPassword));
            user.setOtp(null);  // Clear OTP
            user.setExpiryOtp(null);  // Clear OTP expiry time
            userRepository.save(user);
            return true;
        }

        return false;
    }

    public void uploadAvatar(String userId, MultipartFile file) throws Exception {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException(ResultCode.USER_NOT_FOUND));

        validateFileUpload(file);

        WebClient webClient = webClientBuilder.build();

        try {
            String imageAvatar = webClient.post()
                    .uri("http://localhost:5002/api/v1/internal/files/upload")
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(BodyInserters.fromMultipartData("file", file.getResource()))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            changeProfileImage(existingUser.getUserId(), imageAvatar);
        } catch (Exception e) {
            // Log and rethrow or handle the error properly
            throw new RuntimeException("Failed to upload avatar image: " + e.getMessage(), e);
        }
    }


    private String generateOtp() {
        Random random = new Random();
        int randomNumber = random.nextInt(999999);
        StringBuilder output = new StringBuilder(Integer.toString(randomNumber));

        while (output.length() < 6) {
            output.insert(0, "0");
        }
        return output.toString();
    }

    private void sendOtpToNotificationService(String email) {
        WebClient webClient = webClientBuilder.build();
        String notificationServiceUrl = "http://localhost:5009/api/v1/notification/sendOtp";

        OtpRequest otpRequest = new OtpRequest(email); // chỉ email

        webClient.post()
                .uri(notificationServiceUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(otpRequest))
                .retrieve()
                .bodyToMono(String.class)
                .block(); // chỉ block nếu bạn cần chờ kết quả ngay
    }


    private void validateFileUpload(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Please select a file to upload");
        }

        if (file.getSize() > 10 * 1024 * 1024) {
            throw new IllegalArgumentException("File is too large! Maximum size is 10MB");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("File must be an image");
        }
    }

}
