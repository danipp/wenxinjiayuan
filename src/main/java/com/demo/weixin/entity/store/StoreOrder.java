package com.demo.weixin.entity.store;

import com.demo.weixin.entity.Base;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 商城订单实体
 * 订单状态流转由 OrderStateMachine 集中管理，禁止直接修改 status 字段。
 * 支持积分兑换和现金购买两种支付方式。
 */
@Data
@NoArgsConstructor
@Document(collection = "storeOrder")
@Schema(description = "商城订单")
public class StoreOrder extends Base {

    /** 订单业务主键 */
    @Field
    private Long orderId;

    /** 订单号（业务编号，如 S20260730143052001） */
    @Field
    @Schema(description = "订单号")
    private String orderNum;

    /** 买家用户ID */
    @Field
    @Schema(description = "买家用户ID")
    private Long buyerUserId;

    /** 卖家用户ID */
    @Field
    @Schema(description = "卖家用户ID")
    private Long sellerUserId;

    /** 店铺ID */
    @Field
    @Schema(description = "店铺ID")
    private Long shopId;

    /** 商品ID */
    @Field
    @Schema(description = "商品ID")
    private Long goodsId;

    /** 商品标题（冗余，避免商品修改影响历史订单） */
    @Field
    @Schema(description = "商品标题")
    private String goodsTitle;

    /** 商品图片（冗余） */
    @Field
    @Schema(description = "商品图片")
    private String goodsImage;

    /** 支付类型：1积分兑换 2现金购买 3混合支付（积分+现金） */
    @Field
    @Schema(description = "支付类型：1积分兑换 2现金购买 3混合支付")
    private Integer payType;

    /** 积分单价 */
    @Field
    @Schema(description = "积分单价")
    private Integer pointsPrice;

    /** 现金单价 */
    @Field
    @Schema(description = "现金单价")
    private BigDecimal cashPrice;

    /** 购买数量 */
    @Field
    @Schema(description = "购买数量")
    private Integer count;

    /** 积分总额（单价*数量） */
    @Field
    @Schema(description = "积分总额")
    private Integer totalPoints;

    /** 现金总额（单价*数量） */
    @Field
    @Schema(description = "现金总额")
    private BigDecimal totalAmount;

    /** 订单状态（对应 OrderStatusEnum.code） */
    @Field
    @Schema(description = "订单状态")
    private Integer status;

    /** 核销码（6位数字，支付成功后生成） */
    @Field
    @Schema(description = "核销码")
    private String redeemCode;

    /** 支付时间 */
    @Field
    @Schema(description = "支付时间")
    private Date payTime;

    /** 核销时间 */
    @Field
    @Schema(description = "核销时间")
    private Date verifyTime;

    /** 退款时间 */
    @Field
    @Schema(description = "退款时间")
    private Date refundTime;

    /** 退款原因 */
    @Field
    @Schema(description = "退款原因")
    private String refundReason;

    /** 评价ID（null表示未评价） */
    @Field
    @Schema(description = "评价ID")
    private Long commentId;

    /** 微信支付参数JSON（缓存JSAPI签名参数） */
    @Field
    @Schema(description = "微信支付参数JSON")
    private String payParamsJson;

    /** 财务支付状态（对应 PaymentStatusEnum.code）：PENDING_PAY/PAID/REFUND_APPLY/REFUNDING/REFUNDED/CLOSED */
    @Field
    @Schema(description = "财务支付状态")
    private String paymentStatus;

    /** 微信支付流水账单号（支付回调后生成） */
    @Field
    @Schema(description = "微信支付流水号")
    private String transactionId;

    /** 微信退款流水号 */
    @Field
    @Schema(description = "微信退款流水号")
    private String refundId;

    /** 订单超时自动关闭时间（下单30分钟后） */
    @Field
    @Schema(description = "订单超时关闭时间")
    private Date expireTime;

    /** 订单全生命周期状态变更履历 */
    @Field
    @Schema(description = "订单状态变更履历")
    private List<OrderStatusHistory> statusHistories = new ArrayList<>();

    @Override
    public Long getID() {
        return orderId;
    }

    @Override
    public void setID(Long id) {
        this.orderId = id;
    }

    /**
     * 内部类：订单状态变更履历节点
     */
    @Data
    @NoArgsConstructor
    @Schema(description = "订单状态变更履历节点")
    public static class OrderStatusHistory {
        /** 变更后的订单业务状态 */
        @Field
        @Schema(description = "变更后的订单状态")
        private Integer status;

        /** 变更后的财务支付状态 */
        @Field
        @Schema(description = "变更后的支付状态")
        private String paymentStatus;

        /** 操作执行人（用户ID或角色标识） */
        @Field
        @Schema(description = "操作人")
        private String operator;

        /** 变更事由说明 */
        @Field
        @Schema(description = "变更事由")
        private String remark;

        /** 操作发生时间 */
        @Field
        @Schema(description = "操作时间")
        private Date createTime;
    }

    /**
     * 向订单安全追加一条状态变更履历
     *
     * @param status         变更后的订单业务状态
     * @param paymentStatus  变更后的支付状态
     * @param operator       操作人
     * @param remark         变更事由
     */
    public void addHistory(Integer status, String paymentStatus, String operator, String remark) {
        if (this.statusHistories == null) {
            this.statusHistories = new ArrayList<>();
        }
        OrderStatusHistory history = new OrderStatusHistory();
        history.setStatus(status);
        history.setPaymentStatus(paymentStatus);
        history.setOperator(operator);
        history.setRemark(remark);
        history.setCreateTime(new Date());
        this.statusHistories.add(history);
    }
}
