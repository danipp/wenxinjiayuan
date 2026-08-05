package com.demo.weixin.controller.admin;

import cn.hutool.core.date.DateUtil;
import com.demo.common.core.result.Result;
import com.demo.weixin.annotation.AdminNeedLogin;
import com.demo.weixin.annotation.ManageAuditLog;
import com.demo.weixin.entity.SystemAuditLog;
import com.demo.weixin.service.SystemAuditLogService;
import com.demo.weixin.vo.AuditLogQueryVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

/**
 * 管理端-系统操作审计日志控制层
 */
@RestController
@Tag(name = "管理后台-系统操作审计")
@RequestMapping("/manage/api/auditLog")
public class SystemAuditLogController extends BaseAdminController {

    @Autowired
    private SystemAuditLogService systemAuditLogService;

    @PostMapping("/page")
    @Operation(summary = "查询后台操作审计日志列表",
            responses = {@ApiResponse(description = "成功信息", content = @Content(schema = @Schema(implementation = SystemAuditLog.class)))})
    @AdminNeedLogin
    public Result<Page<SystemAuditLog>> queryLogPage(@RequestBody AuditLogQueryVO queryVO) {
        queryVO.setPageNumber(queryVO.getPageNumber() - 1);
        if (queryVO.getPageNumber() < 0) {
            // 注意， 第一页是从0 开始的...
            queryVO.setPageNumber(0);
        }
        Page<SystemAuditLog> page = systemAuditLogService.queryLogPage(queryVO);
        return Result.success(page, page.getTotalElements());
    }

    @DeleteMapping("/clear/{daysAgo}")
    @Operation(summary = "清理历史操作日志（仅限超级管理员）",
            responses = {@ApiResponse(description = "成功信息", content = @Content(schema = @Schema(implementation = String.class)))})
    @AdminNeedLogin
    @ManageAuditLog(module = "系统维护", action = "清理历史审计日志") // 此敏感操作本身也会被记录
    public Result<String> clearOldLogs(@PathVariable Integer daysAgo) {
        if (daysAgo == null || daysAgo < 30) {
            return Result.failed("出于安全审计需要，最少必须保留最近 30 天的操作日志");
        }
        Date beforeDate = DateUtil.offsetDay(new Date(), -daysAgo);
        long deletedCount = systemAuditLogService.clearOldLogs(beforeDate);
        return Result.success("成功清理 " + deletedCount + " 条历史日志。");
    }
}