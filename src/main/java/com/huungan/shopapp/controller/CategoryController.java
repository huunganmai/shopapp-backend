package com.huungan.shopapp.controller;

import com.huungan.shopapp.dtos.CategoryDTO;
import com.huungan.shopapp.models.Category;
import com.huungan.shopapp.responses.categories.CategoryResponse;
import com.huungan.shopapp.services.CategoryService;
import com.huungan.shopapp.components.LocalizationUtils;
import com.huungan.shopapp.utils.MessageKeys;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;
    private final LocalizationUtils localizationUtils;

    @PostMapping("")
    public ResponseEntity<?> insertCategory(
            @Valid @RequestBody CategoryDTO categoryDTO,
            BindingResult result
    ) {
        if(result.hasErrors()) {
            List<String> errorMessages = result.getFieldErrors()
                    .stream()
                    .map(fieldError -> fieldError.getDefaultMessage())
                    .toList();
            return ResponseEntity.badRequest().body(CategoryResponse.builder()
                            .error(localizationUtils.getLocalizedMessage(MessageKeys.INSERT_CATEGORY_FAILED))
                    .build());
        }
        Category newCategory = categoryService.createCategory(categoryDTO);
        return ResponseEntity.ok(CategoryResponse.builder()
                        .message(localizationUtils.getLocalizedMessage(MessageKeys.INSERT_CATEGORY_SUCCESSFULLY))
                        .category(newCategory)
                .build());
    }


    @GetMapping("")
    public ResponseEntity<List<Category>> getAllCategories(@RequestParam int page, @RequestParam int limit) {
        List<Category> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable long id) {
        Category category = categoryService.getCategoryById(id);
        return ResponseEntity.ok(category);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponse> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody CategoryDTO categoryDTO
    ) {
        categoryService.updateCategory(id, categoryDTO);
        return ResponseEntity.ok(CategoryResponse.builder()
                        .message(localizationUtils.getLocalizedMessage(MessageKeys.UPDATE_CATEGORY_SUCCESSFULLY))
                .build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CategoryResponse> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok(CategoryResponse.builder()
                        .message(MessageKeys.DELETE_CATEGORY_SUCCESSFULLY)
                .build());
    }
}
