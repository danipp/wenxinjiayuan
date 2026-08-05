package com.demo.weixin.controller.mine;

import com.demo.common.core.result.Result;
import com.demo.weixin.annotation.DistributedIdempotent;
import com.demo.weixin.annotation.ManageAuditLog;
import com.demo.weixin.annotation.NeedLogin;
import com.demo.weixin.constant.Constants;
import com.demo.weixin.controller.BaseController;
import com.demo.weixin.entity.mine.FollowRecord;
import com.demo.weixin.service.mine.FollowRecordService;
import com.demo.weixin.vo.mine.FollowQueryVO;
import com.demo.weixin.vo.mine.FollowVO;
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
 * 个人中心-关注记录控制器
 * 提供关注记录分页查询和关注操作接口。
 * 关注操作使用分布式幂等锁防重入，关注成功后给被关注者增加积分。
 */
@RestController
@Tag(name = "个人中心-关注记录")
@RequestMapping("/api/mine/follow")
@Slf4j
public class FollowRecordController extends BaseController {

    @Autowired
    private FollowRecordService followRecordService;

    @PostMapping("/page")
    @Operation(summary = "关注记录分页查询",
            responses = {@ApiResponse(description = "成功信息", content = @Content(schema = @Schema(implementation = FollowRecord.class)))})
    @NeedLogin
    public Result<Page<FollowRecord>> page(@RequestBody FollowQueryVO queryVO) {
        // 前端页码从1开始，MongoDB从0开始
        queryVO.setPageNumber(queryVO.getPageNumber() - 1);
        if (queryVO.getPageNumber() < 0) {
            queryVO.setPageNumber(0);
        }
        Page<FollowRecord> page = followRecordService.getFollowPage(
                getCurrentUserId(), queryVO.getPageNumber(), queryVO.getPageSize());
        return Result.success(page, page.getTotalElements());
    }

    @PostMapping("/follow")
    @Operation(summary = "关注操作",
            responses = {@ApiResponse(description = "成功信息", content = @Content(schema = @Schema(implementation = FollowRecord.class)))})
    @NeedLogin
    @DistributedIdempotent(prefix = Constants.LOCK_MINE_FOLLOW, message = "关注请求正在处理中，请不要高频连击")
    public Result<FollowRecord> follow(@Valid @RequestBody FollowVO vo) {
        FollowRecord record = followRecordService.follow(vo.getTargetUserId(), getCurrentUserId(), vo);
        return Result.success(record);
    }

    @DeleteMapping("/unfollow/{targetUserId}")
    @Operation(summary = "取消关注",
            responses = {@ApiResponse(description = "成功信息")})
    @NeedLogin
    @DistributedIdempotent(prefix = Constants.LOCK_MINE_UNFOLLOW, message = "取消关注请求正在处理中，请不要高频连击")
    @ManageAuditLog(module = "个人中心-关注记录", action = "取消关注")
    public Result<Void> unfollow(@PathVariable Long targetUserId) {
        followRecordService.unfollow(targetUserId, getCurrentUserId());
        return Result.success();
    }
}
