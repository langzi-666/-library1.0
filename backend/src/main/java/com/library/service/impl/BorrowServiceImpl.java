package com.library.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.library.dto.BorrowDTO;
import com.library.dto.BorrowRecordDTO;
import com.library.dto.BorrowRecordQueryDTO;
import com.library.dto.RenewDTO;
import com.library.dto.ReturnDTO;
import com.library.entity.Book;
import com.library.entity.BorrowRecord;
import com.library.entity.User;
import com.library.mapper.BookMapper;
import com.library.mapper.BorrowRecordMapper;
import com.library.mapper.UserMapper;
import com.library.service.BookService;
import com.library.service.BorrowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 借阅服务实现类
 */
@Service
public class BorrowServiceImpl extends ServiceImpl<BorrowRecordMapper, BorrowRecord> implements BorrowService {
    
    @Autowired
    private BorrowRecordMapper borrowRecordMapper;
    
    @Autowired
    private BookMapper bookMapper;
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private BookService bookService;
    
    // 借阅规则配置（可后续移到系统配置表）
    private static final int MAX_BORROW_COUNT = 5; // 普通用户最多同时借阅5本
    private static final int BORROW_DAYS = 30; // 借阅期限30天
    private static final int MAX_RENEW_COUNT = 1; // 最多续借1次
    private static final BigDecimal OVERDUE_FEE_PER_DAY = new BigDecimal("1.00"); // 每天逾期费用1元
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public BorrowRecordDTO borrowBook(BorrowDTO borrowDTO, String currentUserId) {
        // 确定借阅用户
        String userId = StringUtils.hasText(borrowDTO.getUserId()) ? borrowDTO.getUserId() : currentUserId;
        if (!StringUtils.hasText(userId)) {
            throw new RuntimeException("用户ID不能为空");
        }
        
        // 查询用户
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        // 检查用户是否可以借阅
        checkBorrowPermission(userId);
        
        // 查找图书（通过bookId或ISBN）
        Book book = findBookByIdOrIsbn(borrowDTO.getBookIdOrIsbn());
        if (book == null) {
            throw new RuntimeException("图书不存在");
        }
        
        // 检查图书库存
        if (book.getAvailableStock() == null || book.getAvailableStock() < borrowDTO.getQuantity()) {
            throw new RuntimeException("图书库存不足");
        }
        
        // 检查用户是否已借阅该图书且未归还
        int borrowingCount = borrowRecordMapper.countBorrowingByUserIdAndBookId(userId, book.getBookId());
        if (borrowingCount > 0) {
            throw new RuntimeException("您已借阅该图书且未归还，不能重复借阅");
        }
        
        // 检查用户当前借阅数量
        int currentBorrowingCount = borrowRecordMapper.countBorrowingByUserId(userId);
        if (currentBorrowingCount + borrowDTO.getQuantity() > MAX_BORROW_COUNT) {
            throw new RuntimeException("已达到借阅上限（最多同时借阅" + MAX_BORROW_COUNT + "本）");
        }
        
        // 创建借阅记录
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime dueDate = now.plusDays(BORROW_DAYS);
        
        BorrowRecord record = new BorrowRecord();
        record.setUserId(userId);
        record.setBookId(book.getBookId());
        record.setBorrowDate(now);
        record.setDueDate(dueDate);
        record.setRenewCount(0);
        record.setStatus("借阅中");
        record.setOverdueDays(0);
        record.setOverdueFee(BigDecimal.ZERO);
        record.setBookStatus("正常");
        record.setRemark(borrowDTO.getRemark());
        
        borrowRecordMapper.insert(record);
        
        // 更新图书库存
        bookService.decreaseStock(book.getBookId(), borrowDTO.getQuantity());
        
        return getBorrowRecordById(record.getRecordId());
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public BorrowRecordDTO returnBook(ReturnDTO returnDTO, String currentUserId) {
        // 查找借阅记录
        BorrowRecord record = findBorrowRecordByIdOrBookId(returnDTO.getRecordIdOrBookId(), returnDTO.getUserId(), currentUserId);
        if (record == null) {
            throw new RuntimeException("借阅记录不存在");
        }
        
        // 检查是否已归还
        if ("已归还".equals(record.getStatus())) {
            throw new RuntimeException("该图书已归还");
        }
        
        // 计算逾期
        LocalDateTime now = LocalDateTime.now();
        int overdueDays = 0;
        BigDecimal overdueFee = BigDecimal.ZERO;
        
        if (now.isAfter(record.getDueDate())) {
            overdueDays = (int) java.time.Duration.between(record.getDueDate(), now).toDays();
            overdueFee = OVERDUE_FEE_PER_DAY.multiply(new BigDecimal(overdueDays));
        }
        
        // 更新借阅记录
        record.setReturnDate(now);
        record.setStatus("已归还");
        record.setOverdueDays(overdueDays);
        record.setOverdueFee(overdueFee);
        record.setBookStatus(returnDTO.getBookStatus());
        record.setRemark(returnDTO.getRemark());
        
        borrowRecordMapper.updateById(record);
        
        // 更新图书库存
        Book book = bookMapper.selectById(record.getBookId());
        if (book != null) {
            // 如果图书丢失，不增加库存
            if (!"丢失".equals(returnDTO.getBookStatus())) {
                bookService.increaseStock(record.getBookId(), 1);
            } else {
                // 图书丢失，减少总库存
                bookService.decreaseStock(record.getBookId(), 1);
            }
            
            // 如果图书损坏，更新图书状态
            if ("损坏".equals(returnDTO.getBookStatus())) {
                book.setStatus("损坏");
                bookMapper.updateById(book);
            }
        }
        
        return getBorrowRecordById(record.getRecordId());
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public BorrowRecordDTO renewBook(RenewDTO renewDTO, String currentUserId) {
        // 查询借阅记录
        BorrowRecord record = borrowRecordMapper.selectById(renewDTO.getRecordId());
        if (record == null) {
            throw new RuntimeException("借阅记录不存在");
        }
        
        // 检查是否是当前用户的借阅记录
        if (!record.getUserId().equals(currentUserId)) {
            throw new RuntimeException("只能续借自己的借阅记录");
        }
        
        // 检查状态
        if (!"借阅中".equals(record.getStatus())) {
            throw new RuntimeException("只能续借借阅中的图书");
        }
        
        // 检查是否已逾期
        if (LocalDateTime.now().isAfter(record.getDueDate())) {
            throw new RuntimeException("已逾期的图书不能续借");
        }
        
        // 检查续借次数
        if (record.getRenewCount() >= MAX_RENEW_COUNT) {
            throw new RuntimeException("已达到最大续借次数（最多续借" + MAX_RENEW_COUNT + "次）");
        }
        
        // 检查是否在应还日期前7天内
        LocalDateTime now = LocalDateTime.now();
        long daysUntilDue = java.time.Duration.between(now, record.getDueDate()).toDays();
        if (daysUntilDue > 7) {
            throw new RuntimeException("只能在应还日期前7天内申请续借");
        }
        
        // 续借
        LocalDateTime newDueDate = record.getDueDate().plusDays(renewDTO.getDays());
        record.setDueDate(newDueDate);
        record.setRenewCount(record.getRenewCount() + 1);
        
        borrowRecordMapper.updateById(record);
        
        return getBorrowRecordById(record.getRecordId());
    }
    
    @Override
    public IPage<BorrowRecordDTO> queryBorrowRecords(BorrowRecordQueryDTO queryDTO) {
        Page<BorrowRecordDTO> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        return borrowRecordMapper.selectBorrowRecordList(page, queryDTO);
    }
    
    @Override
    public IPage<BorrowRecordDTO> queryUserBorrowRecords(String userId, BorrowRecordQueryDTO queryDTO) {
        queryDTO.setUserId(userId);
        return queryBorrowRecords(queryDTO);
    }
    
    @Override
    public List<BorrowRecordDTO> queryBookBorrowRecords(String bookId) {
        return borrowRecordMapper.selectBorrowRecordsByBookId(bookId);
    }
    
    @Override
    public IPage<BorrowRecordDTO> queryOverdueRecords(BorrowRecordQueryDTO queryDTO) {
        Page<BorrowRecordDTO> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        queryDTO.setOverdue(true);
        return borrowRecordMapper.selectOverdueRecords(page, queryDTO);
    }
    
    @Override
    public BorrowRecordDTO getBorrowRecordById(Integer recordId) {
        BorrowRecord record = borrowRecordMapper.selectById(recordId);
        if (record == null) {
            return null;
        }
        // 通过查询方法获取完整信息（包含图书和用户信息）
        BorrowRecordQueryDTO query = new BorrowRecordQueryDTO();
        Page<BorrowRecordDTO> page = new Page<>(1, 10000);
        IPage<BorrowRecordDTO> result = borrowRecordMapper.selectBorrowRecordList(page, query);
        return result.getRecords().stream()
                .filter(r -> r.getRecordId() != null && r.getRecordId().equals(recordId))
                .findFirst()
                .orElse(null);
    }
    
    @Override
    public void checkBorrowPermission(String userId) {
        // 查询用户
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        // 检查用户账户状态
        if ("锁定".equals(user.getStatus())) {
            throw new RuntimeException("账户已被锁定，不能借阅");
        }
        
        if ("已注销".equals(user.getStatus())) {
            throw new RuntimeException("账户已注销，不能借阅");
        }
        
        // 检查是否有逾期未还的图书
        int overdueCount = borrowRecordMapper.countOverdueByUserId(userId);
        if (overdueCount > 0) {
            throw new RuntimeException("您有逾期未还的图书，请先归还后才能借阅新书");
        }
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateOverdueStatus() {
        // 查询所有借阅中且已逾期的记录
        LambdaQueryWrapper<BorrowRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BorrowRecord::getStatus, "借阅中")
                .lt(BorrowRecord::getDueDate, LocalDateTime.now());
        
        List<BorrowRecord> overdueRecords = borrowRecordMapper.selectList(wrapper);
        
        for (BorrowRecord record : overdueRecords) {
            LocalDateTime now = LocalDateTime.now();
            int overdueDays = (int) java.time.Duration.between(record.getDueDate(), now).toDays();
            BigDecimal overdueFee = OVERDUE_FEE_PER_DAY.multiply(new BigDecimal(overdueDays));
            
            record.setStatus("已逾期");
            record.setOverdueDays(overdueDays);
            record.setOverdueFee(overdueFee);
            
            borrowRecordMapper.updateById(record);
        }
    }
    
    /**
     * 通过bookId或ISBN查找图书
     */
    private Book findBookByIdOrIsbn(String bookIdOrIsbn) {
        // 先按bookId查找
        Book book = bookMapper.selectById(bookIdOrIsbn);
        if (book != null) {
            return book;
        }
        
        // 再按ISBN查找
        LambdaQueryWrapper<Book> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Book::getIsbn, bookIdOrIsbn);
        return bookMapper.selectOne(wrapper);
    }
    
    /**
     * 通过recordId或bookId查找借阅记录
     */
    private BorrowRecord findBorrowRecordByIdOrBookId(String recordIdOrBookId, String userId, String currentUserId) {
        // 先尝试按recordId查找
        try {
            Integer recordId = Integer.parseInt(recordIdOrBookId);
            BorrowRecord record = borrowRecordMapper.selectById(recordId);
            if (record != null) {
                // 检查权限：如果是普通用户，只能操作自己的记录
                if (StringUtils.hasText(currentUserId) && !"系统管理员".equals(getUserRole(currentUserId)) 
                        && !"图书管理员".equals(getUserRole(currentUserId))) {
                    if (!record.getUserId().equals(currentUserId)) {
                        throw new RuntimeException("无权操作他人的借阅记录");
                    }
                }
                return record;
            }
        } catch (NumberFormatException e) {
            // 不是数字，继续按bookId查找
        }
        
        // 按bookId查找（查找该用户借阅该图书且未归还的记录）
        String finalUserId = StringUtils.hasText(userId) ? userId : currentUserId;
        LambdaQueryWrapper<BorrowRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BorrowRecord::getBookId, recordIdOrBookId)
                .eq(BorrowRecord::getUserId, finalUserId)
                .ne(BorrowRecord::getStatus, "已归还")
                .orderByDesc(BorrowRecord::getBorrowDate)
                .last("LIMIT 1");
        
        return borrowRecordMapper.selectOne(wrapper);
    }
    
    /**
     * 获取用户角色
     */
    private String getUserRole(String userId) {
        User user = userMapper.selectById(userId);
        return user != null ? user.getRole() : "";
    }
}

