package com.demo.weixin.service.store;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONUtil;
import com.demo.common.exception.BizException;
import com.demo.weixin.dao.store.StoreOrderDao;
import com.demo.weixin.entity.User;
import com.demo.weixin.entity.store.StoreOrder;
import com.demo.weixin.enums.store.OrderEventEnum;
import com.demo.weixin.enums.store.OrderStatusEnum;
import com.demo.weixin.enums.store.PayTypeEnum;
import com.demo.weixin.enums.store.PaymentStatusEnum;
import com.demo.weixin.service.UserService;
import com.demo.weixin.service.WxPaySwitchService;
import com.demo.weixin.vo.store.PayResultVO;
import com.github.binarywang.wxpay.bean.request.WxPayRefundV3Request;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderV3Request;
import com.github.binarywang.wxpay.bean.result.WxPayRefundV3Result;
import com.github.binarywang.wxpay.bean.result.enums.TradeTypeEnum;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.WxPayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

/**
 * 支付服务
 * <p>
 * 积分支付：同步完成，扣减积分并生成核销码。
 * 现金支付：调用微信支付 JSAPI 统一下单，返回支付参数给前端，支付结果通过微信回调异步更新。
 * 退款处理：积分原路返还，现金退款调用微信退款接口。
 * </p>
 */
@Service
@Slf4j
public class PaymentService {

    @Autowired
    private UserPointsService userPointsService;
    @Autowired
    private StoreOrderDao storeOrderDao;
    @Autowired
    private OrderStateMachine orderStateMachine;
    @Autowired
    private WxPaySwitchService wxPaySwitchService;
    @Autowired
    private UserService userService;
    @Autowired
    private com.demo.weixin.service.WechatMsgTemplateService wechatMsgTemplateService;

    @Value("${wx.payNotifyUrl}")
    private String payNotifyUrl;

    @Value("${wx.refundNotifyUrl}")
    private String refundNotifyUrl;

    /**
     * 积分支付（同步完成）
     * 扣减用户积分，生成核销码，订单状态流转 PENDING_PAY → PENDING_VERIFY
     *
     * @param userId 用户ID
     * @param order  订单（当前状态为PENDING_PAY）
     */
    public void payByPoints(Long userId, StoreOrder order) {
        // 1. 扣减积分（UserPointsService内部保证并发安全）
        userPointsService.deduct(userId, order.getTotalPoints(), "积分商城下单", order.getOrderId());
        // 2. 生成8位核销码
        String redeemCode = generateRedeemCode();
        // 3. 状态机流转
        OrderStatusEnum newStatus = orderStateMachine.transit(
                OrderStatusEnum.getByCode(order.getStatus()),
                OrderEventEnum.PAY_SUCCESS);
        // 4. 更新订单
        StoreOrder.OrderStatusHistory history = new StoreOrder.OrderStatusHistory();
        history.setStatus(newStatus.getCode());
        history.setPaymentStatus(PaymentStatusEnum.PAID.getCode());
        history.setOperator(String.valueOf(userId));
        history.setRemark("积分支付成功");
        history.setCreateTime(new Date());
        Boolean success = storeOrderDao.updateOneDocument(
                Criteria.where("orderId").is(order.getOrderId()),
                new Update()
                        .set("status", newStatus.getCode())
                        .set("paymentStatus", PaymentStatusEnum.PAID.getCode())
                        .set("redeemCode", redeemCode)
                        .set("payTime", new Date())
                        .push("statusHistories", history));
        if (!success) {
            // 订单更新失败，回滚积分
            userPointsService.refund(userId, order.getTotalPoints(), "积分支付失败回滚", order.getOrderId());
            throw new BizException("支付处理失败，积分已退回");
        }
        log.info("积分支付成功，userId={}，orderId={}，orderNum={}", userId, order.getOrderId(), order.getOrderNum());
    }

