package com.demo.weixin.controller.assistance;

import com.demo.common.core.result.Result;
import com.demo.weixin.annotation.AdminNeedLogin;
import com.demo.weixin.annotation.ManageAuditLog;
import com.demo.weixin.annotation.NeedLogin;
import com.demo.weixin.controller.BaseController;
import com.demo.weixin.entity.assistance.AssistanceStat;
import com.demo.weixin.service.assistance.AssistanceStatService;
import com.demo.weixin.vo.assistance.StatConfigVO;
import com.demo.weixin.vo.assistance.StatQueryVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 消费帮扶-统计配置控制器
 * 提供统计列表查询、统计配置保存/删除、初始化默认统计项接口。
 * 保存、删除、初始化操作均为管理员操作并记录审计日志。
 */
@RestController
@Tag(name = "消费帮扶-统计配置")
@RequestMapping("/api/assistance/stat")
@Slf4j
public class AssistanceStatController extends BaseController {

    @Autowired
    private AssistanceStatService assistanceStatService;

    // [变更 2026-08-03 18:00] 增加communityId参数用于社区数据隔离
    @GetMapping("/list")
    @Operation(summary = "获取统计列表",
            description = "demo: GET /api/assistance/stat/list?mode=all&communityId=123456",
            responses = {@ApiResponse(description = "成功返回统计列表", content = @Content(schema = @Schema(implementation = AssistanceStat.class)))})
    @NeedLogin
    public Result<List<AssistanceStat>> list(@RequestParam(defaultValue = "all") String mode,
                                              @RequestParam(required = false) Long communityId) {
        StatQueryVO vo = new StatQueryVO();
        vo.setMode(mode);
        vo.setCommunityId(communityId);
        return Result.success(assistanceStatService.getStatList(vo));
    }

    @PostMapping("/save")
    @Operation(summary = "保存统计配置（管理员）",
            description = "demo: POST /api/assistance/stat/save，body传StatConfigVO",
            responses = {@ApiResponse(description = "成功返回统计配置", content = @Content(schema = @Schema(implementation = AssistanceStat.class)))})
    @AdminNeedLogin
    @ManageAuditLog(module = "消费帮扶-统计配置", action = "保存统计配置")
    public Result<AssistanceStat> save(@Valid @RequestBody StatConfigVO vo) {
        return Result.success(assistanceStatService.saveStatConfig(vo));
    }

    @PostMapping("/delete/{statId}")
    @Operation(summary = "删除统计配置（管理员）",
            description = "demo: POST /api/assistance/stat/delete/123",
            responses = {@ApiResponse(description = "成功信息")})
    @AdminNeedLogin
    @ManageAuditLog(module = "消费帮扶-统计配置", action = "删除统计配置")
    public Result<Void> delete(@PathVariable Long statId) {
        assistanceStatService.deleteStatConfig(statId);
        return Result.success();
    }

    @PostMapping("/initDefault")
    @Operation(summary = "初始化默认统计项（管理员）",
            description = "demo: POST /api/assistance/stat/initDefault",
            responses = {@ApiResponse(description = "成功信息")})
    @AdminNeedLogin
    @ManageAuditLog(module = "消费帮扶-统计配置", action = "初始化默认统计项")
    public Result<Void> initDefault() {
        assistanceStatService.initDefaultStats();
        return Result.success();
    }
}
