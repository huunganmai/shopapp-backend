package com.huungan.shopapp.controller;

import com.huungan.shopapp.dtos.ProductImageDTO;
import com.huungan.shopapp.models.Product;
import com.huungan.shopapp.models.ProductImage;
import com.huungan.shopapp.services.IProductImageService;
import com.huungan.shopapp.services.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/product_images")
public class ProductImageController {
    private final IProductImageService productImageService;
    private final ProductService productService;


    @PostMapping(value = "/uploads/{id}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadImages(
            @PathVariable("id") Long productId,
            @ModelAttribute("files") List<MultipartFile> files
    ) {
        try {
            Product exitstingProduct = productService.getProductById(productId);
            files = files == null ? new ArrayList<MultipartFile>() : files;
            if(files.size() > ProductImage.MAXIMUM_IMAGES_PER_PRODUCT) {
                return ResponseEntity.badRequest().body("You can only upload maximum 5 images");
            }
            List<ProductImage> productImages = new ArrayList<>();
            for(MultipartFile file : files) {
                if(file.getSize() == 0) {
                    continue;
                }
                if(file.getSize() > 10 * 1024 *1024) {
                    return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                            .body("File is to larger. Maximum size is 10MB");
                }

                // Save file and update thumbnail
                String filename = productImageService.storeFile(file);
                // Save imageUrl in DB
                ProductImage productImage = productService.createProductImage(
                        exitstingProduct.getId(),
                        ProductImageDTO.builder()
                                .imageUrl(filename)
                                .build()
                );
                productImages.add(productImage);
            }
            productService.addThumbnailToProduct(exitstingProduct.getId(), productImages.get(0).getImageUrl());
            return ResponseEntity.ok(productImages);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{imageName}")
    public ResponseEntity<?> viewImage(@PathVariable String imageName) {
        try{
            java.nio.file.Path imagePath = Paths.get("uploads/" + imageName);
            UrlResource resource = new UrlResource(imagePath.toUri());

            if(resource.exists()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(resource);
            } else {
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(new UrlResource(Paths.get("uploads/notfound.jpeg").toUri()));
            }
        } catch(Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            ProductImage productImage = productImageService.deleteProductImage(id);
            if(productImage != null) {
                productImageService.deleteFile(productImage.getImageUrl());
            }
            return ResponseEntity.ok(productImage);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.toString());
        }
    }
}
