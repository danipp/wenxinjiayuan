package com.demo.weixin.controller.mine;

import com.demo.common.core.result.Result;
import com.demo.weixin.annotation.NeedLogin;
import com.demo.weixin.controller.BaseController;
import com.demo.weixin.service.mine.LeaderboardService;
import com.demo.weixin.vo.mine.LeaderboardItemVO;
import com.demo.weixin.vo.mine.LeaderboardVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 个人中心-达人排行榜控制器
 * 提供互助达人（按帮忙次数排序）和活动达人（按参与活动次数排序）排行榜查询，支持按社区筛选。
 */
@RestController
@Tag(name = "个人中心-达人排行榜")
@RequestMapping("/api/mine/leaderboard")
@Slf4j
public class LeaderboardController extends BaseController {

    @Autowired
    private LeaderboardService leaderboardService;

    @PostMapping("/list")
    @Operation(summary = "达人排行榜（互助达人/活动达人）",
            responses = {@ApiResponse(description = "成功信息", content = @Content(schema = @Schema(implementation = LeaderboardItemVO.class)))})
    @NeedLogin
    public Result<List<LeaderboardItemVO>> list(@RequestBody LeaderboardVO vo) {
        // 限制条数兜底，默认20
        Integer limit = vo.getLimit() != null ? vo.getLimit() : 20;
        List<LeaderboardItemVO> list = leaderboardService.getLeaderboard(vo.getType(), vo.getCommunity(), limit);
        return Result.success(list);
    }
}
