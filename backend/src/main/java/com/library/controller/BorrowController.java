package com.library.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.library.common.Result;
import com.library.dto.BorrowDTO;
import com.library.dto.BorrowRecordDTO;
import com.library.dto.BorrowRecordQueryDTO;
import com.library.dto.RenewDTO;
import com.library.dto.ReturnDTO;
import com.library.service.BorrowService;
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
 * 借阅管理控制器
 */
@Api(tags = "借阅管理")
@RestController
@RequestMapping("/borrow")
public class BorrowController {
    
    @Autowired
    private BorrowService borrowService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private OperationLogService operationLogService;
    
    @ApiOperation("借阅图书")
    @PostMapping
    public Result<BorrowRecordDTO> borrowBook(@Validated @RequestBody BorrowDTO borrowDTO, HttpServletRequest request) {
        try {
            String userId = getUserIdFromRequest(request);
            BorrowRecordDTO record = borrowService.borrowBook(borrowDTO, userId);
            operationLogService.saveLog(userId, "借阅图书", "借阅图书：" + record.getBookTitle(), 
                    getIpAddress(request), request.getHeader("User-Agent"), "成功");
            return Result.success("借阅成功", record);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @ApiOperation("归还图书")
    @PostMapping("/return")
    public Result<BorrowRecordDTO> returnBook(@Validated @RequestBody ReturnDTO returnDTO, HttpServletRequest request) {
        try {
            String userId = getUserIdFromRequest(request);
            BorrowRecordDTO record = borrowService.returnBook(returnDTO, userId);
            operationLogService.saveLog(userId, "归还图书", "归还图书：" + record.getBookTitle(), 
                    getIpAddress(request), request.getHeader("User-Agent"), "成功");
            return Result.success("归还成功", record);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @ApiOperation("续借图书")
    @PostMapping("/renew")
    public Result<BorrowRecordDTO> renewBook(@Validated @RequestBody RenewDTO renewDTO, HttpServletRequest request) {
        try {
            String userId = getUserIdFromRequest(request);
            BorrowRecordDTO record = borrowService.renewBook(renewDTO, userId);
            operationLogService.saveLog(userId, "续借图书", "续借图书：" + record.getBookTitle(), 
                    getIpAddress(request), request.getHeader("User-Agent"), "成功");
            return Result.success("续借成功", record);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @ApiOperation("查询借阅记录列表（分页）")
    @GetMapping("/list")
    public Result<IPage<BorrowRecordDTO>> queryBorrowRecords(@Validated BorrowRecordQueryDTO queryDTO) {
        try {
            IPage<BorrowRecordDTO> records = borrowService.queryBorrowRecords(queryDTO);
            return Result.success(records);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @ApiOperation("查询当前用户的借阅记录")
    @GetMapping("/my")
    public Result<IPage<BorrowRecordDTO>> queryMyBorrowRecords(@Validated BorrowRecordQueryDTO queryDTO, HttpServletRequest request) {
        try {
            String userId = getUserIdFromRequest(request);
            IPage<BorrowRecordDTO> records = borrowService.queryUserBorrowRecords(userId, queryDTO);
            return Result.success(records);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @ApiOperation("查询指定用户的借阅记录")
    @GetMapping("/user/{userId}")
    public Result<IPage<BorrowRecordDTO>> queryUserBorrowRecords(@PathVariable String userId, @Validated BorrowRecordQueryDTO queryDTO) {
        try {
            IPage<BorrowRecordDTO> records = borrowService.queryUserBorrowRecords(userId, queryDTO);
            return Result.success(records);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @ApiOperation("查询图书的借阅记录")
    @GetMapping("/book/{bookId}")
    public Result<List<BorrowRecordDTO>> queryBookBorrowRecords(@PathVariable String bookId) {
        try {
            List<BorrowRecordDTO> records = borrowService.queryBookBorrowRecords(bookId);
            return Result.success(records);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @ApiOperation("查询逾期记录")
    @GetMapping("/overdue")
    public Result<IPage<BorrowRecordDTO>> queryOverdueRecords(@Validated BorrowRecordQueryDTO queryDTO) {
        try {
            IPage<BorrowRecordDTO> records = borrowService.queryOverdueRecords(queryDTO);
            return Result.success(records);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @ApiOperation("根据ID获取借阅记录")
    @GetMapping("/{recordId}")
    public Result<BorrowRecordDTO> getBorrowRecordById(@PathVariable Integer recordId) {
        try {
            BorrowRecordDTO record = borrowService.getBorrowRecordById(recordId);
            return Result.success(record);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @ApiOperation("自动更新逾期状态")
    @PostMapping("/update-overdue")
    public Result<Void> updateOverdueStatus() {
        try {
            borrowService.updateOverdueStatus();
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

