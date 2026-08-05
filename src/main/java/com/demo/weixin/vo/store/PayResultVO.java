package com.demo.weixin.vo.store;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 支付结果VO（创建订单返回值）
 * 积分兑换：payParams为null，订单已同步完成支付
 * 现金购买：payParams为微信createOrderV3返回对象，前端直接用于wx.requestPayment调起支付
 */
@Data
@Schema(description = "支付结果VO")
public class PayResultVO {

    /** 订单ID */
    @Schema(description = "订单ID")
    private Long orderId;

    /** 订单号 */
    @Schema(description = "订单号")
    private String orderNum;

    /** 现金总额（现金购买时使用） */
    @Schema(description = "现金总额")
    private BigDecimal totalAmount;

    /** 积分总额（积分兑换时使用） */
    @Schema(description = "积分总额")
    private Integer totalPoints;

    /** 支付类型描述（积分兑换/现金购买） */
    @Schema(description = "支付类型描述")
    private String payType;

    /**
     * 微信支付参数（createOrderV3返回对象，前端直接用于wx.requestPayment）
     * 积分兑换时为null
     */
    @Schema(description = "微信支付参数（createOrderV3返回对象，积分兑换时为null）")
    private Object payParams;
}
