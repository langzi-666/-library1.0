package com.library.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.library.dto.*;
import com.library.entity.Book;
import com.library.entity.BorrowRecord;
import com.library.entity.Category;
import com.library.entity.User;
import com.library.mapper.BookMapper;
import com.library.mapper.BorrowRecordMapper;
import com.library.mapper.CategoryMapper;
import com.library.mapper.UserMapper;
import com.library.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 统计服务实现类
 */
@Service
public class StatisticsServiceImpl implements StatisticsService {
    
    @Autowired
    private BookMapper bookMapper;
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private BorrowRecordMapper borrowRecordMapper;
    
    @Autowired
    private CategoryMapper categoryMapper;
    
    @Override
    public StatisticsDTO getStatistics() {
        StatisticsDTO statistics = new StatisticsDTO();
        statistics.setBookStatistics(getBookStatistics());
        statistics.setUserStatistics(getUserStatistics());
        statistics.setBorrowStatistics(getBorrowStatistics());
        statistics.setPopularBooks(getPopularBooks(10));
        statistics.setActiveUsers(getActiveUsers(10));
        return statistics;
    }
    
    @Override
    public BookStatisticsDTO getBookStatistics() {
        BookStatisticsDTO bookStats = new BookStatisticsDTO();
        
        // 图书总数
        long totalBooks = bookMapper.selectCount(null);
        bookStats.setTotalBooks((int) totalBooks);
        
        // 库存统计
        List<Book> books = bookMapper.selectList(null);
        int totalStock = books.stream().mapToInt(Book::getStock).sum();
        int availableStock = books.stream().mapToInt(Book::getAvailableStock).sum();
        int borrowedStock = totalStock - availableStock;
        int lowStockCount = (int) books.stream().filter(b -> b.getAvailableStock() < 5).count();
        
        bookStats.setTotalStock(totalStock);
        bookStats.setAvailableStock(availableStock);
        bookStats.setBorrowedStock(borrowedStock);
        bookStats.setLowStockCount(lowStockCount);
        
        // 分类统计
        List<CategoryStatisticsDTO> categoryStats = getCategoryStatistics();
        bookStats.setCategoryStatistics(categoryStats);
        
        // 状态分布
        List<StatusStatisticsDTO> statusStats = getBookStatusStatistics();
        bookStats.setStatusStatistics(statusStats);
        
        // 新增图书趋势（最近30天）
        List<TimeSeriesDTO> newBooksTrend = getNewBooksTrend(30);
        bookStats.setNewBooksTrend(newBooksTrend);
        
        return bookStats;
    }
    
    @Override
    public UserStatisticsDTO getUserStatistics() {
        UserStatisticsDTO userStats = new UserStatisticsDTO();
        
        // 用户总数
        long totalUsers = userMapper.selectCount(null);
        userStats.setTotalUsers((int) totalUsers);
        
        // 角色分布
        List<RoleStatisticsDTO> roleStats = getRoleStatistics();
        userStats.setRoleStatistics(roleStats);
        
        // 状态分布
        List<StatusStatisticsDTO> statusStats = getUserStatusStatistics();
        userStats.setStatusStatistics(statusStats);
        
        // 新增用户趋势（最近30天）
        List<TimeSeriesDTO> newUsersTrend = getNewUsersTrend(30);
        userStats.setNewUsersTrend(newUsersTrend);
        
        // 活跃用户数量（最近30天有借阅行为的用户）
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        LambdaQueryWrapper<BorrowRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.ge(BorrowRecord::getBorrowDate, thirtyDaysAgo);
        List<BorrowRecord> recentBorrows = borrowRecordMapper.selectList(wrapper);
        long activeUsers = recentBorrows.stream()
                .map(BorrowRecord::getUserId)
                .distinct()
                .count();
        userStats.setActiveUsers((int) activeUsers);
        
        return userStats;
    }
    
