package com.demo.weixin.service.store;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.demo.common.exception.BizException;
import com.demo.weixin.dao.store.StoreOrderDao;
import com.demo.weixin.entity.store.StoreGoods;
import com.demo.weixin.entity.store.StoreOrder;
import com.demo.weixin.entity.store.StoreShop;
import com.demo.weixin.enums.store.GoodsStatusEnum;
import com.demo.weixin.enums.store.OrderEventEnum;
import com.demo.weixin.enums.store.OrderStatusEnum;
import com.demo.weixin.enums.store.PayTypeEnum;
import com.demo.weixin.enums.store.PaymentStatusEnum;
import com.demo.weixin.vo.store.OrderCreateVO;
import com.demo.weixin.vo.store.OrderQueryVO;
import com.demo.weixin.vo.store.OrderRefundVO;
import com.demo.weixin.vo.store.OrderVerifyVO;
import com.demo.weixin.vo.store.PayResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 商城订单服务（核心业务）
 * <p>
 * 订单全生命周期管理：创建→支付→核销→完成 / 退款→已退款 / 取消。
 * 所有状态流转通过 OrderStateMachine 集中管理，禁止直接修改 status 字段。
 * 库存管理：下单扣减，取消/退款恢复。
 * </p>
 */
@Service
@Slf4j
public class StoreOrderService {

    @Autowired
    private StoreOrderDao storeOrderDao;
    @Autowired
    private StoreGoodsService storeGoodsService;
    @Autowired
    private StoreShopService storeShopService;
    @Autowired
    private PaymentService paymentService;
    @Autowired
    private OrderStateMachine orderStateMachine;
    @Autowired
    private com.demo.weixin.service.WechatMsgTemplateService wechatMsgTemplateService;