    /**
     * 现金支付-发起微信统一下单
     * 调用微信支付 JSAPI 统一下单接口，返回支付参数给前端调起支付。
     * 订单号使用 "S" 前缀（Store商城），与充电订单 "P" 前缀物理隔离。
     * createOrderV3返回的对象即为payParams，前端直接用于wx.requestPayment调起支付。
     *
     * @param order 订单（当前状态为PENDING_PAY）
     * @return 支付结果（含订单号和微信支付参数）
     */
    public PayResultVO payByCash(StoreOrder order) {
        log.info("现金支付-发起微信统一下单，orderNum={}，totalAmount={}",
                order.getOrderNum(), order.getTotalAmount());

        // 1. 获取用户openId（JSAPI支付必须）
        User user = userService.getUser(order.getBuyerUserId());
        if (user == null || user.getOpenId() == null) {
            throw new BizException("用户不存在或未授权微信登录，无法发起支付");
        }

        // 2. 构建微信统一下单请求
        WxPayUnifiedOrderV3Request request = new WxPayUnifiedOrderV3Request();
        request.setDescription("公益商城商品 - " + order.getGoodsTitle());
        request.setOutTradeNo(order.getOrderNum());
        request.setNotifyUrl(payNotifyUrl);

        // 金额：元转分
        WxPayUnifiedOrderV3Request.Amount amount = new WxPayUnifiedOrderV3Request.Amount();
        // 金额：元转分，使用HALF_UP四舍五入避免精度丢失
        amount.setTotal(order.getTotalAmount().multiply(BigDecimal.valueOf(100)).setScale(0, RoundingMode.HALF_UP).intValue());
        amount.setCurrency("CNY");
        request.setAmount(amount);

        // 支付者：openId
        WxPayUnifiedOrderV3Request.Payer payer = new WxPayUnifiedOrderV3Request.Payer();
        payer.setOpenid(user.getOpenId());
        request.setPayer(payer);

        // 3. 调用微信统一下单接口，createOrderV3返回的对象即为payParams
        WxPayService wxPayService = wxPaySwitchService.merchantPayService();
        Object payParams;
        try {
            payParams = wxPayService.createOrderV3(TradeTypeEnum.JSAPI, request);
        } catch (WxPayException e) {
            log.error("微信统一下单失败，orderNum={}", order.getOrderNum(), e);
            throw new BizException("微信支付下单失败：" + e.getMessage());
        }

        // 4. 将支付参数缓存到订单（便于后续排查问题）
        String payParamsJson = JSONUtil.toJsonStr(payParams);
        storeOrderDao.updateOneDocument(
                Criteria.where("orderId").is(order.getOrderId()),
                new Update().set("payParamsJson", payParamsJson));

        // 5. 构建返回结果
        PayResultVO result = new PayResultVO();
        result.setOrderId(order.getOrderId());
        result.setOrderNum(order.getOrderNum());
        result.setTotalAmount(order.getTotalAmount());
        result.setPayType(PayTypeEnum.CASH.getDesc());
        result.setPayParams(payParams);

        log.info("微信统一下单成功，orderNum={}", order.getOrderNum());
        return result;
    }

