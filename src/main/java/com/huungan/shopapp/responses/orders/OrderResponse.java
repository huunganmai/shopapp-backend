package com.huungan.shopapp.responses.orders;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.huungan.shopapp.responses.BaseResponse;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class OrderResponse extends BaseResponse {
    private Long id;

    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("fullname")
    private String fullName;

    private String email;

    @JsonProperty("phone_number")
    private String phoneNumber;

    private String address;

    private String note;

    @JsonProperty("total_money")
    private float totalMoney;

    @JsonProperty("order_date")
    private Data orderDate;

    private String status;
}
