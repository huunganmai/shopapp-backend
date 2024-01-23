package com.huungan.shopapp.services;

import com.huungan.shopapp.models.ProductImage;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface IProductImageService {
    ProductImage deleteProductImage(Long id) throws Exception;

    String storeFile(MultipartFile file) throws IOException;
    void deleteFile(String filename) throws IOException;
}