    /**
     * 混合支付-先扣积分后发起微信现金支付
     * [新增 2026-07-31 18:04] 支持积分+现金混合支付。
     * 1. 同步扣减用户积分
     * 2. 调用微信支付 JSAPI 统一下单，返回支付参数给前端
     * 3. 如果微信下单失败，自动回滚已扣减的积分
     * 支付回调成功后生成核销码，与现金支付回调逻辑一致（handlePayCallback统一处理）。
     *
     * @param userId 用户ID
     * @param order  订单（当前状态为PENDING_PAY）
     * @return 支付结果（含订单号和微信支付参数）
     */
    public PayResultVO payByMixed(Long userId, StoreOrder order) {
        log.info("混合支付-先扣积分后发起微信支付，orderNum={}，totalPoints={}，totalAmount={}",
                order.getOrderNum(), order.getTotalPoints(), order.getTotalAmount());

        // 1. 先扣减积分（同步完成，UserPointsService内部保证并发安全）
        userPointsService.deduct(userId, order.getTotalPoints(), "混合支付-积分扣减", order.getOrderId());

        // 2. 发起微信现金支付（复用payByCash逻辑）
        try {
            PayResultVO result = payByCash(order);
            // 覆盖返回类型为混合支付，补充积分总额
            result.setPayType(PayTypeEnum.MIXED.getDesc());
            result.setTotalPoints(order.getTotalPoints());
            return result;
        } catch (Exception e) {
            // 微信下单失败，回滚已扣减的积分
            log.error("混合支付微信下单失败，回滚积分，userId={}，orderNum={}", userId, order.getOrderNum(), e);
            userPointsService.refund(userId, order.getTotalPoints(), "混合支付失败回滚", order.getOrderId());
            throw e;
        }
    }

    /**
     * 支付回调处理
     * 微信支付回调或 DemoController 模拟回调均调用此方法。
     * 支付成功：PENDING_PAY → PENDING_VERIFY，生成核销码，记录微信流水号
     * 支付失败：PENDING_PAY → CANCELLED
     *
     * @param orderNum      订单号
     * @param success       支付是否成功
     * @param transactionId 微信支付流水号（DemoController模拟时可传null）
     */
    public void handlePayCallback(String orderNum, boolean success, String transactionId) {
        StoreOrder order = storeOrderDao.findOne(Criteria.where("orderNum").is(orderNum));
        if (order == null) {
            throw new BizException("订单不存在：" + orderNum);
        }
        // 幂等检查：如果订单已经是待核销状态且支付状态已为PAID，说明已处理过（微信可能重复回调）
        if (Integer.valueOf(OrderStatusEnum.PENDING_VERIFY.getCode()).equals(order.getStatus())
                && PaymentStatusEnum.PAID.getCode().equals(order.getPaymentStatus())) {
            log.info("支付回调幂等跳过，订单已为待核销且已支付状态，orderNum={}", orderNum);
            return;
        }
        if (success) {
            // 支付成功：生成核销码，状态流转
            OrderStatusEnum newStatus = orderStateMachine.transit(
                    OrderStatusEnum.getByCode(order.getStatus()),
                    OrderEventEnum.PAY_SUCCESS);
            String redeemCode = generateRedeemCode();
            StoreOrder.OrderStatusHistory successHistory = new StoreOrder.OrderStatusHistory();
            successHistory.setStatus(newStatus.getCode());
            successHistory.setPaymentStatus(PaymentStatusEnum.PAID.getCode());
            successHistory.setOperator("WXPAY_CALLBACK");
            successHistory.setRemark("微信支付回调成功");
            successHistory.setCreateTime(new Date());
            storeOrderDao.updateOneDocument(
                    Criteria.where("orderId").is(order.getOrderId()),
                    new Update()
                            .set("status", newStatus.getCode())
                            .set("paymentStatus", PaymentStatusEnum.PAID.getCode())
                            .set("redeemCode", redeemCode)
                            .set("payTime", new Date())
                            .set("transactionId", transactionId)
                            .push("statusHistories", successHistory));
            log.info("支付回调成功，orderNum={}，transactionId={}，核销码已生成", orderNum, transactionId);

            // [新增 2026-08-03] 通知卖家有新订单待核销
            try {
                java.util.Map<String, String> msgData = new java.util.HashMap<>();
                msgData.put("thing1", order.getGoodsTitle());
                msgData.put("character_string2", order.getOrderNum());
                msgData.put("phrase3", "待核销");
                wechatMsgTemplateService.pushWechatSubscribeMsg(order.getSellerUserId(), "ORDER_PAID", msgData, null);
            } catch (Exception e) {
                log.warn("订单支付成功通知发送失败，orderId={}", order.getOrderId(), e);
            }
        } else {
            // 支付失败：取消订单
            // [新增 2026-07-31 18:04] 混合支付订单需退还下单时已扣减的积分
            refundPointsForMixedOrder(order);
            OrderStatusEnum newStatus = orderStateMachine.transit(
                    OrderStatusEnum.getByCode(order.getStatus()),
                    OrderEventEnum.CANCEL);
            StoreOrder.OrderStatusHistory failHistory = new StoreOrder.OrderStatusHistory();
            failHistory.setStatus(newStatus.getCode());
            failHistory.setPaymentStatus(PaymentStatusEnum.CLOSED.getCode());
            failHistory.setOperator("WXPAY_CALLBACK");
            failHistory.setRemark("微信支付回调失败，订单关闭");
            failHistory.setCreateTime(new Date());
            storeOrderDao.updateOneDocument(
                    Criteria.where("orderId").is(order.getOrderId()),
                    new Update()
                            .set("status", newStatus.getCode())
                            .set("paymentStatus", PaymentStatusEnum.CLOSED.getCode())
                            .push("statusHistories", failHistory));
            log.warn("支付回调失败，订单已取消，orderNum={}", orderNum);
        }
    }

