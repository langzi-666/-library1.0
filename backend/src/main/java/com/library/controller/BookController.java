package com.library.controller;

import com.library.common.Result;
import com.library.dto.BookCreateDTO;
import com.library.dto.BookDTO;
import com.library.dto.BookQueryDTO;
import com.library.dto.BookUpdateDTO;
import com.library.service.BookService;
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
 * 图书管理控制器
 */
@Api(tags = "图书管理")
@RestController
@RequestMapping("/book")
public class BookController {
    
    @Autowired
    private BookService bookService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private OperationLogService operationLogService;
    
    @ApiOperation("创建图书")
    @PostMapping
    public Result<BookDTO> createBook(@Validated @RequestBody BookCreateDTO createDTO, HttpServletRequest request) {
        try {
            BookDTO book = bookService.createBook(createDTO);
            String userId = getUserIdFromRequest(request);
            operationLogService.saveLog(userId, "创建图书", "创建图书：" + book.getTitle(), getIpAddress(request), request.getHeader("User-Agent"), "成功");
            return Result.success("创建成功", book);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @ApiOperation("更新图书")
    @PutMapping("/{bookId}")
    public Result<BookDTO> updateBook(@PathVariable String bookId, @Validated @RequestBody BookUpdateDTO updateDTO, HttpServletRequest request) {
        try {
            BookDTO book = bookService.updateBook(bookId, updateDTO);
            String userId = getUserIdFromRequest(request);
            operationLogService.saveLog(userId, "更新图书", "更新图书：" + bookId, getIpAddress(request), request.getHeader("User-Agent"), "成功");
            return Result.success("更新成功", book);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @ApiOperation("删除图书")
    @DeleteMapping("/{bookId}")
    public Result<Void> deleteBook(@PathVariable String bookId, HttpServletRequest request) {
        try {
            bookService.deleteBook(bookId);
            String userId = getUserIdFromRequest(request);
            operationLogService.saveLog(userId, "删除图书", "删除图书：" + bookId, getIpAddress(request), request.getHeader("User-Agent"), "成功");
            return Result.success("删除成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @ApiOperation("根据ID获取图书")
    @GetMapping("/{bookId}")
    public Result<BookDTO> getBookById(@PathVariable String bookId) {
        try {
            BookDTO book = bookService.getBookById(bookId);
            return Result.success(book);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @ApiOperation("多条件查询图书（分页）")
    @GetMapping("/query")
    public Result<List<BookDTO>> queryBooks(@Validated BookQueryDTO queryDTO) {
        try {
            List<BookDTO> books = bookService.queryBooks(queryDTO);
            return Result.success(books);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @ApiOperation("根据ISBN查询图书")
    @GetMapping("/isbn/{isbn}")
    public Result<BookDTO> getBookByIsbn(@PathVariable String isbn) {
        try {
            BookDTO book = bookService.getBookByIsbn(isbn);
            return Result.success(book);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @ApiOperation("更新库存")
    @PutMapping("/{bookId}/stock")
    public Result<Void> updateStock(@PathVariable String bookId, @RequestParam Integer stock, HttpServletRequest request) {
        try {
            bookService.updateStock(bookId, stock);
            String userId = getUserIdFromRequest(request);
            operationLogService.saveLog(userId, "更新库存", "更新图书库存：" + bookId + "，库存：" + stock, getIpAddress(request), request.getHeader("User-Agent"), "成功");
            return Result.success("更新成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @ApiOperation("增加库存")
    @PutMapping("/{bookId}/stock/increase")
    public Result<Void> increaseStock(@PathVariable String bookId, @RequestParam Integer quantity, HttpServletRequest request) {
        try {
            bookService.increaseStock(bookId, quantity);
            String userId = getUserIdFromRequest(request);
            operationLogService.saveLog(userId, "增加库存", "增加图书库存：" + bookId + "，数量：" + quantity, getIpAddress(request), request.getHeader("User-Agent"), "成功");
            return Result.success("增加成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @ApiOperation("减少库存")
    @PutMapping("/{bookId}/stock/decrease")
    public Result<Void> decreaseStock(@PathVariable String bookId, @RequestParam Integer quantity, HttpServletRequest request) {
        try {
            bookService.decreaseStock(bookId, quantity);
            String userId = getUserIdFromRequest(request);
            operationLogService.saveLog(userId, "减少库存", "减少图书库存：" + bookId + "，数量：" + quantity, getIpAddress(request), request.getHeader("User-Agent"), "成功");
            return Result.success("减少成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @ApiOperation("更新可用库存")
    @PutMapping("/{bookId}/available-stock")
    public Result<Void> updateAvailableStock(@PathVariable String bookId, @RequestParam Integer availableStock, HttpServletRequest request) {
        try {
            bookService.updateAvailableStock(bookId, availableStock);
            String userId = getUserIdFromRequest(request);
            operationLogService.saveLog(userId, "更新可用库存", "更新图书可用库存：" + bookId + "，可用库存：" + availableStock, getIpAddress(request), request.getHeader("User-Agent"), "成功");
            return Result.success("更新成功", null);
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

