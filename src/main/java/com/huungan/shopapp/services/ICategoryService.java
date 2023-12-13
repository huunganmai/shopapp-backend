package com.huungan.shopapp.services;

import com.huungan.shopapp.dtos.CategoryDTO;
import com.huungan.shopapp.models.Category;

import java.util.List;

public interface ICategoryService {
    Category createCategory(CategoryDTO category);

    Category getCategoryById(long id);

    List<Category> getAllCategories();

    Category updateCategory(long categoryId, CategoryDTO category);

    void deleteCategory(long categoryId);
}