    /**
     * 创建订单
     * <p>
     * 积分支付：同步扣减积分，订单直接进入待核销状态，返回订单信息（无payParams）。
     * 现金支付：订单进入待支付状态，调用微信统一下单返回payParams给前端调起支付。
     * </p>
     *
     * @param buyerUserId 买家用户ID
     * @param vo          订单创建入参
     * @return 支付结果（积分兑换无payParams，现金购买含微信支付参数）
     */
    public PayResultVO createOrder(Long buyerUserId, OrderCreateVO vo) {
        // 1. 查询并校验商品
        StoreGoods goods = storeGoodsService.getGoodsDetail(vo.getGoodsId());
        if (!Integer.valueOf(GoodsStatusEnum.ON_SALE.getCode()).equals(goods.getStatus())) {
            throw new BizException("商品已下架，无法购买");
        }
        // 2. 校验支付类型
        PayTypeEnum payType = PayTypeEnum.getByCode(vo.getPayType());
        if (payType == null) {
            throw new BizException("无效的支付类型");
        }
        // S6: 校验支付类型与商品类型是否匹配
        Integer goodsType = goods.getGoodsType();
        // [变更 2026-07-31 18:04] 去掉 type=3 拦截，支持混合支付
        boolean payTypeMatch = false;
        if (Integer.valueOf(1).equals(goodsType)) {
            // 积分兑换商品，payType必须为1（积分兑换）
            payTypeMatch = Integer.valueOf(PayTypeEnum.POINTS.getCode()).equals(vo.getPayType());
        } else if (Integer.valueOf(2).equals(goodsType)) {
            // 现金购买商品，payType必须为2（现金购买）
            payTypeMatch = Integer.valueOf(PayTypeEnum.CASH.getCode()).equals(vo.getPayType());
        } else if (Integer.valueOf(3).equals(goodsType)) {
            // [新增 2026-07-31 18:04] 混合支付商品，payType必须为3（混合支付）
            payTypeMatch = Integer.valueOf(PayTypeEnum.MIXED.getCode()).equals(vo.getPayType());
        }
        if (!payTypeMatch) {
            throw new BizException("支付方式与商品类型不匹配");
        }
        int count = vo.getCount() != null ? vo.getCount() : 1;
        if (count <= 0) {
            throw new BizException("购买数量必须大于0");
        }
        // 3. 原子扣减库存
        Boolean stockDeducted = storeGoodsService.deductStock(goods.getGoodsId(), count);
        if (!stockDeducted) {
            throw new BizException("库存不足，下单失败");
        }
        // 4. 确定卖家
        Long sellerUserId = null;
        if (goods.getShopId() != null) {
            StoreShop shop = storeShopService.getShopDetail(goods.getShopId());
            if (shop != null) {
                sellerUserId = shop.getSellerUserId();
            }
        }
        // 5. 构建订单
        StoreOrder order = new StoreOrder();
        order.setOrderNum(generateOrderNum());
        order.setBuyerUserId(buyerUserId);
        order.setSellerUserId(sellerUserId);
        order.setShopId(goods.getShopId());
        order.setGoodsId(goods.getGoodsId());
        order.setGoodsTitle(goods.getTitle());
        order.setGoodsImage(goods.getCoverImage());
        order.setPayType(payType.getCode());
        order.setPointsPrice(goods.getPointsPrice() != null ? goods.getPointsPrice() : 0);
        order.setCashPrice(goods.getCashPrice() != null ? goods.getCashPrice() : BigDecimal.ZERO);
        order.setCount(count);
        order.setTotalPoints(order.getPointsPrice() * count);
        order.setTotalAmount(order.getCashPrice().multiply(BigDecimal.valueOf(count)));
        order.setStatus(OrderStatusEnum.PENDING_PAY.getCode());
        order.setPaymentStatus(PaymentStatusEnum.PENDING_PAY.getCode());
        // 订单30分钟超时自动关闭，由 StoreOrderTimeoutTask 定时扫描关闭
        order.setExpireTime(DateUtil.offsetMinute(new Date(), 30));
        // S2: insert失败时恢复已扣减的库存，避免库存永久丢失
        try {
            storeOrderDao.insertDocument(order);
        } catch (Exception e) {
            log.error("订单插入失败，恢复库存，buyerUserId={}，goodsId={}", buyerUserId, goods.getGoodsId(), e);
            storeGoodsService.restoreStock(goods.getGoodsId(), count);
            throw new BizException("订单创建失败，请重试");
        }
        // 记录初始状态履历
        order.addHistory(order.getStatus(), order.getPaymentStatus(),
                String.valueOf(buyerUserId), "创建订单");
        storeOrderDao.updateOneDocument(
                Criteria.where("orderId").is(order.getOrderId()),
                new Update().set("statusHistories", order.getStatusHistories()));
        log.info("创建订单，orderId={}，orderNum={}，buyerUserId={}，payType={}",
                order.getOrderId(), order.getOrderNum(), buyerUserId, payType.getDesc());
        // 6. 支付处理
        try {
            if (payType == PayTypeEnum.POINTS) {
                // 积分支付：同步完成
                paymentService.payByPoints(buyerUserId, order);
                // 构建积分支付结果（无payParams）
                PayResultVO result = new PayResultVO();
                result.setOrderId(order.getOrderId());
                result.setOrderNum(order.getOrderNum());
                result.setTotalPoints(order.getTotalPoints());
                result.setPayType(PayTypeEnum.POINTS.getDesc());
                return result;
            } else if (payType == PayTypeEnum.MIXED) {
                // [新增 2026-07-31 18:04] 混合支付：先扣积分，再发起微信现金支付
                return paymentService.payByMixed(buyerUserId, order);
            } else {
                // 现金支付：调用微信统一下单，返回payParams给前端
                return paymentService.payByCash(order);
            }
        } catch (BizException e) {
            // 支付失败：恢复库存，取消订单
            log.error("订单支付失败，orderId={}，error={}", order.getOrderId(), e.getMessage());
            storeGoodsService.restoreStock(goods.getGoodsId(), count);
            // 状态机流转：待支付 → 已取消
            OrderStatusEnum newStatus = orderStateMachine.transit(
                    OrderStatusEnum.PENDING_PAY, OrderEventEnum.CANCEL);
            StoreOrder.OrderStatusHistory cancelHistory = new StoreOrder.OrderStatusHistory();
            cancelHistory.setStatus(newStatus.getCode());
            cancelHistory.setPaymentStatus(PaymentStatusEnum.CLOSED.getCode());
            cancelHistory.setOperator(String.valueOf(buyerUserId));
            cancelHistory.setRemark("支付失败，自动取消");
            cancelHistory.setCreateTime(new Date());
            storeOrderDao.updateOneDocument(
                    Criteria.where("orderId").is(order.getOrderId()),
                    new Update()
                            .set("status", newStatus.getCode())
                            .set("paymentStatus", PaymentStatusEnum.CLOSED.getCode())
                            .push("statusHistories", cancelHistory));
            throw e;
        }
    }

