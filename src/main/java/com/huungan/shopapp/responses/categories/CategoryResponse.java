package com.huungan.shopapp.responses.categories;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.huungan.shopapp.models.Category;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryResponse {
    @JsonProperty("category")
    private Category category;
}
