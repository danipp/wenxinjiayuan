package com.demo.weixin.controller.mine;

import com.demo.common.core.result.Result;
import com.demo.weixin.annotation.DistributedIdempotent;
import com.demo.weixin.annotation.ManageAuditLog;
import com.demo.weixin.annotation.NeedLogin;
import com.demo.weixin.constant.Constants;
import com.demo.weixin.controller.BaseController;
import com.demo.weixin.service.UserService;
import com.demo.weixin.vo.SignInfoVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 个人中心-签到控制器
 * [新增 2026-08-03 18:40] 提供每日签到、签到信息查询、满周奖励领取接口。
 * 每日签到奖励5积分，满7天签到可额外领取20积分奖励。
 */
@RestController
@Tag(name = "个人中心-签到")
@RequestMapping("/api/mine/sign")
@Slf4j
public class SignController extends BaseController {

    @Autowired
    private UserService userService;

    /**
     * 每日签到
     * demo: POST /api/mine/sign/do
     */
    @PostMapping("/do")
    @Operation(summary = "每日签到",
            description = "demo: POST /api/mine/sign/do，每天签到一次，奖励5积分",
            responses = {@ApiResponse(description = "签到成功", content = @Content(schema = @Schema(implementation = SignInfoVo.class)))})
    @NeedLogin
    @ManageAuditLog(module = "个人中心-签到", action = "每日签到")
    @DistributedIdempotent(prefix = Constants.LOCK_MINE_SIGN, message = "签到请求正在处理中，请不要高频连击")
    public Result<SignInfoVo> doSign() {
        SignInfoVo vo = userService.doSign(getCurrentUserId());
        return Result.success(vo);
    }

    /**
     * 查询本周签到信息
     * demo: GET /api/mine/sign/info
     */
    @GetMapping("/info")
    @Operation(summary = "查询本周签到信息",
            description = "demo: GET /api/mine/sign/info，返回本周签到状态、签到次数、奖励领取状态",
            responses = {@ApiResponse(description = "成功信息", content = @Content(schema = @Schema(implementation = SignInfoVo.class)))})
    @NeedLogin
    public Result<SignInfoVo> info() {
        SignInfoVo vo = userService.getSignInfo(getCurrentUserId());
        return Result.success(vo);
    }

    /**
     * 领取满周签到奖励
     * demo: POST /api/mine/sign/reward
     */
    @PostMapping("/reward")
    @Operation(summary = "领取满周签到奖励",
            description = "demo: POST /api/mine/sign/reward，满7天签到后可领取20积分额外奖励，每周限领一次",
            responses = {@ApiResponse(description = "领取成功", content = @Content(schema = @Schema(implementation = SignInfoVo.class)))})
    @NeedLogin
    @ManageAuditLog(module = "个人中心-签到", action = "领取满周签到奖励")
    @DistributedIdempotent(prefix = Constants.LOCK_MINE_SIGN_REWARD, message = "奖励领取请求正在处理中，请不要高频连击")
    public Result<SignInfoVo> claimReward() {
        SignInfoVo vo = userService.claimSignReward(getCurrentUserId());
        return Result.success(vo);
    }
}
