package com.library.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.library.dto.CategoryCreateDTO;
import com.library.dto.CategoryDTO;
import com.library.dto.CategoryUpdateDTO;
import com.library.entity.Book;
import com.library.entity.Category;
import com.library.mapper.BookMapper;
import com.library.mapper.CategoryMapper;
import com.library.service.CategoryService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 图书分类服务实现类
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    
    @Autowired
    private CategoryMapper categoryMapper;
    
    @Autowired
    private BookMapper bookMapper;
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public CategoryDTO createCategory(CategoryCreateDTO createDTO) {
        // 验证父分类是否存在
        if (createDTO.getParentId() != null && createDTO.getParentId() > 0) {
            Category parent = categoryMapper.selectById(createDTO.getParentId());
            if (parent == null) {
                throw new RuntimeException("父分类不存在");
            }
            // 验证级别不能超过3级
            if (parent.getLevel() >= 3) {
                throw new RuntimeException("分类级别不能超过3级");
            }
            // 子分类级别应该是父分类级别+1
            if (createDTO.getLevel() != null && createDTO.getLevel() != parent.getLevel() + 1) {
                throw new RuntimeException("分类级别不正确");
            }
        }
        
        Category category = new Category();
        BeanUtils.copyProperties(createDTO, category);
        if (category.getSortOrder() == null) {
            category.setSortOrder(0);
        }
        if (category.getParentId() == null) {
            category.setParentId(0);
        }
        if (category.getLevel() == null) {
            category.setLevel(1);
        }
        
        categoryMapper.insert(category);
        return convertToDTO(category);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public CategoryDTO updateCategory(Integer categoryId, CategoryUpdateDTO updateDTO) {
        Category category = categoryMapper.selectById(categoryId);
        if (category == null) {
            throw new RuntimeException("分类不存在");
        }
        
        // 验证父分类
        if (updateDTO.getParentId() != null && updateDTO.getParentId() > 0) {
            if (updateDTO.getParentId().equals(categoryId)) {
                throw new RuntimeException("不能将分类设置为自己父分类");
            }
            Category parent = categoryMapper.selectById(updateDTO.getParentId());
            if (parent == null) {
                throw new RuntimeException("父分类不存在");
            }
            if (parent.getLevel() >= 3) {
                throw new RuntimeException("分类级别不能超过3级");
            }
        }
        
        // 更新字段
        if (updateDTO.getCategoryName() != null) {
            category.setCategoryName(updateDTO.getCategoryName());
        }
        if (updateDTO.getParentId() != null) {
            category.setParentId(updateDTO.getParentId());
        }
        if (updateDTO.getLevel() != null) {
            category.setLevel(updateDTO.getLevel());
        }
        if (updateDTO.getDescription() != null) {
            category.setDescription(updateDTO.getDescription());
        }
        if (updateDTO.getSortOrder() != null) {
            category.setSortOrder(updateDTO.getSortOrder());
        }
        
        categoryMapper.updateById(category);
        return convertToDTO(category);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCategory(Integer categoryId) {
        Category category = categoryMapper.selectById(categoryId);
        if (category == null) {
            throw new RuntimeException("分类不存在");
        }
        
        // 检查是否有子分类
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Category::getParentId, categoryId);
        Long count = categoryMapper.selectCount(wrapper);
        if (count > 0) {
            throw new RuntimeException("该分类下存在子分类，无法删除");
        }
        
        // 检查是否有图书使用该分类
        LambdaQueryWrapper<Book> bookWrapper = new LambdaQueryWrapper<>();
        bookWrapper.eq(Book::getCategoryId, categoryId);
        Long bookCount = bookMapper.selectCount(bookWrapper);
        if (bookCount > 0) {
            throw new RuntimeException("该分类下存在图书，无法删除");
        }
        
        categoryMapper.deleteById(categoryId);
    }
    
    @Override
    public CategoryDTO getCategoryById(Integer categoryId) {
        Category category = categoryMapper.selectById(categoryId);
        if (category == null) {
            throw new RuntimeException("分类不存在");
        }
        return convertToDTO(category);
    }
    
    @Override
    public List<CategoryDTO> getAllCategoriesTree() {
        List<Category> allCategories = categoryMapper.selectList(null);
        List<CategoryDTO> rootCategories = allCategories.stream()
                .filter(c -> c.getParentId() == null || c.getParentId() == 0)
                .map(this::convertToDTO)
                .sorted((a, b) -> Integer.compare(a.getSortOrder(), b.getSortOrder()))
                .collect(Collectors.toList());
        
        // 递归构建树形结构
        rootCategories.forEach(root -> buildTree(root, allCategories));
        
        return rootCategories;
    }
    
    @Override
    public List<CategoryDTO> getRootCategories() {
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Category::getParentId, 0);
        wrapper.orderByAsc(Category::getSortOrder);
        List<Category> categories = categoryMapper.selectList(wrapper);
        return categories.stream().map(this::convertToDTO).collect(Collectors.toList());
    }
    
    @Override
    public List<CategoryDTO> getCategoriesByParentId(Integer parentId) {
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Category::getParentId, parentId);
        wrapper.orderByAsc(Category::getSortOrder);
        List<Category> categories = categoryMapper.selectList(wrapper);
        return categories.stream().map(this::convertToDTO).collect(Collectors.toList());
    }
    
    /**
     * 构建树形结构
     */
    private void buildTree(CategoryDTO parent, List<Category> allCategories) {
        List<CategoryDTO> children = allCategories.stream()
                .filter(c -> c.getParentId() != null && c.getParentId().equals(parent.getCategoryId()))
                .map(this::convertToDTO)
                .sorted((a, b) -> Integer.compare(a.getSortOrder(), b.getSortOrder()))
                .collect(Collectors.toList());
        
        parent.setChildren(children);
        children.forEach(child -> buildTree(child, allCategories));
    }
    
    /**
     * 转换为DTO
     */
    private CategoryDTO convertToDTO(Category category) {
        CategoryDTO dto = new CategoryDTO();
        BeanUtils.copyProperties(category, dto);
        return dto;
    }
}

