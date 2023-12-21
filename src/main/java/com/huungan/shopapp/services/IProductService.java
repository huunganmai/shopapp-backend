package com.huungan.shopapp.services;

import com.huungan.shopapp.dtos.ProductDTO;
import com.huungan.shopapp.dtos.ProductImageDTO;
import com.huungan.shopapp.models.Product;
import com.huungan.shopapp.models.ProductImage;
import com.huungan.shopapp.responses.products.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public interface IProductService {
    public Product createProduct(ProductDTO productDTO) throws Exception;

    Product getProductById(long id) throws Exception;

    Page<ProductResponse> getAllProducts(String keyword, Long categoryId,PageRequest pageRequest);

    Product updateProduct(long id, ProductDTO productDTO) throws Exception;

    void deleteProduct(long id);

    boolean existsByName(String name);

    public ProductImage createProductImage(
            Long productId,
            ProductImageDTO productImageDTO
    ) throws Exception;

    public List<Product> findByProductIds(List<Long> produtcIds);
}
