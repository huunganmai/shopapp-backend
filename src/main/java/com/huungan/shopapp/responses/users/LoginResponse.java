package com.huungan.shopapp.responses.users;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse {
    @JsonProperty("message")
    private String message;

    @JsonProperty("token")
    private String token;

    @JsonProperty("refresh_token")
    private String refreshToken;
    @JsonProperty("token_types")
    private String tokenType = "Bearer";

    @JsonProperty("user_id")
    private Long userId;
    private String username;

    private List<String> roles;
}