    /**
     * 卖家核销订单
     * 校验核销码匹配后，状态流转 PENDING_VERIFY → COMPLETED
     *
     * @param sellerUserId 卖家用户ID（当前登录用户）
     * @param vo           核销入参（订单ID + 核销码）
     */
    public StoreOrder verifyOrder(Long sellerUserId, OrderVerifyVO vo) {
        StoreOrder order = storeOrderDao.findById(vo.getOrderId());
        if (order == null) {
            throw new BizException("订单不存在");
        }
        // L3: 校验卖家身份：sellerUserId为null时不允许直接核销
        if (order.getSellerUserId() == null) {
            throw new BizException("该商品无指定核销人，请联系客服");
        }
        if (!order.getSellerUserId().equals(sellerUserId)) {
            throw new BizException("无权核销他人订单");
        }
        // 校验核销码
        if (StrUtil.isBlank(order.getRedeemCode())) {
            throw new BizException("订单尚未生成核销码");
        }
        if (!order.getRedeemCode().equals(vo.getRedeemCode())) {
            throw new BizException("核销码错误，请重新向买家确认");
        }
        // 状态机流转
        OrderStatusEnum newStatus = orderStateMachine.transit(
                OrderStatusEnum.getByCode(order.getStatus()),
                OrderEventEnum.VERIFY);
        StoreOrder.OrderStatusHistory history = new StoreOrder.OrderStatusHistory();
        history.setStatus(newStatus.getCode());
        history.setPaymentStatus(order.getPaymentStatus());
        history.setOperator(String.valueOf(sellerUserId));
        history.setRemark("卖家核销完成");
        history.setCreateTime(new Date());
        storeOrderDao.updateOneDocument(
                Criteria.where("orderId").is(order.getOrderId()),
                new Update()
                        .set("status", newStatus.getCode())
                        .set("verifyTime", new Date())
                        .push("statusHistories", history));
        // [新增 2026-07-31 17:21] 订单核销完成，更新店铺月销量冗余字段
        if (order.getShopId() != null) {
            storeShopService.updateMonthlySales(order.getShopId(), order.getCount());
        }
        log.info("订单核销成功，orderId={}，sellerUserId={}", order.getOrderId(), sellerUserId);
        // [新增 2026-08-03] 通知买家订单已核销完成
        try {
            java.util.Map<String, String> msgData = new java.util.HashMap<>();
            msgData.put("thing1", order.getGoodsTitle());
            msgData.put("character_string2", order.getOrderNum());
            msgData.put("phrase3", "已完成");
            wechatMsgTemplateService.pushWechatSubscribeMsg(order.getBuyerUserId(), "ORDER_VERIFIED", msgData, null);
        } catch (Exception e) {
            log.warn("订单核销完成通知发送失败，orderId={}", order.getOrderId(), e);
        }
        return storeOrderDao.findById(order.getOrderId());
    }

