package com.huungan.shopapp.responses.users;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.huungan.shopapp.models.User;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterResponse {
    @JsonProperty("message")
    private String message;

    @JsonProperty("user")
    private User user;
}
