package com.huungan.shopapp.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.huungan.shopapp.responses.products.ProductListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductRedisService implements IProductRedisService{
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper redisObjectMapper;


    private String getKeyFrom(
            String keyword,
            Long categoryId,
            PageRequest pageRequest
    ) {
        int pageNumber = pageRequest.getPageNumber();
        int pageSize = pageRequest.getPageSize();
        Sort sort = pageRequest.getSort();
        String sortDirection = sort.getOrderFor("id")
                .getDirection() == Sort.Direction.ASC ? "asc" : "desc";
        String key = String.format("%s:%d:%d:%s", keyword, pageNumber, pageSize, sortDirection);
        return key;
    }

    @Override
    public void clear() {
        redisTemplate.getConnectionFactory().getConnection().flushAll();
    }

    @Override
    public ProductListResponse getAllProducts(String keyword, Long categoryId, PageRequest pageRequest)
            throws JsonProcessingException {
        String key = this.getKeyFrom(keyword, categoryId, pageRequest);
        String json = (String) redisTemplate.opsForValue().get(key);
        ProductListResponse productListResponse =
                json != null ?
                        redisObjectMapper.readValue(json, new TypeReference<ProductListResponse>() {})
                        : null;
        return productListResponse;
    }

    @Override
    public void saveAllProducts(ProductListResponse productListResponses,
                                String keyword,
                                Long categoryId,
                                PageRequest pageRequest)
            throws JsonProcessingException {
        String key = this.getKeyFrom(keyword, categoryId, pageRequest);
        String json = redisObjectMapper.writeValueAsString(productListResponses);
        redisTemplate.opsForValue().set(key, json);
    }
}
