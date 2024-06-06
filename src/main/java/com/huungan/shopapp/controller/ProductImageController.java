package com.huungan.shopapp.controller;

import com.huungan.shopapp.components.LocalizationUtils;
import com.huungan.shopapp.dtos.ProductImageDTO;
import com.huungan.shopapp.models.Product;
import com.huungan.shopapp.models.ProductImage;
import com.huungan.shopapp.responses.ResponseObject;
import com.huungan.shopapp.services.IProductImageService;
import com.huungan.shopapp.services.ProductService;
import com.huungan.shopapp.utils.MessageKeys;
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
@RequestMapping("${api.prefix}/product-images")
public class ProductImageController {
    private final IProductImageService productImageService;
    private final ProductService productService;
    private final LocalizationUtils localizationUtils;

    @PostMapping(value = "/uploads/{id}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseObject> uploadImages(
            @PathVariable("id") Long productId,
            @ModelAttribute("files") List<MultipartFile> files
    ) throws Exception {
        Product existingProduct = productService.getProductById(productId);
        files = files == null ? new ArrayList<MultipartFile>() : files;
        if(files.size() > ProductImage.MAXIMUM_IMAGES_PER_PRODUCT) {
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .message(localizationUtils.getLocalizedMessage(MessageKeys.UPLOAD_IMAGES_MAX_5))
                    .build());
        }
        List<ProductImage> productImages = new ArrayList<>();
        for(MultipartFile file : files) {
            if(file.getSize() == 0) {
                continue;
            }
            if(file.getSize() > 10 * 1024 * 1024) {
                return ResponseEntity.badRequest().body(ResponseObject.builder()
                        .status(HttpStatus.PAYLOAD_TOO_LARGE)
                        .message(localizationUtils.getLocalizedMessage(MessageKeys.UPLOAD_IMAGES_FILE_LARGE))
                        .build());
            }
            String contentType = file.getContentType();
            if(contentType == null || !contentType.startsWith("image/")) {
                return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(ResponseObject.builder()
                        .status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                        .message(localizationUtils.getLocalizedMessage(MessageKeys.UPLOAD_IMAGES_FILE_MUST_BE_IMAGE))
                        .build());
            }

            // Save file and update thumbnail
            String filename = productImageService.storeFile(file);

            // Save image URL in Database
            ProductImage productImage = productService.createProductImage(
                    existingProduct.getId(),
                    ProductImageDTO.builder()
                            .imageUrl(filename)
                            .build()
            );
            productImages.add(productImage);
        }
        productService.addThumbnailToProduct(existingProduct.getId(), productImages.get(0).getImageUrl());
        return ResponseEntity.ok().body(ResponseObject.builder()
                .status(HttpStatus.OK)
                .data(productImages)
                .message(localizationUtils.getLocalizedMessage(MessageKeys.UPLOAD_IMAGES_SUCCESSFULLY))
                .build());
    }

    @GetMapping("/{imageName}")
    public ResponseEntity<?> viewImage(@PathVariable String imageName) throws Exception {
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
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ResponseObject> delete(@PathVariable Long id) throws Exception {
        ProductImage productImage = productImageService.deleteProductImage(id);
        if(productImage != null) {
            productImageService.deleteFile(productImage.getImageUrl());
        }
        return ResponseEntity.ok().body(ResponseObject.builder()
                .status(HttpStatus.OK)
                .data(productImage)
                .message(localizationUtils.getLocalizedMessage(MessageKeys.DELETE_IMAGES_SUCCESSFULLY))
                .build());
    }
}