    /**
     * 退款处理
     * <p>
     * 积分订单：同步原路返还积分，直接流转至 REFUNDED。
     * 现金订单：调用微信退款接口提交退款请求，设置 paymentStatus=REFUNDING，
     *          等待微信退款回调确认后才流转至 REFUNDED（异步两步式）。
     * </p>
     *
     * @param order 订单（当前状态为REFUND_APPROVED）
     */
    public void processRefund(StoreOrder order) {
        if (Integer.valueOf(PayTypeEnum.POINTS.getCode()).equals(order.getPayType())) {
            // ===== 积分退款：同步完成 =====
            userPointsService.refund(order.getBuyerUserId(), order.getTotalPoints(), "订单退款-积分返还", order.getOrderId());
            // 状态机流转：退款已通过 → 已退款
            OrderStatusEnum newStatus = orderStateMachine.transit(
                    OrderStatusEnum.getByCode(order.getStatus()),
                    OrderEventEnum.REFUND_COMPLETE);
            StoreOrder.OrderStatusHistory refundHistory = new StoreOrder.OrderStatusHistory();
            refundHistory.setStatus(newStatus.getCode());
            refundHistory.setPaymentStatus(PaymentStatusEnum.REFUNDED.getCode());
            refundHistory.setOperator("SYSTEM");
            refundHistory.setRemark("积分退款完成，积分已原路返还");
            refundHistory.setCreateTime(new Date());
            storeOrderDao.updateOneDocument(
                    Criteria.where("orderId").is(order.getOrderId()),
                    new Update()
                            .set("status", newStatus.getCode())
                            .set("paymentStatus", PaymentStatusEnum.REFUNDED.getCode())
                            .set("refundTime", new Date())
                            .push("statusHistories", refundHistory));
            log.info("积分退款完成，orderNum={}，退还积分={}", order.getOrderNum(), order.getTotalPoints());
        } else {
            // ===== 现金退款或混合退款：异步两步式 =====
            // [新增 2026-07-31 18:04] 混合退款先同步退还积分，再提交微信退款
            if (Integer.valueOf(PayTypeEnum.MIXED.getCode()).equals(order.getPayType())) {
                userPointsService.refund(order.getBuyerUserId(), order.getTotalPoints(), "混合退款-积分返还", order.getOrderId());
                log.info("混合退款-积分已退还，orderNum={}，退还积分={}", order.getOrderNum(), order.getTotalPoints());
            }
            // 第一步：提交微信退款请求，设置 REFUNDING，等待回调确认
            WxPayService wxPayService = wxPaySwitchService.merchantPayService();
            WxPayRefundV3Request refundRequest = new WxPayRefundV3Request();
            refundRequest.setOutTradeNo(order.getOrderNum());
            refundRequest.setOutRefundNo("R" + order.getOrderNum());  // 退款单号前缀 R + 订单号
            refundRequest.setReason(order.getRefundReason() != null ? order.getRefundReason() : "用户申请退款");
            refundRequest.setNotifyUrl(refundNotifyUrl);

            WxPayRefundV3Request.Amount amount = new WxPayRefundV3Request.Amount();
            // 金额：元转分，使用HALF_UP四舍五入避免精度丢失
            int totalCents = order.getTotalAmount().multiply(BigDecimal.valueOf(100)).setScale(0, RoundingMode.HALF_UP).intValue();
            amount.setTotal(totalCents);
            amount.setRefund(totalCents);
            amount.setCurrency("CNY");
            refundRequest.setAmount(amount);

            WxPayRefundV3Result refundResult;
            try {
                refundResult = wxPayService.refundV3(refundRequest);
            } catch (WxPayException e) {
                log.error("微信退款提交失败，orderNum={}", order.getOrderNum(), e);
                throw new BizException("微信退款失败：" + e.getMessage());
            }

            // 微信退款已提交，记录微信退款单号，设置 paymentStatus=REFUNDING
            // 注意：status 保持 REFUND_APPROVED 不变，等回调确认后才流转至 REFUNDED
            String wxRefundId = refundResult.getRefundId();
            StoreOrder.OrderStatusHistory submitHistory = new StoreOrder.OrderStatusHistory();
            submitHistory.setStatus(order.getStatus());
            submitHistory.setPaymentStatus(PaymentStatusEnum.REFUNDING.getCode());
            submitHistory.setOperator("SYSTEM");
            submitHistory.setRemark("微信退款已提交，等待回调确认。微信退款单号：" + wxRefundId);
            submitHistory.setCreateTime(new Date());
            storeOrderDao.updateOneDocument(
                    Criteria.where("orderId").is(order.getOrderId()),
                    new Update()
                            .set("paymentStatus", PaymentStatusEnum.REFUNDING.getCode())
                            .set("refundId", wxRefundId)
                            .push("statusHistories", submitHistory));
            log.info("微信退款已提交，orderNum={}，outRefundNo=R{}，refundId={}，等待回调确认",
                    order.getOrderNum(), order.getOrderNum(), wxRefundId);
        }
    }

