package com.demo.weixin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 签到信息VO
 * [修改 2026-08-03 18:40] 扩展签到信息，新增今日是否签到、本周签到详情、奖励积分等字段
 *
 * @author zane
 */
@Schema(name = "签到信息")
@Data
public class SignInfoVo {

    @Schema(description = "是否可以抽取奖励（满7天签到后可领取周奖励）")
    private Boolean claimReward = false;

    @Schema(description = "签到总数（本周已签到天数）")
    private Integer signCount = 0;

    // [新增 2026-08-03 18:40] 今日是否已签到
    @Schema(description = "今日是否已签到")
    private Boolean signedToday = false;

    // [新增 2026-08-03 18:40] 本周每天签到状态，7个元素对应周一到周日，true=已签到
    @Schema(description = "本周每天签到状态（周一到周日）")
    private List<Boolean> weekSignStatus;

    // [新增 2026-08-03 18:40] 签到奖励积分（每日签到获得积分）
    @Schema(description = "每日签到奖励积分")
    private Integer dailyRewardPoints = 5;

    // [新增 2026-08-03 18:40] 周签到奖励积分（满7天签到可领取的额外奖励）
    @Schema(description = "满周签到奖励积分")
    private Integer weeklyRewardPoints = 20;

    // [新增 2026-08-03 18:40] 奖励是否已领取（本周）
    @Schema(description = "本周奖励是否已领取")
    private Boolean rewardClaimed = false;
}
