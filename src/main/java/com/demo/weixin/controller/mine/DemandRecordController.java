package com.demo.weixin.controller.mine;

import com.demo.common.core.result.Result;
import com.demo.weixin.annotation.DistributedIdempotent;
import com.demo.weixin.annotation.ManageAuditLog;
import com.demo.weixin.annotation.NeedLogin;
import com.demo.weixin.constant.Constants;
import com.demo.weixin.controller.BaseController;
import com.demo.weixin.entity.mine.DemandRecord;
import com.demo.weixin.service.mine.DemandRecordService;
import com.demo.weixin.vo.mine.DemandCreateVO;
import com.demo.weixin.vo.mine.DemandEvaluateVO;
import com.demo.weixin.vo.mine.DemandQueryVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

/**
 * 个人中心-需求/帮忙记录控制器
 * 提供需求/帮忙记录分页查询、发布需求、接单、确认完成、评价接口。
 * 状态流转：待帮忙 → 已接单 → 待评价 → 已完成。
 * 发布需求、接单、评价使用分布式幂等锁防重入，接单、完成、评价记录审计日志。
 */
@RestController
@Tag(name = "个人中心-需求/帮忙记录")
@RequestMapping("/api/mine/demand")
@Slf4j
public class DemandRecordController extends BaseController {

    @Autowired
    private DemandRecordService demandRecordService;

    @PostMapping("/page")
    @Operation(summary = "需求/帮忙记录分页查询",
            responses = {@ApiResponse(description = "成功信息", content = @Content(schema = @Schema(implementation = DemandRecord.class)))})
    @NeedLogin
    public Result<Page<DemandRecord>> page(@RequestBody DemandQueryVO queryVO) {
        // 前端页码从1开始，MongoDB从0开始
        queryVO.setPageNumber(queryVO.getPageNumber() - 1);
        if (queryVO.getPageNumber() < 0) {
            queryVO.setPageNumber(0);
        }
        Page<DemandRecord> page = demandRecordService.getDemandPage(getCurrentUserId(), queryVO);
        return Result.success(page, page.getTotalElements());
    }

    @PostMapping("/public/page")
    @Operation(summary = "公共需求列表（好事广场）",
            responses = {@ApiResponse(description = "成功信息", content = @Content(schema = @Schema(implementation = DemandRecord.class)))})
    @NeedLogin
    public Result<Page<DemandRecord>> publicPage(@RequestBody DemandQueryVO queryVO) {
        // 前端页码从1开始，MongoDB从0开始
        queryVO.setPageNumber(queryVO.getPageNumber() - 1);
        if (queryVO.getPageNumber() < 0) {
            queryVO.setPageNumber(0);
        }
        Page<DemandRecord> page = demandRecordService.getPublicDemandPage(getCurrentUserId(), queryVO);
        return Result.success(page, page.getTotalElements());
    }

    @GetMapping("/public/detail/{demandId}")
    @Operation(summary = "需求详情（公共视角）",
            responses = {@ApiResponse(description = "成功信息", content = @Content(schema = @Schema(implementation = DemandRecord.class)))})
    @NeedLogin
    public Result<DemandRecord> publicDetail(@PathVariable Long demandId) {
        return Result.success(demandRecordService.getDemandDetail(demandId));
    }

    @PostMapping("/create")
    @Operation(summary = "发布需求",
            responses = {@ApiResponse(description = "成功信息", content = @Content(schema = @Schema(implementation = DemandRecord.class)))})
    @NeedLogin
    @DistributedIdempotent(prefix = Constants.LOCK_MINE_DEMAND_CREATE, message = "发布请求正在处理中，请不要高频连击")
    public Result<DemandRecord> create(@Valid @RequestBody DemandCreateVO vo) {
        return Result.success(demandRecordService.createDemand(getCurrentUserId(), vo));
    }

    @PostMapping("/accept/{demandId}")
    @Operation(summary = "接单",
            responses = {@ApiResponse(description = "成功信息", content = @Content(schema = @Schema(implementation = DemandRecord.class)))})
    @NeedLogin
    @DistributedIdempotent(prefix = Constants.LOCK_MINE_DEMAND_ACCEPT, key = "#demandId", message = "接单请求正在处理中，请不要高频连击")
    @ManageAuditLog(module = "个人中心-需求记录", action = "接单")
    public Result<DemandRecord> accept(@PathVariable Long demandId) {
        return Result.success(demandRecordService.acceptDemand(getCurrentUserId(), demandId));
    }

    @PostMapping("/complete/{demandId}")
    @Operation(summary = "确认服务完成（已接单→待评价）",
            responses = {@ApiResponse(description = "成功信息", content = @Content(schema = @Schema(implementation = DemandRecord.class)))})
    @NeedLogin
    @DistributedIdempotent(prefix = Constants.LOCK_MINE_DEMAND_COMPLETE, message = "完成需求请求正在处理中，请不要高频连击")
    @ManageAuditLog(module = "个人中心-需求记录", action = "确认服务完成")
    public Result<DemandRecord> complete(@PathVariable Long demandId) {
        return Result.success(demandRecordService.completeDemand(getCurrentUserId(), demandId));
    }

    @PostMapping("/evaluate")
    @Operation(summary = "评价需求（待评价→已完成）",
            responses = {@ApiResponse(description = "成功信息", content = @Content(schema = @Schema(implementation = DemandRecord.class)))})
    @NeedLogin
    @DistributedIdempotent(prefix = Constants.LOCK_MINE_DEMAND_EVALUATE, key = "#vo.demandId", message = "评价请求正在处理中，请不要高频连击")
    @ManageAuditLog(module = "个人中心-需求记录", action = "评价需求")
    public Result<DemandRecord> evaluate(@Valid @RequestBody DemandEvaluateVO vo) {
        return Result.success(demandRecordService.evaluateDemand(getCurrentUserId(), vo));
    }
}
