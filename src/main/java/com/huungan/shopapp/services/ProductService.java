package com.huungan.shopapp.services;

import com.huungan.shopapp.components.LocalizationUtils;
import com.huungan.shopapp.dtos.ProductDTO;
import com.huungan.shopapp.dtos.ProductImageDTO;
import com.huungan.shopapp.exceptions.DataNotFoundException;
import com.huungan.shopapp.exceptions.InvalidParamException;
import com.huungan.shopapp.models.Category;
import com.huungan.shopapp.models.Product;
import com.huungan.shopapp.models.ProductImage;
import com.huungan.shopapp.repositories.CategoryRepository;
import com.huungan.shopapp.repositories.ProductImageRepository;
import com.huungan.shopapp.repositories.ProductRepository;
import com.huungan.shopapp.responses.products.ProductResponse;
import com.huungan.shopapp.utils.MessageKeys;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService implements IProductService{
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductImageRepository productImageRepository;
    private final LocalizationUtils localizationUtils;

    @Override
    @Transactional
    public Product createProduct(ProductDTO productDTO) throws DataNotFoundException {
        Category existingCategory = categoryRepository.findById(productDTO.getCategoryId())
                .orElseThrow(() ->
                        new DataNotFoundException("Cannot find category with id = " + productDTO.getCategoryId()));
        Product newProduct = Product.builder()
                .name(productDTO.getName())
                .price(productDTO.getPrice())
                .thumbnail(productDTO.getThumbnail())
                .description(productDTO.getDescription())
                .category(existingCategory)
                .build();
        return productRepository.save(newProduct);
    }

    @Override
    public Product getProductById(long productId) throws DataNotFoundException {
        Optional<Product> optionalProduct = productRepository.getDetailProduct(productId);
        if(optionalProduct.isPresent()) {
            return optionalProduct.get();
        }
        throw new DataNotFoundException("Cannot find product with id =" + productId);
//        return productRepository.getDetailProduct(productId)
//                .orElseThrow(() -> new DataNotFoundException("Cannot find product with id = " + productId));
    }

    @Override
    public Page<ProductResponse> getAllProducts(String keyword, Long categoryId,PageRequest pageRequest) {

        return productRepository
                .searchProducts( keyword, categoryId, pageRequest)
                .map(ProductResponse::fromProduct);
    }

    @Override
    @Transactional
    public Product updateProduct(long productId, ProductDTO productDTO) throws Exception {
        Product existingProduct = productRepository.findById(productId)
                .orElseThrow(() -> new DataNotFoundException(
                        "Cannot find product with id = " + productId));
        Category existingCategory = categoryRepository.findById(productDTO.getCategoryId())
                .orElseThrow(() -> new DataNotFoundException(
                        "Cannot find category with id = " +
                        productDTO.getCategoryId()));
        existingProduct.setCategory(existingCategory);
        if(productDTO.getName() != null && !productDTO.getName().isEmpty()) {
            existingProduct.setName(productDTO.getName());
        }
        if(productDTO.getPrice() > 0) {
            existingProduct.setPrice(productDTO.getPrice());
        }
        if(productDTO.getDescription() != null && !productDTO.getDescription().isEmpty()) {
            existingProduct.setDescription(productDTO.getDescription());
        }
        if(productDTO.getThumbnail() != null && !productDTO.getThumbnail().isEmpty()) {
            existingProduct.setThumbnail(productDTO.getThumbnail());
        }

        return productRepository.save(existingProduct);
    }

    @Override
    @Transactional
    public void deleteProduct(long id) {
        Optional<Product> existingProduct = productRepository.findById(id);
        if(existingProduct.isPresent()) {
            productRepository.deleteById(id);
        }
    }

    @Override
    public boolean existsByName(String name) {
        return productRepository.existsByName(name);
    }

    @Override
    public ProductImage createProductImage(
            Long productId,
            ProductImageDTO productImageDTO
    ) throws Exception {
        Product existingProduct = productRepository.findById(productId)
                .orElseThrow(() -> new DataNotFoundException("Cannot find product with id = " + productId));
        ProductImage newProductImage = ProductImage.builder()
                .product(existingProduct)
                .imageUrl(productImageDTO.getImageUrl())
                .build();
        int size = productImageRepository.findByProductId(productId).size();
        if(size >= ProductImage.MAXIMUM_IMAGES_PER_PRODUCT) {
            throw new InvalidParamException(
                    "Number of image must be <= " +
                    ProductImage.MAXIMUM_IMAGES_PER_PRODUCT);
        }
        return productImageRepository.save(newProductImage);
    }

    @Override
    public List<Product> findByProductIds(List<Long> produtcIds) {
        return productRepository.findByProductIds(produtcIds);
    }

    @Override
    @Transactional
    public void addThumbnailToProduct(long id, String filename) throws Exception {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(
                        localizationUtils.getLocalizedMessage(MessageKeys.FIND_PRODUCT_BY_ID_FAILED
                )));
        existingProduct.setThumbnail(filename);
        productRepository.save(existingProduct);
    }


    private boolean isImageFile(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && contentType.startsWith("image/");
    }
}
