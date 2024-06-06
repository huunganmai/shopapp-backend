package com.huungan.shopapp.services;

import com.huungan.shopapp.dtos.UpdateUserDTO;
import com.huungan.shopapp.dtos.UserDTO;
import com.huungan.shopapp.models.User;

public interface IUserService {
    User createUser(UserDTO userDTO) throws Exception;

    String login(String phoneNumber, String password, Long roleId) throws Exception;

    User getUserDetailsFromToken(String token) throws Exception;
    User getUserDetailsFromRefreshToken(String refreshToken) throws Exception;

    User updateUser(long id ,UpdateUserDTO updateUserDTO) throws Exception;
}