    @Override
    public BorrowStatisticsDTO getBorrowStatistics() {
        BorrowStatisticsDTO borrowStats = new BorrowStatisticsDTO();
        
        List<BorrowRecord> allRecords = borrowRecordMapper.selectList(null);
        
        // 总借阅次数
        int totalBorrowCount = allRecords.size();
        borrowStats.setTotalBorrowCount(totalBorrowCount);
        
        // 总归还次数
        long totalReturnCount = allRecords.stream()
                .filter(r -> "已归还".equals(r.getStatus()))
                .count();
        borrowStats.setTotalReturnCount((int) totalReturnCount);
        
        // 当前借阅中数量
        long currentBorrowingCount = allRecords.stream()
                .filter(r -> "借阅中".equals(r.getStatus()))
                .count();
        borrowStats.setCurrentBorrowingCount((int) currentBorrowingCount);
        
        // 归还率
        BigDecimal returnRate = totalBorrowCount > 0
                ? BigDecimal.valueOf(totalReturnCount)
                        .divide(BigDecimal.valueOf(totalBorrowCount), 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100))
                : BigDecimal.ZERO;
        borrowStats.setReturnRate(returnRate);
        
        // 逾期统计
        List<BorrowRecord> overdueRecords = allRecords.stream()
                .filter(r -> "已逾期".equals(r.getStatus()) || 
                        ("借阅中".equals(r.getStatus()) && r.getDueDate() != null && 
                         r.getDueDate().isBefore(LocalDateTime.now())))
                .collect(Collectors.toList());
        
        int overdueBookCount = overdueRecords.size();
        long overdueUserCount = overdueRecords.stream()
                .map(BorrowRecord::getUserId)
                .distinct()
                .count();
        
        borrowStats.setOverdueBookCount(overdueBookCount);
        borrowStats.setOverdueUserCount((int) overdueUserCount);
        
