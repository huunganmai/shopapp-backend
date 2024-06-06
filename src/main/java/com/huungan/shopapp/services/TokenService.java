package com.huungan.shopapp.services;

import com.huungan.shopapp.components.JwtTokenUtils;
import com.huungan.shopapp.exceptions.DataNotFoundException;
import com.huungan.shopapp.exceptions.ExpiredTokenException;
import com.huungan.shopapp.models.Token;
import com.huungan.shopapp.models.User;
import com.huungan.shopapp.repositories.TokenRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TokenService implements ITokenService {
    private static final int MAX_TOKENS = 3;
    @Value("${jwt.expiration}")
    private int expiration;
    @Value("${jwt.expiration-refresh-token}")
    private int expirationRefreshToken;

    private final TokenRepository tokenRepository;
    private final JwtTokenUtils jwtTokenUtils;

    @Override
    @Transactional
    public Token addToken(User user, String token, boolean isMobile) {
        List<Token> userTokens = tokenRepository.findByUser(user);
        int tokenCount = userTokens.size();
        if(tokenCount >= MAX_TOKENS) {
            boolean hasNonMobileToken = !userTokens.stream().allMatch(Token::isMobile);
            Token tokenDelete;
            if (hasNonMobileToken) {
                tokenDelete = userTokens.stream()
                        .filter(userToken -> !userToken.isMobile())
                        .findFirst()
                        .orElse(userTokens.get(0));
            } else {
                tokenDelete = userTokens.get(0);
            }
            tokenRepository.delete(tokenDelete);
        }
        LocalDateTime currentDateTime = LocalDateTime.now();
        Token newToken = Token.builder()
                .user(user)
                .token(token)
                .tokenType("Bearer")
                .expirationDate(currentDateTime.plusSeconds(expiration))
                .expired(false)
                .revoked(false)
                .isMobile(isMobile)
                .build();
        newToken.setRefreshToken(UUID.randomUUID().toString());
        newToken.setRefreshExpirationDate(currentDateTime.plusSeconds(expirationRefreshToken));
        tokenRepository.save(newToken);
        return newToken;
    }

    @Override
    @Transactional
    public Token refreshToken(User user, String refreshToken) throws Exception {
        Token existingToken = tokenRepository.findByRefreshToken(refreshToken);
        if(existingToken == null) {
            throw new DataNotFoundException("Refresh token does not exist");
        }
        if(existingToken.getRefreshExpirationDate().isBefore(LocalDateTime.now())){
            throw new ExpiredTokenException("Refresh token is expired");
        }
        String token = jwtTokenUtils.generateToken(user);
        LocalDateTime currentDateTime = LocalDateTime.now();
        existingToken.setExpirationDate(currentDateTime.plusSeconds(expiration));
        existingToken.setRefreshExpirationDate(currentDateTime.plusSeconds(expirationRefreshToken));
        existingToken.setToken(token);
        existingToken.setRefreshToken(UUID.randomUUID().toString());
//        tokenRepository.save(existingToken);
        return existingToken;
    }
}
