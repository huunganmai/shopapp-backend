package com.huungan.shopapp.controller;

import com.huungan.shopapp.dtos.UpdateUserDTO;
import com.huungan.shopapp.dtos.UserDTO;
import com.huungan.shopapp.dtos.UserLoginDTO;
import com.huungan.shopapp.models.User;
import com.huungan.shopapp.responses.users.LoginResponse;
import com.huungan.shopapp.responses.users.RegisterResponse;
import com.huungan.shopapp.responses.users.UserResponse;
import com.huungan.shopapp.services.IUserService;
import com.huungan.shopapp.components.LocalizationUtils;
import com.huungan.shopapp.utils.MessageKeys;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.ResourceBundle;

@RestController
@RequestMapping("${api.prefix}/users")
@RequiredArgsConstructor
public class UserController {
    private final IUserService userService;
    private final LocalizationUtils localizationUtils;

    @PostMapping("/register")
    public ResponseEntity<?> createUser(
            @Valid @RequestBody UserDTO userDTO,
            BindingResult result
    ) {
        try {
            if(result.hasErrors()) {
                List<String> errorMessages = result.getFieldErrors()
                        .stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();
                return ResponseEntity.badRequest().body(errorMessages);
            }
            if(!userDTO.getPassword().equals(userDTO.getRetypePassword())) {
                return ResponseEntity.badRequest().body(
                        RegisterResponse.builder()
                                .message(localizationUtils.getLocalizedMessage(MessageKeys.PASSWORD_NOT_MATCH))
                                .build()
                );
            }
            User newUser = userService.createUser(userDTO);
            return ResponseEntity.ok(RegisterResponse.builder()
                            .user(newUser)
                            .message(MessageKeys.REGISTER_SUCCESSFULLY)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody UserLoginDTO userLoginDTO
    ) {
        try {
            String token = userService.login(
                    userLoginDTO.getPhoneNumber(),
                    userLoginDTO.getPassword(),
                    userLoginDTO.getRoleId() == null ? 1 : userLoginDTO.getRoleId()
            );
            return ResponseEntity.ok(LoginResponse.builder()
                            .message(localizationUtils.getLocalizedMessage(MessageKeys.LOGIN_SUCCESSFULLY))
                            .token(token)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    LoginResponse.builder()
                            .message(localizationUtils.getLocalizedMessage(MessageKeys.LOGIN_FAILED, e.getMessage()))
                            .build()
            );
        }
    }

    @PostMapping("/details")
    public ResponseEntity<?> getUserDetails(
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        try {
            String extractToken = authorizationHeader.substring(7);
            User user = userService.getUserDetailsFromToken(extractToken);
            return ResponseEntity.ok(UserResponse.fromUser(user));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/details/{id}")
    public ResponseEntity<?> updateUser(
            @PathVariable("id") long id,
            @RequestBody UpdateUserDTO updatedUserDTO,
            @RequestHeader("Authorization") String authorizationHeader
            ) {
        try {
            String extractToken = authorizationHeader.substring(7);
            User user = userService.getUserDetailsFromToken(extractToken);
            User updatedUser = userService.updateUser(id, updatedUserDTO);
            UserResponse userResponse = UserResponse.fromUser(updatedUser);
            return ResponseEntity.ok(userResponse);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
