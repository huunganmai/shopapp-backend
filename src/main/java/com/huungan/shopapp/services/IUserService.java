package com.huungan.shopapp.services;

import com.huungan.shopapp.dtos.UserDTO;
import com.huungan.shopapp.models.User;

public interface IUserService {
    User createUser(UserDTO userDTO) throws Exception;

    String login(String phoneNumber, String password) throws Exception;
}
