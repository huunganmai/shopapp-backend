package com.huungan.shopapp.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.huungan.shopapp.components.LocalizationUtils;
import com.huungan.shopapp.dtos.ProductDTO;
import com.huungan.shopapp.models.Product;
import com.huungan.shopapp.responses.ResponseObject;
import com.huungan.shopapp.responses.products.ProductListResponse;
import com.huungan.shopapp.responses.products.ProductResponse;
import com.huungan.shopapp.services.IProductRedisService;
import com.huungan.shopapp.services.IProductService;
import com.huungan.shopapp.utils.MessageKeys;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import com.github.javafaker.Faker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("${api.prefix}/products")
@RequiredArgsConstructor
public class ProductController {
    private final IProductService productService;
    private final IProductRedisService productRedisService;
    private final LocalizationUtils localizationUtils;
    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    @PostMapping("")
    public ResponseEntity<ResponseObject> createProduct(
            @Valid @RequestBody ProductDTO productDTO,
            BindingResult result
    ) throws Exception {
        if(result.hasErrors()) {
            List<String> errorMessages = result.getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .toList();
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .message(String.join("; ", errorMessages))
                    .build());
        }
        Product newProduct = productService.createProduct(productDTO);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .status(HttpStatus.OK)
                .data(newProduct)
                .message(localizationUtils.getLocalizedMessage(MessageKeys.CREATE_PRODUCT_SUCCESSFULLY))
                .build());
    }




    @GetMapping("")
    public ResponseEntity<ResponseObject> getProducts(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "0", name = "category_id") Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit
    ) throws JsonProcessingException {
        int totalPages = 0;
        PageRequest pageRequest = PageRequest.of(
                page, limit,
                Sort.by("id").ascending()
        );
        logger.info(String.format("keyword = %s, category_id = %d, page = %d, limit = %d",
                keyword, categoryId, page, limit));
        ProductListResponse productListResponse = productRedisService
                .getAllProducts(keyword, categoryId, pageRequest);
        if(productListResponse == null) {
            Page<ProductResponse> productPage = productService.getAllProducts(keyword, categoryId,pageRequest);
            totalPages = productPage.getTotalPages();
            productListResponse = ProductListResponse.builder()
                    .products(productPage.getContent())
                    .totalPages(productPage.getTotalPages())
                    .build();
            productRedisService.saveAllProducts(
                    productListResponse,
                    keyword,
                    categoryId,
                    pageRequest
            );
        }
//        ProductListResponse productListResponse = ProductListResponse.builder()
//                .products(productResponses)
//                .totalPages(totalPages)
//                .build();
        return ResponseEntity.ok().body(ResponseObject.builder()
                .status(HttpStatus.OK)
                .data(productListResponse)
                .message(localizationUtils.getLocalizedMessage(MessageKeys.FIND_PRODUCT_BY_KEYWORD_SUCCESSFULLY, keyword))
                .build());
    }

    @GetMapping("/by-ids")
    public ResponseEntity<ResponseObject> getProductByIds(@RequestParam("ids") String ids) {
        List<Long> productIds = Arrays.stream(ids.split(","))
                .map(Long::parseLong)
                .toList();
        List<Product> products = productService.findByProductIds(productIds);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .status(HttpStatus.OK)
                .data(products)
                .message(localizationUtils.getLocalizedMessage(MessageKeys.FIND_PRODUCT_BY_IDS_SUCCESSFULLY))
                .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable("id") long id) throws Exception{
        Product existingProduct = productService.getProductById(id);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .status(HttpStatus.OK)
                .data(existingProduct)
                .message(localizationUtils.getLocalizedMessage(MessageKeys.FIND_PRODUCT_BY_ID_SUCCESSFULLY))
                .build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseObject> deleteProduct(@PathVariable long id) {
        productService.deleteProduct((id));
        return ResponseEntity.ok().body(ResponseObject.builder()
                .status(HttpStatus.OK)
                .message(localizationUtils.getLocalizedMessage(MessageKeys.DELETE_PRODUCT_BY_ID_SUCCESSFULLY))
                .build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ResponseObject> updateProduct(
            @PathVariable Long id,
            @RequestBody ProductDTO productDTO
    ) throws Exception{
        Product updateProduct = productService.updateProduct(id, productDTO);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .status(HttpStatus.OK)
                .data(productDTO)
                .message(localizationUtils.getLocalizedMessage(MessageKeys.UPDATE_PRODUCT_BY_ID_SUCCESSFULLY))
                .build());
    }

//    @PostMapping("generate-fake-products")
    private ResponseEntity<String> generateFakeProducts() {
        Faker faker = new Faker();
        for(int i = 0; i < 1000000; i++) {
            String productName = faker.commerce().productName();
            if(productService.existsByName(productName)) {
                continue;
            }
            ProductDTO productDTO = ProductDTO.builder()
                    .name(productName)
                    .price((float)faker.number().numberBetween(10, 90000000))
                    .description(faker.lorem().sentence())
                    .thumbnail("")
                    .categoryId((long)faker.number().numberBetween(2, 6))
                    .build();
            try {
                productService.createProduct(productDTO);
            } catch (Exception e) {
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        }
        return ResponseEntity.ok("Fake products successfully");
    }

}
