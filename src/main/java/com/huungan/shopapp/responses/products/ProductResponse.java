package com.huungan.shopapp.responses.products;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.huungan.shopapp.models.Product;
import com.huungan.shopapp.responses.BaseResponse;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductResponse extends BaseResponse {

    private String name;

    private Float price;

    private String thumbnail;

    private String description;

    @JsonProperty("category_id")
    private Long categoryId;

    public static ProductResponse fromProduct(Product product) {
        ProductResponse productResponse = ProductResponse.builder()
                .name(product.getName())
                .price(product.getPrice())
                .thumbnail(product.getThumbnail())
                .description(product.getDescription())
                .categoryId(product.getCategory().getId())
                .build();
        productResponse.setUpdatedAt(product.getUpdatedAt());
        productResponse.setCreatedAt(product.getCreatedAt());
        return productResponse;
    }
}
