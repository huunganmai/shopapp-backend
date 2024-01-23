package com.huungan.shopapp.services;

import com.huungan.shopapp.exceptions.DataNotFoundException;
import com.huungan.shopapp.models.ProductImage;
import com.huungan.shopapp.repositories.ProductImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductImageService implements IProductImageService{
    private final ProductImageRepository productImageRepository;
    private static final String UPLOADS_FOLDER = "uploads";

    @Override
    @Transactional
    public ProductImage deleteProductImage(Long id) throws Exception {
        Optional<ProductImage> productImage = productImageRepository.findById(id);
        if(productImage.isEmpty()) {
            throw new DataNotFoundException(String.format("Cannot find product image with id: %ld", id));
        }
        productImageRepository.deleteById(id);
        return productImage.get();
    }

    @Override
    public String storeFile(MultipartFile file) throws IOException {
        String filename = "";
        if(file.getOriginalFilename() != null) {
            filename = StringUtils.cleanPath(file.getOriginalFilename());
        }
        if(filename.isEmpty()) {
            throw new IllegalArgumentException("Invalid filename or filename is empty");
        }
        String uniqueFilename = UUID.randomUUID().toString() + "_" + filename;
        Path uploadDir = Paths.get(UPLOADS_FOLDER);
        if(!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }

        Path destination = Paths.get(uploadDir.toString(), uniqueFilename);
        Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
        return uniqueFilename;
    }

    @Override
    public void deleteFile(String filename) throws IOException {
        Path uploadDir = Paths.get(UPLOADS_FOLDER);
        Path filePath = uploadDir.resolve(filename);
        if(Files.exists(filePath)) {
            Files.delete(filePath);
        } else {
            throw new FileNotFoundException("File not found: " + filename);
        }
    }
}
