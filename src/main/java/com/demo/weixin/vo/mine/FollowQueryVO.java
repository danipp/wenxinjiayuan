package com.demo.weixin.vo.mine;

import com.demo.weixin.vo.BaseQueryVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 关注记录分页查询入参
 * 查询当前用户关注了谁（followerUserId = 当前用户）。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "关注记录分页查询入参")
public class FollowQueryVO extends BaseQueryVo {
}
