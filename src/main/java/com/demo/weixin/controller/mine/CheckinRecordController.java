package com.demo.weixin.controller.mine;

import com.demo.common.core.result.Result;
import com.demo.weixin.annotation.DistributedIdempotent;
import com.demo.weixin.annotation.NeedLogin;
import com.demo.weixin.constant.Constants;
import com.demo.weixin.controller.BaseController;
import com.demo.weixin.entity.mine.CheckinRecord;
import com.demo.weixin.service.mine.CheckinRecordService;
import com.demo.weixin.vo.mine.CheckinCreateVO;
import com.demo.weixin.vo.mine.CheckinRecordQueryVO;
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
 * 个人中心-打卡记录控制器
 * 提供打卡记录分页查询、累计打卡数查询和创建打卡记录接口。
 */
@RestController
@Tag(name = "个人中心-打卡记录")
@RequestMapping("/api/mine/checkin")
@Slf4j
public class CheckinRecordController extends BaseController {

    @Autowired
    private CheckinRecordService checkinRecordService;

    @PostMapping("/page")
    @Operation(summary = "打卡记录分页查询",
            responses = {@ApiResponse(description = "成功信息", content = @Content(schema = @Schema(implementation = CheckinRecord.class)))})
    @NeedLogin
    public Result<Page<CheckinRecord>> page(@RequestBody CheckinRecordQueryVO queryVO) {
        // 前端页码从1开始，MongoDB从0开始
        queryVO.setPageNumber(queryVO.getPageNumber() - 1);
        if (queryVO.getPageNumber() < 0) {
            queryVO.setPageNumber(0);
        }
        Page<CheckinRecord> page = checkinRecordService.getCheckinPage(getCurrentUserId(), queryVO);
        return Result.success(page, page.getTotalElements());
    }

    @GetMapping("/count")
    @Operation(summary = "累计打卡数",
            responses = {@ApiResponse(description = "成功信息", content = @Content(schema = @Schema(implementation = Long.class)))})
    @NeedLogin
    public Result<Long> count() {
        return Result.success(checkinRecordService.getCheckinCount(getCurrentUserId()));
    }

    @PostMapping("/create")
    @Operation(summary = "创建打卡记录",
            responses = {@ApiResponse(description = "成功信息", content = @Content(schema = @Schema(implementation = CheckinRecord.class)))})
    @NeedLogin
    @DistributedIdempotent(prefix = Constants.LOCK_MINE_CHECKIN, message = "打卡请求正在火速处理中，请不要高频连击")
    public Result<CheckinRecord> create(@Valid @RequestBody CheckinCreateVO vo) {
        CheckinRecord record = checkinRecordService.createCheckinRecord(
                getCurrentUserId(), vo.getFrameNo(), vo.getFrameName(), vo.getFrameImage(), vo.getLocation());
        return Result.success(record);
    }
}
