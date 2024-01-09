package com.huungan.shopapp.responses.orders;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class OrderListResponse {
    private List<OrderResponse> orderResponses;
    private int totalPages;
}