    /**
     * 买家申请退款
     * 状态流转 PENDING_VERIFY → REFUND_REQUESTED
     *
     * @param buyerUserId 买家用户ID
     * @param vo          退款入参
     */
    public StoreOrder requestRefund(Long buyerUserId, OrderRefundVO vo) {
        StoreOrder order = storeOrderDao.findById(vo.getOrderId());
        if (order == null) {
            throw new BizException("订单不存在");
        }
        if (!order.getBuyerUserId().equals(buyerUserId)) {
            throw new BizException("无权操作他人订单");
        }
        // 状态机流转
        OrderStatusEnum newStatus = orderStateMachine.transit(
                OrderStatusEnum.getByCode(order.getStatus()),
                OrderEventEnum.REQUEST_REFUND);
        StoreOrder.OrderStatusHistory history = new StoreOrder.OrderStatusHistory();
        history.setStatus(newStatus.getCode());
        history.setPaymentStatus(PaymentStatusEnum.REFUND_APPLY.getCode());
        history.setOperator(String.valueOf(buyerUserId));
        history.setRemark("买家申请退款：" + vo.getReason());
        history.setCreateTime(new Date());
        storeOrderDao.updateOneDocument(
                Criteria.where("orderId").is(order.getOrderId()),
                new Update()
                        .set("status", newStatus.getCode())
                        .set("paymentStatus", PaymentStatusEnum.REFUND_APPLY.getCode())
                        .set("refundReason", vo.getReason())
                        .push("statusHistories", history));
        log.info("买家申请退款，orderId={}，buyerUserId={}，reason={}", order.getOrderId(), buyerUserId, vo.getReason());
        // [新增 2026-08-03] 通知卖家有退款申请待处理
        try {
            java.util.Map<String, String> msgData = new java.util.HashMap<>();
            msgData.put("thing1", order.getGoodsTitle());
            msgData.put("character_string2", order.getOrderNum());
            msgData.put("phrase3", "退款申请");
            wechatMsgTemplateService.pushWechatSubscribeMsg(order.getSellerUserId(), "REFUND_REQUESTED", msgData, null);
        } catch (Exception e) {
            log.warn("退款申请通知发送失败，orderId={}", order.getOrderId(), e);
        }
        return storeOrderDao.findById(order.getOrderId());
    }

    /**
     * 卖家同意退款
     * 状态流转 REFUND_REQUESTED → REFUND_APPROVED，然后执行退款处理：
     * - 积分退款：同步完成，REFUND_APPROVED → REFUNDED
     * - 现金退款：提交微信退款，paymentStatus=REFUNDING，等待回调确认后 REFUND_APPROVED → REFUNDED
     * 无论何种支付方式，库存均在同意退款时恢复。
     *
     * @param sellerUserId 卖家用户ID
     * @param orderId      订单ID
     */
    public StoreOrder approveRefund(Long sellerUserId, Long orderId) {
        StoreOrder order = storeOrderDao.findById(orderId);
        if (order == null) {
            throw new BizException("订单不存在");
        }
        if (order.getSellerUserId() != null && !order.getSellerUserId().equals(sellerUserId)) {
            throw new BizException("无权操作他人订单");
        }
        // 状态机流转：退款申请中 → 退款已通过
        OrderStatusEnum approvedStatus = orderStateMachine.transit(
                OrderStatusEnum.getByCode(order.getStatus()),
                OrderEventEnum.APPROVE_REFUND);
        // M11: 积分退款不设REFUNDING，直接让processRefund设为REFUNDED；只有现金退款才设REFUNDING
        boolean isPointsRefund = Integer.valueOf(PayTypeEnum.POINTS.getCode()).equals(order.getPayType());
        String paymentStatusToSet = isPointsRefund
                ? PaymentStatusEnum.REFUND_APPLY.getCode()
                : PaymentStatusEnum.REFUNDING.getCode();
        StoreOrder.OrderStatusHistory approveHistory = new StoreOrder.OrderStatusHistory();
        approveHistory.setStatus(approvedStatus.getCode());
        approveHistory.setPaymentStatus(paymentStatusToSet);
        approveHistory.setOperator(String.valueOf(sellerUserId));
        approveHistory.setRemark("卖家同意退款，退款处理中");
        approveHistory.setCreateTime(new Date());
        storeOrderDao.updateOneDocument(
                Criteria.where("orderId").is(orderId),
                new Update()
                        .set("status", approvedStatus.getCode())
                        .set("paymentStatus", paymentStatusToSet)
                        .push("statusHistories", approveHistory));
        // 重新查询订单（状态已更新为REFUND_APPROVED）
        order = storeOrderDao.findById(orderId);
        // S5: 执行退款处理，捕获异常时回退状态，避免订单卡死
        try {
            // 执行退款处理（积分同步完成REFUNDED；现金提交微信后设REFUNDING，等回调确认）
            paymentService.processRefund(order);
        } catch (Exception e) {
            log.error("退款处理失败，回退订单状态，orderId={}", orderId, e);
            // 回退状态：status回到REFUND_REQUESTED，paymentStatus回到REFUND_APPLY，以便卖家重新发起退款
            StoreOrder.OrderStatusHistory rollbackHistory = new StoreOrder.OrderStatusHistory();
            rollbackHistory.setStatus(OrderStatusEnum.REFUND_REQUESTED.getCode());
            rollbackHistory.setPaymentStatus(PaymentStatusEnum.REFUND_APPLY.getCode());
            rollbackHistory.setOperator(String.valueOf(sellerUserId));
            rollbackHistory.setRemark("退款处理失败，回退至退款申请中：" + e.getMessage());
            rollbackHistory.setCreateTime(new Date());
            storeOrderDao.updateOneDocument(
                    Criteria.where("orderId").is(orderId),
                    new Update()
                            .set("status", OrderStatusEnum.REFUND_REQUESTED.getCode())
                            .set("paymentStatus", PaymentStatusEnum.REFUND_APPLY.getCode())
                            .push("statusHistories", rollbackHistory));
            throw new BizException("退款申请失败，请重试");
        }
        // 恢复库存
        storeGoodsService.restoreStock(order.getGoodsId(), order.getCount());
        log.info("卖家同意退款，退款已提交处理，orderId={}，sellerUserId={}", orderId, sellerUserId);
        // [新增 2026-08-03] 通知买家退款已通过
        try {
            java.util.Map<String, String> msgData = new java.util.HashMap<>();
            msgData.put("thing1", order.getGoodsTitle());
            msgData.put("character_string2", order.getOrderNum());
            msgData.put("phrase3", "退款通过");
            wechatMsgTemplateService.pushWechatSubscribeMsg(order.getBuyerUserId(), "REFUND_APPROVED", msgData, null);
        } catch (Exception e) {
            log.warn("退款通过通知发送失败，orderId={}", order.getOrderId(), e);
        }
        return storeOrderDao.findById(orderId);
    }

