package com.library.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.library.dto.BookCreateDTO;
import com.library.dto.BookDTO;
import com.library.dto.BookQueryDTO;
import com.library.dto.BookUpdateDTO;
import com.library.entity.Book;
import com.library.entity.Category;
import com.library.mapper.BookMapper;
import com.library.mapper.CategoryMapper;
import com.library.service.BookService;
import com.library.utils.BookIdGenerator;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 图书服务实现类
 */
@Service
public class BookServiceImpl extends ServiceImpl<BookMapper, Book> implements BookService {
    
    @Autowired
    private BookMapper bookMapper;
    
    @Autowired
    private CategoryMapper categoryMapper;
    
    @Autowired
    private BookIdGenerator bookIdGenerator;
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public BookDTO createBook(BookCreateDTO createDTO) {
        // 验证ISBN是否已存在
        if (existsByIsbn(createDTO.getIsbn())) {
            throw new RuntimeException("ISBN已存在");
        }
        
        // 验证分类是否存在
        Category category = categoryMapper.selectById(createDTO.getCategoryId());
        if (category == null) {
            throw new RuntimeException("分类不存在");
        }
        
        // 创建图书
        Book book = new Book();
        BeanUtils.copyProperties(createDTO, book);
        book.setBookId(bookIdGenerator.generateBookId());
        book.setStatus("在库");
        book.setAvailableStock(createDTO.getStock());
        
        bookMapper.insert(book);
        return convertToDTO(book);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public BookDTO updateBook(String bookId, BookUpdateDTO updateDTO) {
        Book book = bookMapper.selectById(bookId);
        if (book == null) {
            throw new RuntimeException("图书不存在");
        }
        
        // 验证ISBN是否与其他图书重复
        if (updateDTO.getIsbn() != null && !updateDTO.getIsbn().equals(book.getIsbn())) {
            Book existingBook = bookMapper.selectOne(
                    new LambdaQueryWrapper<Book>().eq(Book::getIsbn, updateDTO.getIsbn())
            );
            if (existingBook != null && !existingBook.getBookId().equals(bookId)) {
                throw new RuntimeException("ISBN已被其他图书使用");
            }
        }
        
        // 验证分类
        if (updateDTO.getCategoryId() != null) {
            Category category = categoryMapper.selectById(updateDTO.getCategoryId());
            if (category == null) {
                throw new RuntimeException("分类不存在");
            }
        }
        
        // 更新字段
        if (updateDTO.getTitle() != null) {
            book.setTitle(updateDTO.getTitle());
        }
        if (updateDTO.getAuthor() != null) {
            book.setAuthor(updateDTO.getAuthor());
        }
        if (updateDTO.getPublisher() != null) {
            book.setPublisher(updateDTO.getPublisher());
        }
        if (updateDTO.getPublishDate() != null) {
            book.setPublishDate(updateDTO.getPublishDate());
        }
        if (updateDTO.getIsbn() != null) {
            book.setIsbn(updateDTO.getIsbn());
        }
        if (updateDTO.getCategoryId() != null) {
            book.setCategoryId(updateDTO.getCategoryId());
        }
        if (updateDTO.getPrice() != null) {
            book.setPrice(updateDTO.getPrice());
        }
        if (updateDTO.getStock() != null) {
            book.setStock(updateDTO.getStock());
            // 如果可用库存大于新库存，则调整可用库存
            if (book.getAvailableStock() > updateDTO.getStock()) {
                book.setAvailableStock(updateDTO.getStock());
            }
        }
        if (updateDTO.getDescription() != null) {
            book.setDescription(updateDTO.getDescription());
        }
        if (updateDTO.getCoverImage() != null) {
            book.setCoverImage(updateDTO.getCoverImage());
        }
        if (updateDTO.getStatus() != null) {
            book.setStatus(updateDTO.getStatus());
        }
        
        bookMapper.updateById(book);
        return convertToDTO(book);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteBook(String bookId) {
        Book book = bookMapper.selectById(bookId);
        if (book == null) {
            throw new RuntimeException("图书不存在");
        }
        
        // 检查是否有借阅记录（这里暂时不检查，后续借阅模块完成后可以添加）
        // TODO: 检查借阅记录
        
        bookMapper.deleteById(bookId);
    }
    
    @Override
    public BookDTO getBookById(String bookId) {
        Book book = bookMapper.selectById(bookId);
        if (book == null) {
            throw new RuntimeException("图书不存在");
        }
        return convertToDTO(book);
    }
    
    @Override
    public List<BookDTO> queryBooks(BookQueryDTO queryDTO) {
        LambdaQueryWrapper<Book> wrapper = new LambdaQueryWrapper<>();
        
        // 关键词搜索（书名、作者、ISBN）
        if (StringUtils.hasText(queryDTO.getKeyword())) {
            wrapper.and(w -> w.like(Book::getTitle, queryDTO.getKeyword())
                    .or().like(Book::getAuthor, queryDTO.getKeyword())
                    .or().like(Book::getIsbn, queryDTO.getKeyword()));
        }
        
        // 分类筛选
        if (queryDTO.getCategoryId() != null) {
            wrapper.eq(Book::getCategoryId, queryDTO.getCategoryId());
        }
        
        // 出版社筛选
        if (StringUtils.hasText(queryDTO.getPublisher())) {
            wrapper.like(Book::getPublisher, queryDTO.getPublisher());
        }
        
        // 状态筛选
        if (StringUtils.hasText(queryDTO.getStatus())) {
            wrapper.eq(Book::getStatus, queryDTO.getStatus());
        }
        
        // 库存筛选
        if (queryDTO.getHasStock() != null) {
            if (queryDTO.getHasStock()) {
                wrapper.gt(Book::getAvailableStock, 0);
            } else {
                wrapper.eq(Book::getAvailableStock, 0);
            }
        }
        
        // 分页
        Page<Book> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        IPage<Book> bookPage = bookMapper.selectPage(page, wrapper);
        
        return bookPage.getRecords().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStock(String bookId, Integer stock) {
        if (stock < 0) {
            throw new RuntimeException("库存不能为负数");
        }
        
        Book book = bookMapper.selectById(bookId);
        if (book == null) {
            throw new RuntimeException("图书不存在");
        }
        
        book.setStock(stock);
        // 如果可用库存大于新库存，则调整可用库存
        if (book.getAvailableStock() > stock) {
            book.setAvailableStock(stock);
        }
        
        bookMapper.updateById(book);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void increaseStock(String bookId, Integer quantity) {
        if (quantity <= 0) {
            throw new RuntimeException("增加数量必须大于0");
        }
        
        Book book = bookMapper.selectById(bookId);
        if (book == null) {
            throw new RuntimeException("图书不存在");
        }
        
        book.setStock(book.getStock() + quantity);
        book.setAvailableStock(book.getAvailableStock() + quantity);
        
        bookMapper.updateById(book);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void decreaseStock(String bookId, Integer quantity) {
        if (quantity <= 0) {
            throw new RuntimeException("减少数量必须大于0");
        }
        
        Book book = bookMapper.selectById(bookId);
        if (book == null) {
            throw new RuntimeException("图书不存在");
        }
        
        if (book.getStock() < quantity) {
            throw new RuntimeException("库存不足");
        }
        
        book.setStock(book.getStock() - quantity);
        // 可用库存不能小于0
        int newAvailableStock = Math.max(0, book.getAvailableStock() - quantity);
        book.setAvailableStock(newAvailableStock);
        
        bookMapper.updateById(book);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateAvailableStock(String bookId, Integer availableStock) {
        if (availableStock < 0) {
            throw new RuntimeException("可用库存不能为负数");
        }
        
        Book book = bookMapper.selectById(bookId);
        if (book == null) {
            throw new RuntimeException("图书不存在");
        }
        
        if (availableStock > book.getStock()) {
            throw new RuntimeException("可用库存不能大于总库存");
        }
        
        book.setAvailableStock(availableStock);
        bookMapper.updateById(book);
    }
    
    @Override
    public boolean existsByIsbn(String isbn) {
        LambdaQueryWrapper<Book> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Book::getIsbn, isbn);
        return bookMapper.selectCount(wrapper) > 0;
    }
    
    @Override
    public BookDTO getBookByIsbn(String isbn) {
        LambdaQueryWrapper<Book> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Book::getIsbn, isbn);
        Book book = bookMapper.selectOne(wrapper);
        if (book == null) {
            throw new RuntimeException("图书不存在");
        }
        return convertToDTO(book);
    }
    
    /**
     * 转换为DTO
     */
    private BookDTO convertToDTO(Book book) {
        BookDTO dto = new BookDTO();
        BeanUtils.copyProperties(book, dto);
        
        // 查询分类名称
        if (book.getCategoryId() != null) {
            Category category = categoryMapper.selectById(book.getCategoryId());
            if (category != null) {
                dto.setCategoryName(category.getCategoryName());
            }
        }
        
        return dto;
    }
}

