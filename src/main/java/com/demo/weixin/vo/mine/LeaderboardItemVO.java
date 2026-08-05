package com.demo.weixin.vo.mine;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 达人排行榜条目
 */
@Data
@Schema(description = "达人排行榜条目")
public class LeaderboardItemVO {

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "昵称")
    private String nickName;

    @Schema(description = "头像")
    private String avatar;

    @Schema(description = "统计次数（帮忙次数或参与活动次数）")
    private Integer count;

    @Schema(description = "平均评分（仅互助达人有值）")
    private Double avgRating;
}