    /**
     * 卖家拒绝退款
     * 状态流转 REFUND_REQUESTED → REFUND_REJECTED → 自动恢复 PENDING_VERIFY
     *
     * @param sellerUserId 卖家用户ID
     * @param orderId      订单ID
     */
    public StoreOrder rejectRefund(Long sellerUserId, Long orderId) {
        StoreOrder order = storeOrderDao.findById(orderId);
        if (order == null) {
            throw new BizException("订单不存在");
        }
        if (order.getSellerUserId() != null && !order.getSellerUserId().equals(sellerUserId)) {
            throw new BizException("无权操作他人订单");
        }
        // 状态机流转：退款申请中 → 退款被拒绝
        OrderStatusEnum rejectedStatus = orderStateMachine.transit(
                OrderStatusEnum.getByCode(order.getStatus()),
                OrderEventEnum.REJECT_REFUND);
        StoreOrder.OrderStatusHistory rejectHistory = new StoreOrder.OrderStatusHistory();
        rejectHistory.setStatus(rejectedStatus.getCode());
        rejectHistory.setPaymentStatus(PaymentStatusEnum.PAID.getCode());
        rejectHistory.setOperator(String.valueOf(sellerUserId));
        rejectHistory.setRemark("卖家拒绝退款");
        rejectHistory.setCreateTime(new Date());
        storeOrderDao.updateOneDocument(
                Criteria.where("orderId").is(orderId),
                new Update()
                        .set("status", rejectedStatus.getCode())
                        .set("paymentStatus", PaymentStatusEnum.PAID.getCode())
                        .push("statusHistories", rejectHistory));
        // 自动恢复：退款被拒绝 → 待核销
        OrderStatusEnum restoredStatus = orderStateMachine.transit(
                rejectedStatus, OrderEventEnum.RESTORE);
        StoreOrder.OrderStatusHistory restoreHistory = new StoreOrder.OrderStatusHistory();
        restoreHistory.setStatus(restoredStatus.getCode());
        restoreHistory.setPaymentStatus(PaymentStatusEnum.PAID.getCode());
        restoreHistory.setOperator(String.valueOf(sellerUserId));
        restoreHistory.setRemark("拒绝退款后恢复待核销");
        restoreHistory.setCreateTime(new Date());
        storeOrderDao.updateOneDocument(
                Criteria.where("orderId").is(orderId),
                new Update()
                        .set("status", restoredStatus.getCode())
                        .push("statusHistories", restoreHistory));
        log.info("卖家拒绝退款，订单恢复待核销，orderId={}，sellerUserId={}", orderId, sellerUserId);
        return storeOrderDao.findById(orderId);
    }

