package com.demo.weixin.vo.mine;

import com.demo.weixin.vo.BaseQueryVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 打卡记录分页查询入参
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "打卡记录分页查询入参")
public class CheckinRecordQueryVO extends BaseQueryVo {
}
