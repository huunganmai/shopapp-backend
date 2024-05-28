package com.huungan.shopapp.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.huungan.shopapp.responses.products.ProductListResponse;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public interface IProductRedisService {
    void clear();

    ProductListResponse getAllProducts(
            String keyword,
            Long categoryId,
            PageRequest pageRequest
    ) throws JsonProcessingException;

    void saveAllProducts(
            ProductListResponse productResponses,
            String keyword,
            Long categoryId,
            PageRequest pageRequest
    ) throws JsonProcessingException;
}
