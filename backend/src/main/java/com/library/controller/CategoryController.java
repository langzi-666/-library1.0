package com.library.controller;

import com.library.common.Result;
import com.library.dto.CategoryCreateDTO;
import com.library.dto.CategoryDTO;
import com.library.dto.CategoryUpdateDTO;
import com.library.service.CategoryService;
import com.library.service.OperationLogService;
import com.library.utils.JwtUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 图书分类管理控制器
 */
@Api(tags = "图书分类管理")
@RestController
@RequestMapping("/category")
public class CategoryController {
    
    @Autowired
    private CategoryService categoryService;
    
    @Autowired
    private OperationLogService operationLogService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @ApiOperation("创建分类")
    @PostMapping
    public Result<CategoryDTO> createCategory(@Validated @RequestBody CategoryCreateDTO createDTO, HttpServletRequest request) {
        try {
            CategoryDTO category = categoryService.createCategory(createDTO);
            String userId = getUserIdFromRequest(request);
            operationLogService.saveLog(userId, "创建分类", "创建分类：" + category.getCategoryName(), getIpAddress(request), request.getHeader("User-Agent"), "成功");
            return Result.success("创建成功", category);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @ApiOperation("更新分类")
    @PutMapping("/{categoryId}")
    public Result<CategoryDTO> updateCategory(@PathVariable Integer categoryId, @Validated @RequestBody CategoryUpdateDTO updateDTO, HttpServletRequest request) {
        try {
            CategoryDTO category = categoryService.updateCategory(categoryId, updateDTO);
            String userId = getUserIdFromRequest(request);
            operationLogService.saveLog(userId, "更新分类", "更新分类：" + categoryId, getIpAddress(request), request.getHeader("User-Agent"), "成功");
            return Result.success("更新成功", category);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @ApiOperation("删除分类")
    @DeleteMapping("/{categoryId}")
    public Result<Void> deleteCategory(@PathVariable Integer categoryId, HttpServletRequest request) {
        try {
            categoryService.deleteCategory(categoryId);
            String userId = getUserIdFromRequest(request);
            operationLogService.saveLog(userId, "删除分类", "删除分类：" + categoryId, getIpAddress(request), request.getHeader("User-Agent"), "成功");
            return Result.success("删除成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @ApiOperation("根据ID获取分类")
    @GetMapping("/{categoryId}")
    public Result<CategoryDTO> getCategoryById(@PathVariable Integer categoryId) {
        try {
            CategoryDTO category = categoryService.getCategoryById(categoryId);
            return Result.success(category);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @ApiOperation("获取所有分类（树形结构）")
    @GetMapping("/tree")
    public Result<List<CategoryDTO>> getAllCategoriesTree() {
        try {
            List<CategoryDTO> categories = categoryService.getAllCategoriesTree();
            return Result.success(categories);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @ApiOperation("获取一级分类列表")
    @GetMapping("/root")
    public Result<List<CategoryDTO>> getRootCategories() {
        try {
            List<CategoryDTO> categories = categoryService.getRootCategories();
            return Result.success(categories);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @ApiOperation("根据父分类ID获取子分类列表")
    @GetMapping("/parent/{parentId}")
    public Result<List<CategoryDTO>> getCategoriesByParentId(@PathVariable Integer parentId) {
        try {
            List<CategoryDTO> categories = categoryService.getCategoriesByParentId(parentId);
            return Result.success(categories);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取客户端IP地址
     */
    private String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
    
    /**
     * 从请求中获取用户ID
     */
    private String getUserIdFromRequest(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            try {
                return jwtUtil.getUserIdFromToken(token);
            } catch (Exception e) {
                return "system";
            }
        }
        return "system";
    }
}