    /**
     * 买家取消订单（仅限待支付状态）
     * 状态流转 PENDING_PAY → CANCELLED，恢复库存
     *
     * @param buyerUserId 买家用户ID
     * @param orderId     订单ID
     */
    public StoreOrder cancelOrder(Long buyerUserId, Long orderId) {
        StoreOrder order = storeOrderDao.findById(orderId);
        if (order == null) {
            throw new BizException("订单不存在");
        }
        if (!order.getBuyerUserId().equals(buyerUserId)) {
            throw new BizException("无权操作他人订单");
        }
        // 状态机流转
        OrderStatusEnum newStatus = orderStateMachine.transit(
                OrderStatusEnum.getByCode(order.getStatus()),
                OrderEventEnum.CANCEL);
        StoreOrder.OrderStatusHistory history = new StoreOrder.OrderStatusHistory();
        history.setStatus(newStatus.getCode());
        history.setPaymentStatus(PaymentStatusEnum.CLOSED.getCode());
        history.setOperator(String.valueOf(buyerUserId));
        history.setRemark("买家取消订单");
        history.setCreateTime(new Date());
        storeOrderDao.updateOneDocument(
                Criteria.where("orderId").is(orderId),
                new Update()
                        .set("status", newStatus.getCode())
                        .set("paymentStatus", PaymentStatusEnum.CLOSED.getCode())
                        .push("statusHistories", history));
        // [新增 2026-07-31 18:04] 混合支付订单取消时退还已扣减的积分
        paymentService.refundPointsForMixedOrder(order);
        // 恢复库存
        storeGoodsService.restoreStock(order.getGoodsId(), order.getCount());
        log.info("买家取消订单，orderId={}，buyerUserId={}", orderId, buyerUserId);
        // [新增 2026-08-03] 通知卖家订单已取消
        try {
            java.util.Map<String, String> msgData = new java.util.HashMap<>();
            msgData.put("thing1", order.getGoodsTitle());
            msgData.put("character_string2", order.getOrderNum());
            msgData.put("phrase3", "已取消");
            wechatMsgTemplateService.pushWechatSubscribeMsg(order.getSellerUserId(), "ORDER_CANCELLED", msgData, null);
        } catch (Exception e) {
            log.warn("订单取消通知发送失败，orderId={}", order.getOrderId(), e);
        }
        return storeOrderDao.findById(orderId);
    }

    /**
     * 分页查询订单
     * 通过 role 区分买家/卖家视角，支持按状态、支付类型、待评价筛选。
     *
     * @param userId 当前用户ID
     * @param queryVO 查询条件
     */
    public Page<StoreOrder> queryOrderPage(Long userId, OrderQueryVO queryVO) {
        Pageable pageable = PageRequest.of(queryVO.getPageNumber(), queryVO.getPageSize(),
                Sort.by(Sort.Order.desc("createTime")));
        Criteria criteria = new Criteria();
        // 视角区分
        String role = StrUtil.isBlank(queryVO.getRole()) ? "buyer" : queryVO.getRole();
        if ("seller".equals(role)) {
            criteria.and("sellerUserId").is(userId);
        } else {
            criteria.and("buyerUserId").is(userId);
        }
        // 支付类型筛选
        if (queryVO.getPayType() != null) {
            criteria.and("payType").is(queryVO.getPayType());
        }
        // 状态筛选：待评价优先（合并到条件中），其次 statusList（多状态），最后 status（单状态）
        if (Boolean.TRUE.equals(queryVO.getPendingComment())) {
            // 待评价只查已完成的订单
            criteria.and("status").is(OrderStatusEnum.COMPLETED.getCode())
                    .and("commentId").is(null);
        } else if (CollectionUtil.isNotEmpty(queryVO.getStatusList())) {
            criteria.and("status").in(queryVO.getStatusList());
        } else if (queryVO.getStatus() != null) {
            criteria.and("status").is(queryVO.getStatus());
        }
        return storeOrderDao.findDocumentPage(criteria, pageable);
    }

