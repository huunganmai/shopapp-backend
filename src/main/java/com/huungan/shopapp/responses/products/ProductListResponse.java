package com.huungan.shopapp.responses.products;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ProductListResponse {
    private List<ProductResponse> products;
    private int totalPages;
}