        // 逾期率
        BigDecimal overdueRate = totalBorrowCount > 0
                ? BigDecimal.valueOf(overdueBookCount)
                        .divide(BigDecimal.valueOf(totalBorrowCount), 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100))
                : BigDecimal.ZERO;
        borrowStats.setOverdueRate(overdueRate);
        
        // 平均逾期天数
        if (!overdueRecords.isEmpty()) {
            double avgOverdueDays = overdueRecords.stream()
                    .filter(r -> r.getOverdueDays() != null && r.getOverdueDays() > 0)
                    .mapToInt(BorrowRecord::getOverdueDays)
                    .average()
                    .orElse(0.0);
            borrowStats.setAvgOverdueDays(BigDecimal.valueOf(avgOverdueDays).setScale(2, RoundingMode.HALF_UP));
        } else {
            borrowStats.setAvgOverdueDays(BigDecimal.ZERO);
        }
        
        // 借阅趋势（最近30天）
        List<TimeSeriesDTO> borrowTrend = getBorrowTrend(30);
        borrowStats.setBorrowTrend(borrowTrend);
        
        return borrowStats;
    }
    
    @Override
    public List<PopularBookDTO> getPopularBooks(Integer limit) {
        List<BorrowRecord> allRecords = borrowRecordMapper.selectList(null);
        
        // 按图书ID分组统计借阅次数
        Map<String, List<BorrowRecord>> bookGroups = allRecords.stream()
                .collect(Collectors.groupingBy(BorrowRecord::getBookId));
        
        List<PopularBookDTO> popularBooks = new ArrayList<>();
        
        for (Map.Entry<String, List<BorrowRecord>> entry : bookGroups.entrySet()) {
            String bookId = entry.getKey();
            List<BorrowRecord> records = entry.getValue();
            
            Book book = bookMapper.selectById(bookId);
            if (book == null) continue;
            
            PopularBookDTO popularBook = new PopularBookDTO();
            popularBook.setBookId(bookId);
            popularBook.setTitle(book.getTitle());
            popularBook.setAuthor(book.getAuthor());
            popularBook.setBorrowCount(records.size());
            
            // 计算平均借阅时长
            List<BorrowRecord> returnedRecords = records.stream()
                    .filter(r -> r.getReturnDate() != null && r.getBorrowDate() != null)
                    .collect(Collectors.toList());
            
            if (!returnedRecords.isEmpty()) {
                double avgDays = returnedRecords.stream()
                        .mapToLong(r -> java.time.Duration.between(r.getBorrowDate(), r.getReturnDate()).toDays())
                        .average()
                        .orElse(0.0);
                popularBook.setAvgBorrowDays(BigDecimal.valueOf(avgDays).setScale(2, RoundingMode.HALF_UP));
            } else {
                popularBook.setAvgBorrowDays(BigDecimal.ZERO);
            }
            
            // 借阅率
            if (book.getStock() > 0) {
                BigDecimal borrowRate = BigDecimal.valueOf(records.size())
                        .divide(BigDecimal.valueOf(book.getStock()), 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100));
                popularBook.setBorrowRate(borrowRate);
            } else {
                popularBook.setBorrowRate(BigDecimal.ZERO);
            }
            
            popularBooks.add(popularBook);
        }
        
        // 按借阅次数排序，取前limit个
        return popularBooks.stream()
                .sorted((a, b) -> b.getBorrowCount().compareTo(a.getBorrowCount()))
                .limit(limit != null ? limit : 10)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<ActiveUserDTO> getActiveUsers(Integer limit) {
        List<BorrowRecord> allRecords = borrowRecordMapper.selectList(null);
        
        // 按用户ID分组统计
        Map<String, List<BorrowRecord>> userGroups = allRecords.stream()
                .collect(Collectors.groupingBy(BorrowRecord::getUserId));
        
        List<ActiveUserDTO> activeUsers = new ArrayList<>();
        
        for (Map.Entry<String, List<BorrowRecord>> entry : userGroups.entrySet()) {
            String userId = entry.getKey();
            List<BorrowRecord> records = entry.getValue();
            
            User user = userMapper.selectById(userId);
            if (user == null) continue;
            
            ActiveUserDTO activeUser = new ActiveUserDTO();
            activeUser.setUserId(userId);
            activeUser.setUsername(user.getUsername());
            activeUser.setName(user.getName());
            activeUser.setBorrowCount(records.size());
            
            // 计算累计借阅时长
            List<BorrowRecord> returnedRecords = records.stream()
                    .filter(r -> r.getReturnDate() != null && r.getBorrowDate() != null)
                    .collect(Collectors.toList());
            
            long totalDays = returnedRecords.stream()
                    .mapToLong(r -> java.time.Duration.between(r.getBorrowDate(), r.getReturnDate()).toDays())
                    .sum();
            
            activeUser.setTotalBorrowDays(BigDecimal.valueOf(totalDays).setScale(2, RoundingMode.HALF_UP));
            
            activeUsers.add(activeUser);
        }
        
        // 按借阅次数排序，取前limit个
        return activeUsers.stream()
                .sorted((a, b) -> b.getBorrowCount().compareTo(a.getBorrowCount()))
                .limit(limit != null ? limit : 10)
                .collect(Collectors.toList());
    }
    
    /**
     * 获取分类统计
     */
    private List<CategoryStatisticsDTO> getCategoryStatistics() {
        List<Book> books = bookMapper.selectList(null);
        List<Category> categories = categoryMapper.selectList(null);
        
        Map<Integer, List<Book>> booksByCategory = books.stream()
                .collect(Collectors.groupingBy(Book::getCategoryId));
        
        List<CategoryStatisticsDTO> categoryStats = new ArrayList<>();
        
        for (Category category : categories) {
            List<Book> categoryBooks = booksByCategory.getOrDefault(category.getCategoryId(), new ArrayList<>());
            
            CategoryStatisticsDTO stat = new CategoryStatisticsDTO();
            stat.setCategoryId(category.getCategoryId());
            stat.setCategoryName(category.getCategoryName());
            stat.setBookCount(categoryBooks.size());
            stat.setStockCount(categoryBooks.stream().mapToInt(Book::getStock).sum());
            
            categoryStats.add(stat);
        }
        
        return categoryStats;
    }
    
    /**
     * 获取图书状态统计
     */
    private List<StatusStatisticsDTO> getBookStatusStatistics() {
        List<Book> books = bookMapper.selectList(null);
        Map<String, Long> statusCounts = books.stream()
                .collect(Collectors.groupingBy(Book::getStatus, Collectors.counting()));
        
        List<StatusStatisticsDTO> statusStats = new ArrayList<>();
        for (Map.Entry<String, Long> entry : statusCounts.entrySet()) {
            StatusStatisticsDTO stat = new StatusStatisticsDTO();
            stat.setStatus(entry.getKey());
            stat.setCount(entry.getValue().intValue());
            statusStats.add(stat);
        }
        
        return statusStats;
    }
    
    /**
     * 获取用户状态统计
     */
    private List<StatusStatisticsDTO> getUserStatusStatistics() {
        List<User> users = userMapper.selectList(null);
        Map<String, Long> statusCounts = users.stream()
                .collect(Collectors.groupingBy(User::getStatus, Collectors.counting()));
        
        List<StatusStatisticsDTO> statusStats = new ArrayList<>();
        for (Map.Entry<String, Long> entry : statusCounts.entrySet()) {
            StatusStatisticsDTO stat = new StatusStatisticsDTO();
            stat.setStatus(entry.getKey());
            stat.setCount(entry.getValue().intValue());
            statusStats.add(stat);
        }
        
        return statusStats;
    }
    
    /**
     * 获取角色统计
     */
    private List<RoleStatisticsDTO> getRoleStatistics() {
        List<User> users = userMapper.selectList(null);
        Map<String, Long> roleCounts = users.stream()
                .collect(Collectors.groupingBy(User::getRole, Collectors.counting()));
        
        List<RoleStatisticsDTO> roleStats = new ArrayList<>();
        for (Map.Entry<String, Long> entry : roleCounts.entrySet()) {
            RoleStatisticsDTO stat = new RoleStatisticsDTO();
            stat.setRole(entry.getKey());
            stat.setUserCount(entry.getValue().intValue());
            roleStats.add(stat);
        }
        
        return roleStats;
    }
    
    /**
     * 获取新增图书趋势
     */
    private List<TimeSeriesDTO> getNewBooksTrend(int days) {
        List<Book> books = bookMapper.selectList(null);
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(days);
        
        Map<String, Long> dailyCounts = books.stream()
                .filter(book -> book.getCreateTime() != null)
                .filter(book -> {
                    LocalDate createDate = book.getCreateTime().toLocalDate();
                    return !createDate.isBefore(startDate) && !createDate.isAfter(endDate);
                })
                .collect(Collectors.groupingBy(
                        book -> book.getCreateTime().toLocalDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                        Collectors.counting()
                ));
        
        List<TimeSeriesDTO> trend = new ArrayList<>();
        for (int i = days - 1; i >= 0; i--) {
            LocalDate date = endDate.minusDays(i);
            String dateStr = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            TimeSeriesDTO item = new TimeSeriesDTO();
            item.setTimeLabel(dateStr);
            item.setValue(dailyCounts.getOrDefault(dateStr, 0L).intValue());
            trend.add(item);
        }
        
        return trend;
    }
    
    /**
     * 获取新增用户趋势
     */
    private List<TimeSeriesDTO> getNewUsersTrend(int days) {
        List<User> users = userMapper.selectList(null);
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(days);
        
        Map<String, Long> dailyCounts = users.stream()
                .filter(user -> user.getRegisterDate() != null)
                .filter(user -> {
                    LocalDate registerDate = user.getRegisterDate().toLocalDate();
                    return !registerDate.isBefore(startDate) && !registerDate.isAfter(endDate);
                })
                .collect(Collectors.groupingBy(
                        user -> user.getRegisterDate().toLocalDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                        Collectors.counting()
                ));
        
        List<TimeSeriesDTO> trend = new ArrayList<>();
        for (int i = days - 1; i >= 0; i--) {
            LocalDate date = endDate.minusDays(i);
            String dateStr = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            TimeSeriesDTO item = new TimeSeriesDTO();
            item.setTimeLabel(dateStr);
            item.setValue(dailyCounts.getOrDefault(dateStr, 0L).intValue());
            trend.add(item);
        }
        
        return trend;
    }
    
    /**
     * 获取借阅趋势
     */
    private List<TimeSeriesDTO> getBorrowTrend(int days) {
        List<BorrowRecord> records = borrowRecordMapper.selectList(null);
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(days);
        
        Map<String, Long> dailyCounts = records.stream()
                .filter(record -> record.getBorrowDate() != null)
                .filter(record -> {
                    LocalDate borrowDate = record.getBorrowDate().toLocalDate();
                    return !borrowDate.isBefore(startDate) && !borrowDate.isAfter(endDate);
                })
                .collect(Collectors.groupingBy(
                        record -> record.getBorrowDate().toLocalDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                        Collectors.counting()
                ));
        
        List<TimeSeriesDTO> trend = new ArrayList<>();
        for (int i = days - 1; i >= 0; i--) {
            LocalDate date = endDate.minusDays(i);
            String dateStr = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            TimeSeriesDTO item = new TimeSeriesDTO();
            item.setTimeLabel(dateStr);
            item.setValue(dailyCounts.getOrDefault(dateStr, 0L).intValue());
            trend.add(item);
        }
        
        return trend;
    }
}

