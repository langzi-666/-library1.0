package com.library.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.library.dto.CategoryCreateDTO;
import com.library.dto.CategoryDTO;
import com.library.dto.CategoryUpdateDTO;
import com.library.entity.Category;

import java.util.List;

/**
 * 图书分类服务接口
 */
public interface CategoryService extends IService<Category> {
    
    /**
     * 创建分类
     */
    CategoryDTO createCategory(CategoryCreateDTO createDTO);
    
    /**
     * 更新分类
     */
    CategoryDTO updateCategory(Integer categoryId, CategoryUpdateDTO updateDTO);
    
    /**
     * 删除分类
     */
    void deleteCategory(Integer categoryId);
    
    /**
     * 根据ID获取分类
     */
    CategoryDTO getCategoryById(Integer categoryId);
    
    /**
     * 获取所有分类（树形结构）
     */
    List<CategoryDTO> getAllCategoriesTree();
    
    /**
     * 获取一级分类列表
     */
    List<CategoryDTO> getRootCategories();
    
    /**
     * 根据父分类ID获取子分类列表
     */
    List<CategoryDTO> getCategoriesByParentId(Integer parentId);
}

