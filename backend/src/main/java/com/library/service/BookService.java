package com.library.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.library.dto.BookCreateDTO;
import com.library.dto.BookDTO;
import com.library.dto.BookQueryDTO;
import com.library.dto.BookUpdateDTO;
import com.library.entity.Book;

import java.util.List;

/**
 * 图书服务接口
 */
public interface BookService extends IService<Book> {
    
    /**
     * 创建图书
     */
    BookDTO createBook(BookCreateDTO createDTO);
    
    /**
     * 更新图书
     */
    BookDTO updateBook(String bookId, BookUpdateDTO updateDTO);
    
    /**
     * 删除图书
     */
    void deleteBook(String bookId);
    
    /**
     * 根据ID获取图书
     */
    BookDTO getBookById(String bookId);
    
    /**
     * 多条件查询图书（分页）
     */
    List<BookDTO> queryBooks(BookQueryDTO queryDTO);
    
    /**
     * 更新库存
     */
    void updateStock(String bookId, Integer stock);
    
    /**
     * 增加库存
     */
    void increaseStock(String bookId, Integer quantity);
    
    /**
     * 减少库存
     */
    void decreaseStock(String bookId, Integer quantity);
    
    /**
     * 更新可用库存
     */
    void updateAvailableStock(String bookId, Integer availableStock);
    
    /**
     * 检查ISBN是否已存在
     */
    boolean existsByIsbn(String isbn);
    
    /**
     * 根据ISBN查询图书
     */
    BookDTO getBookByIsbn(String isbn);
}