    /**
     * 微信退款回调处理
     * 微信退款回调通知或 DemoController 模拟回调均调用此方法。
     * 退款成功：REFUND_APPROVED → REFUNDED，记录微信退款流水号
     * 退款失败：paymentStatus 回退至 REFUND_APPLY，保留 REFUND_APPROVED 状态待人工介入
     *
     * @param outRefundNo 商户退款单号（格式为 "R" + 订单号，如 RS123456）
     * @param success     退款是否成功
     * @param refundId    微信退款流水号（DemoController模拟时可传null）
     */
    public void handleRefundCallback(String outRefundNo, boolean success, String refundId) {
        // 从退款单号反推订单号：outRefundNo = "R" + orderNum，去掉首字符 "R"
        String orderNum = outRefundNo.substring(1);
        StoreOrder order = storeOrderDao.findOne(Criteria.where("orderNum").is(orderNum));
        if (order == null) {
            throw new BizException("退款回调：订单不存在，orderNum=" + orderNum);
        }

        if (success) {
            // 幂等检查：如果已经是 REFUNDED 状态，说明已处理过（微信可能重复回调）
            if (Integer.valueOf(OrderStatusEnum.REFUNDED.getCode()).equals(order.getStatus())) {
                log.info("退款回调幂等跳过，订单已为已退款状态，orderNum={}", orderNum);
                return;
            }
            // 退款成功：REFUND_APPROVED → REFUNDED
            OrderStatusEnum newStatus = orderStateMachine.transit(
                    OrderStatusEnum.getByCode(order.getStatus()),
                    OrderEventEnum.REFUND_COMPLETE);
            StoreOrder.OrderStatusHistory successHistory = new StoreOrder.OrderStatusHistory();
            successHistory.setStatus(newStatus.getCode());
            successHistory.setPaymentStatus(PaymentStatusEnum.REFUNDED.getCode());
            successHistory.setOperator("WXPAY_REFUND_CALLBACK");
            successHistory.setRemark("微信退款回调确认成功，退款已到账");
            successHistory.setCreateTime(new Date());
            storeOrderDao.updateOneDocument(
                    Criteria.where("orderId").is(order.getOrderId()),
                    new Update()
                            .set("status", newStatus.getCode())
                            .set("paymentStatus", PaymentStatusEnum.REFUNDED.getCode())
                            .set("refundId", refundId)
                            .set("refundTime", new Date())
                            .push("statusHistories", successHistory));
            log.info("退款回调成功，orderNum={}，refundId={}，订单已流转至已退款", orderNum, refundId);

            // [新增 2026-08-03] 通知买家退款已到账
            try {
                java.util.Map<String, String> msgData = new java.util.HashMap<>();
                msgData.put("thing1", order.getGoodsTitle());
                msgData.put("character_string2", order.getOrderNum());
                msgData.put("amount3", order.getTotalAmount() != null ? order.getTotalAmount().toPlainString() + "元" : "");
                wechatMsgTemplateService.pushWechatSubscribeMsg(order.getBuyerUserId(), "REFUND_COMPLETED", msgData, null);
            } catch (Exception e) {
                log.warn("退款完成通知发送失败，orderId={}", order.getOrderId(), e);
            }
        } else {
            // 退款失败：status回退至REFUND_REQUESTED，paymentStatus回退至REFUND_APPLY，以便卖家重新发起退款
            StoreOrder.OrderStatusHistory failHistory = new StoreOrder.OrderStatusHistory();
            failHistory.setStatus(OrderStatusEnum.REFUND_REQUESTED.getCode());
            failHistory.setPaymentStatus(PaymentStatusEnum.REFUND_APPLY.getCode());
            failHistory.setOperator("WXPAY_REFUND_CALLBACK");
            failHistory.setRemark("微信退款回调确认失败，订单回退至退款申请中，可重新发起退款");
            failHistory.setCreateTime(new Date());
            storeOrderDao.updateOneDocument(
                    Criteria.where("orderId").is(order.getOrderId()),
                    new Update()
                            .set("status", OrderStatusEnum.REFUND_REQUESTED.getCode())
                            .set("paymentStatus", PaymentStatusEnum.REFUND_APPLY.getCode())
                            .set("refundId", refundId)
                            .push("statusHistories", failHistory));
            log.error("退款回调失败，orderNum={}，refundId={}，订单已回退至退款申请中", orderNum, refundId);
        }
    }

    /**
     * 混合支付订单关闭/取消时退还已扣减的积分
     * [新增 2026-07-31 18:04] 供 StoreOrderService 在取消订单、超时关闭时调用。
     * 非混合支付订单调用此方法无副作用（内部判断后跳过）。
     *
     * @param order 订单
     */
    public void refundPointsForMixedOrder(StoreOrder order) {
        if (Integer.valueOf(PayTypeEnum.MIXED.getCode()).equals(order.getPayType())
                && order.getTotalPoints() != null && order.getTotalPoints() > 0) {
            userPointsService.refund(order.getBuyerUserId(), order.getTotalPoints(), "订单取消-积分返还", order.getOrderId());
            log.info("混合支付订单关闭/取消，已退还积分，orderNum={}，退还积分={}", order.getOrderNum(), order.getTotalPoints());
        }
    }

    /**
     * 生成8位数字核销码（降低碰撞概率）
     */
    private String generateRedeemCode() {
        return RandomUtil.randomNumbers(8);
    }
}
