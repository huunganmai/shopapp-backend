package com.huungan.shopapp.services;

import com.huungan.shopapp.exceptions.DataNotFoundException;
import com.huungan.shopapp.models.Token;
import com.huungan.shopapp.models.User;

public interface ITokenService {
    Token addToken(User user, String token, boolean isMobile);
    Token refreshToken(User user, String refreshToken) throws Exception;
}
