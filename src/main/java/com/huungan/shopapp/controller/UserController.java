package com.huungan.shopapp.controller;

import com.huungan.shopapp.dtos.RefreshTokenDTO;
import com.huungan.shopapp.dtos.UpdateUserDTO;
import com.huungan.shopapp.dtos.UserDTO;
import com.huungan.shopapp.dtos.UserLoginDTO;
import com.huungan.shopapp.models.Token;
import com.huungan.shopapp.models.User;
import com.huungan.shopapp.responses.ResponseObject;
import com.huungan.shopapp.responses.users.LoginResponse;
import com.huungan.shopapp.responses.users.RegisterResponse;
import com.huungan.shopapp.responses.users.UserResponse;
import com.huungan.shopapp.services.ITokenService;
import com.huungan.shopapp.services.IUserService;
import com.huungan.shopapp.components.LocalizationUtils;
import com.huungan.shopapp.utils.MessageKeys;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
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
    private final ITokenService tokenService;

    @PostMapping("/register")
    public ResponseEntity<ResponseObject> createUser(
            @Valid @RequestBody UserDTO userDTO,
            BindingResult result
    ) throws Exception {
        if(result.hasErrors()) {
            List<String> errorMessages = result.getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .toList();
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .data(errorMessages)
                    .build());
        }
        if(!userDTO.getPassword().equals(userDTO.getRetypePassword())) {
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .message(localizationUtils.getLocalizedMessage(MessageKeys.PASSWORD_NOT_MATCH))
                    .build());
        }
        User newUser = userService.createUser(userDTO);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .status(HttpStatus.CREATED)
                .data(UserResponse.fromUser(newUser))
                .message(localizationUtils.getLocalizedMessage(MessageKeys.REGISTER_SUCCESSFULLY))
                .build());
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseObject> login(
            @Valid @RequestBody UserLoginDTO userLoginDTO,
            HttpServletRequest request
    ) throws Exception {
        String token = userService.login(
                userLoginDTO.getPhoneNumber(),
                userLoginDTO.getPassword(),
                userLoginDTO.getRoleId() == null ? 1 : userLoginDTO.getRoleId()
        );
        String userAgent = request.getHeader("User-Agent");
        User userDetail = userService.getUserDetailsFromToken(token);
        Token jwtToken = tokenService.addToken(userDetail, token, isMobileDevice(userAgent));

        LoginResponse loginResponse = LoginResponse.builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.LOGIN_SUCCESSFULLY))
                .token(jwtToken.getToken())
                .tokenType(jwtToken.getTokenType())
                .refreshToken(jwtToken.getRefreshToken())
                .userId(userDetail.getId())
                .username(userDetail.getUsername())
                .roles(userDetail.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
                .build();

        return ResponseEntity.ok().body(ResponseObject.builder()
                .status(HttpStatus.OK)
                .data(loginResponse)
                .message(localizationUtils.getLocalizedMessage(MessageKeys.LOGIN_SUCCESSFULLY))
                .build());
    }

    private boolean isMobileDevice(String agent) {
        return agent.toLowerCase().contains("mobile");
    }

    @PostMapping("refresh-token")
    public ResponseEntity<ResponseObject> refreshToken(
            @Valid @RequestBody RefreshTokenDTO refreshTokenDTO
    ) throws Exception {
        User userDetail = userService.getUserDetailsFromRefreshToken(refreshTokenDTO.getRefreshToken());
        Token jwtToken = tokenService.refreshToken(userDetail, refreshTokenDTO.getRefreshToken());
        LoginResponse loginResponse = LoginResponse.builder()
                .message("Refresh token successfully")
                .token(jwtToken.getToken())
                .refreshToken(jwtToken.getRefreshToken())
                .tokenType(jwtToken.getTokenType())
                .username(userDetail.getUsername())
                .roles(userDetail.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
                .userId(userDetail.getId())
                .build();
        return ResponseEntity.ok().body(
                ResponseObject.builder()
                        .message("Refresh token successfully")
                        .status(HttpStatus.OK)
                        .data(loginResponse)
                        .build()
        );
    }

    @PostMapping("/details")
    public ResponseEntity<ResponseObject> getUserDetails(
            @RequestHeader("Authorization") String authorizationHeader
    ) throws Exception {
        String extractToken = authorizationHeader.substring(7);
        User user = userService.getUserDetailsFromToken(extractToken);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .status(HttpStatus.OK)
                .data(UserResponse.fromUser(user))
                .build());
    }

    @PutMapping("/details/{id}")
    public ResponseEntity<ResponseObject> updateUser(
            @PathVariable("id") long id,
            @RequestBody UpdateUserDTO updatedUserDTO,
            @RequestHeader("Authorization") String authorizationHeader
            ) throws Exception {
        String extractToken = authorizationHeader.substring(7);
        User user = userService.getUserDetailsFromToken(extractToken);
        if(user.getId() != id) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ResponseObject.builder()
                    .status(HttpStatus.FORBIDDEN)
                    .build());
        }
        User updatedUser = userService.updateUser(id, updatedUserDTO);
        UserResponse userResponse = UserResponse.fromUser(updatedUser);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .status(HttpStatus.OK)
                .data(userResponse)
                .build());
    }
}