    /**
     * 获取订单详情（校验当前用户是否是买家或卖家）
     *
     * @param orderId 订单ID
     * @param userId  当前用户ID
     */
    public StoreOrder getOrderDetail(Long orderId, Long userId) {
        StoreOrder order = storeOrderDao.findById(orderId);
        if (order == null) {
            throw new BizException("订单不存在");
        }
        // S3: 校验当前用户是否是买家或卖家
        if (!order.getBuyerUserId().equals(userId)
                && (order.getSellerUserId() == null || !order.getSellerUserId().equals(userId))) {
            throw new BizException("无权查看此订单");
        }
        return order;
    }

    /**
     * 买家获取核销码
     */
    public String getRedeemCode(Long buyerUserId, Long orderId) {
        StoreOrder order = storeOrderDao.findById(orderId);
        if (order == null) {
            throw new BizException("订单不存在");
        }
        if (!order.getBuyerUserId().equals(buyerUserId)) {
            throw new BizException("无权查看他人订单");
        }
        if (StrUtil.isBlank(order.getRedeemCode())) {
            throw new BizException("订单尚未支付，无核销码");
        }
        return order.getRedeemCode();
    }

    /**
     * 卖家退款相关订单状态码列表（供前端查询退款Tab使用）
     */
    public static final List<Integer> REFUND_STATUS_CODES = Arrays.asList(
            OrderStatusEnum.REFUND_REQUESTED.getCode(),
            OrderStatusEnum.REFUND_APPROVED.getCode(),
            OrderStatusEnum.REFUND_REJECTED.getCode(),
            OrderStatusEnum.REFUNDED.getCode()
    );

    /**
     * 生成订单号：S + yyyyMMddHHmmss + 6位随机数
     */
    private String generateOrderNum() {
        return "S" + DateUtil.format(new Date(), "yyyyMMddHHmmss") + RandomUtil.randomNumbers(6);
    }

    /**
     * 查询所有超时未支付的现金订单
     * 条件：status=PENDING_PAY（待支付）且 expireTime <= 当前时间
     * 仅现金支付订单才会有超时问题（积分支付是同步完成的，不存在待支付状态）
     *
     * @return 超时订单列表
     */
    public List<StoreOrder> findExpiredOrders() {
        Criteria criteria = Criteria.where("status").is(OrderStatusEnum.PENDING_PAY.getCode())
                .and("expireTime").lte(new Date());
        return storeOrderDao.findDocumentList(criteria);
    }

    /**
     * 关闭超时未支付订单（系统自动操作）
     * 状态流转 PENDING_PAY → CANCELLED，恢复库存，记录状态履历。
     * 使用条件更新保证原子性：只有 status 仍为 PENDING_PAY 时才执行关闭。
     *
     * @param order 超时订单
     * @return true=关闭成功，false=订单状态已变更（无需关闭）
     */
    public boolean closeExpiredOrder(StoreOrder order) {
        // 使用条件更新保证原子性：status 必须仍为 PENDING_PAY 才执行关闭
        StoreOrder.OrderStatusHistory history = new StoreOrder.OrderStatusHistory();
        history.setStatus(OrderStatusEnum.CANCELLED.getCode());
        history.setPaymentStatus(PaymentStatusEnum.CLOSED.getCode());
        history.setOperator("SYSTEM");
        history.setRemark("超时未支付，系统自动关闭");
        history.setCreateTime(new Date());

        Boolean updated = storeOrderDao.updateOneDocument(
                Criteria.where("orderId").is(order.getOrderId())
                        .and("status").is(OrderStatusEnum.PENDING_PAY.getCode()),
                new Update()
                        .set("status", OrderStatusEnum.CANCELLED.getCode())
                        .set("paymentStatus", PaymentStatusEnum.CLOSED.getCode())
                        .push("statusHistories", history));
        if (!updated) {
            // 订单状态已被其他操作变更（如用户刚好在此刻支付或手动取消），跳过
            log.info("超时订单状态已变更，跳过关闭，orderId={}，currentStatus={}", order.getOrderId(), order.getStatus());
            return false;
        }
        // [新增 2026-07-31 18:04] 混合支付订单关闭时退还已扣减的积分
        paymentService.refundPointsForMixedOrder(order);
        // 恢复库存
        storeGoodsService.restoreStock(order.getGoodsId(), order.getCount());
        log.info("超时订单自动关闭成功，orderId={}，orderNum={}，buyerUserId={}",
                order.getOrderId(), order.getOrderNum(), order.getBuyerUserId());
        return true;
    }
}
