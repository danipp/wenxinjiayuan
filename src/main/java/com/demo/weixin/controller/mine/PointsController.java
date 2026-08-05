package com.demo.weixin.controller.mine;

import com.demo.common.core.result.Result;
import com.demo.weixin.annotation.NeedLogin;
import com.demo.weixin.controller.BaseController;
import com.demo.weixin.entity.store.PointsRecord;
import com.demo.weixin.entity.store.UserPoints;
import com.demo.weixin.service.store.PointsRecordService;
import com.demo.weixin.service.store.UserPointsService;
import com.demo.weixin.vo.store.PointsRecordQueryVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

/**
 * 个人中心-积分控制器
 * 提供用户积分余额查询和积分流水查询接口。
 * [修改 2026-08-03 19:10] 新增积分流水分页查询接口
 */
@RestController
@Tag(name = "个人中心-积分")
@RequestMapping("/api/mine/points")
@Slf4j
public class PointsController extends BaseController {

    @Autowired
    private UserPointsService userPointsService;
    // [新增 2026-08-03 19:10] 积分流水服务
    @Autowired
    private PointsRecordService pointsRecordService;

    /**
     * 查询当前用户积分账户详情（含余额、冻结、累计获得/消耗）
     */
    @GetMapping("/detail")
    @Operation(summary = "查询当前用户积分账户详情",
            responses = {@ApiResponse(description = "成功信息", content = @Content(schema = @Schema(implementation = UserPoints.class)))})
    @NeedLogin
    public Result<UserPoints> detail() {
        return Result.success(userPointsService.getOrCreate(getCurrentUserId()));
    }

    /**
     * 积分流水分页查询
     * [新增 2026-08-03 19:10] 查询当前用户的积分变动明细，支持按变动类型过滤
     * demo: POST /api/mine/points/records
     */
    @PostMapping("/records")
    @Operation(summary = "积分流水分页查询",
            description = "demo: POST /api/mine/points/records，支持按变动类型（1获得/2消耗/3退还）过滤，按时间倒序排列",
            responses = {@ApiResponse(description = "成功信息", content = @Content(schema = @Schema(implementation = PointsRecord.class)))})
    @NeedLogin
    public Result<Page<PointsRecord>> records(@RequestBody PointsRecordQueryVO queryVO) {
        queryVO.setPageNumber(queryVO.getPageNumber() - 1);

        if (queryVO.getPageNumber() < 0) {

            queryVO.setPageNumber(0);

        }
        Page<PointsRecord> page = pointsRecordService.getRecordPage(getCurrentUserId(), queryVO);
        return Result.success(page, page.getTotalElements());
    }
}
