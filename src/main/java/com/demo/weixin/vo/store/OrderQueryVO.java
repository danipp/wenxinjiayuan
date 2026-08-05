package com.demo.weixin.vo.store;

import com.demo.weixin.vo.BaseQueryVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 订单分页查询入参
 * 通过 role 区分买家/卖家视角，通过 status 或 statusList 筛选订单状态。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "订单分页查询入参")
public class OrderQueryVO extends BaseQueryVo {

    @Schema(description = "视角：buyer买家 seller卖家")
    private String role;

    @Schema(description = "订单状态码（对应OrderStatusEnum.code，不传则查全部）")
    private Integer status;

    @Schema(description = "多状态筛选（如卖家退款Tab传 [40,41,42,50]）")
    private List<Integer> statusList;

    @Schema(description = "支付类型：1积分兑换 2现金购买")
    private Integer payType;

    @Schema(description = "买家专用-是否待评价：true查已完成但未评价的订单")
    private Boolean pendingComment;
}
