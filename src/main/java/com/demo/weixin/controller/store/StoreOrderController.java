package com.demo.weixin.controller.store;

import com.demo.common.core.result.Result;
import com.demo.weixin.annotation.DistributedIdempotent;
import com.demo.weixin.annotation.ManageAuditLog;
import com.demo.weixin.annotation.NeedLogin;
import com.demo.weixin.constant.Constants;
import com.demo.weixin.controller.BaseController;
import com.demo.weixin.entity.store.StoreOrder;
import com.demo.weixin.service.store.StoreOrderService;
import com.demo.weixin.vo.store.OrderCreateVO;
import com.demo.weixin.vo.store.OrderQueryVO;
import com.demo.weixin.vo.store.OrderRefundVO;
import com.demo.weixin.vo.store.OrderVerifyVO;
import com.demo.weixin.vo.store.PayResultVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

/**
 * 商城-订单控制器
 * <p>
 * 订单全生命周期接口：创建→支付→核销→完成 / 退款→已退款 / 取消。
 * 创建订单、核销订单、退款处理使用 @DistributedIdempotent 防止并发重复操作。
 * 退款同意/拒绝使用 @ManageAuditLog 记录审计日志。
 * </p>
 */
@RestController
@Tag(name = "商城-订单")
@RequestMapping("/api/store/order")
@Slf4j
public class StoreOrderController extends BaseController {

    @Autowired
    private StoreOrderService storeOrderService;

    @PostMapping("/create")
    @Operation(summary = "创建订单（积分兑换同步完成，现金购买返回微信支付参数）",
            responses = {@ApiResponse(description = "成功信息", content = @Content(schema = @Schema(implementation = PayResultVO.class)))})
    @NeedLogin
    @DistributedIdempotent(prefix = Constants.LOCK_STORE_CREATE_ORDER,
            message = "下单请求正在火速处理中，请不要高频连击")
    public Result<PayResultVO> create(@RequestBody @Valid OrderCreateVO vo) {
        PayResultVO result = storeOrderService.createOrder(getCurrentUserId(), vo);
        return Result.success(result);
    }

    @PostMapping("/verify")
    @Operation(summary = "卖家核销订单",
            responses = {@ApiResponse(description = "成功信息", content = @Content(schema = @Schema(implementation = StoreOrder.class)))})
    @NeedLogin
    @DistributedIdempotent(prefix = Constants.LOCK_STORE_VERIFY,
            key = "#vo.orderId",
            message = "核销请求正在处理中，请不要高频连击")
    @ManageAuditLog(module = "商城订单", action = "核销订单")
    public Result<StoreOrder> verify(@RequestBody @Valid OrderVerifyVO vo) {
        return Result.success(storeOrderService.verifyOrder(getCurrentUserId(), vo));
    }

    @PostMapping("/refund")
    @Operation(summary = "买家申请退款",
            responses = {@ApiResponse(description = "成功信息", content = @Content(schema = @Schema(implementation = StoreOrder.class)))})
    @NeedLogin
    @DistributedIdempotent(prefix = Constants.LOCK_STORE_REQUEST_REFUND,
            key = "#vo.orderId",
            message = "退款申请正在处理中，请不要高频连击")
    public Result<StoreOrder> refund(@RequestBody @Valid OrderRefundVO vo) {
        return Result.success(storeOrderService.requestRefund(getCurrentUserId(), vo));
    }

    @PostMapping("/approveRefund/{orderId}")
    @Operation(summary = "卖家同意退款（自动执行退款处理）",
            responses = {@ApiResponse(description = "成功信息", content = @Content(schema = @Schema(implementation = StoreOrder.class)))})
    @NeedLogin
    @DistributedIdempotent(prefix = Constants.LOCK_STORE_REFUND,
            key = "#orderId",
            message = "退款处理中，请不要高频连击")
    @ManageAuditLog(module = "商城订单", action = "同意退款")
    public Result<StoreOrder> approveRefund(@PathVariable Long orderId) {
        return Result.success(storeOrderService.approveRefund(getCurrentUserId(), orderId));
    }

    @PostMapping("/rejectRefund/{orderId}")
    @Operation(summary = "卖家拒绝退款（订单恢复待核销）",
            responses = {@ApiResponse(description = "成功信息", content = @Content(schema = @Schema(implementation = StoreOrder.class)))})
    @NeedLogin
    @DistributedIdempotent(prefix = Constants.LOCK_STORE_REJECT_REFUND,
            key = "#orderId",
            message = "拒绝退款处理中，请不要高频连击")
    @ManageAuditLog(module = "商城订单", action = "拒绝退款")
    public Result<StoreOrder> rejectRefund(@PathVariable Long orderId) {
        return Result.success(storeOrderService.rejectRefund(getCurrentUserId(), orderId));
    }

    @PostMapping("/cancel/{orderId}")
    @Operation(summary = "买家取消订单（仅限待支付状态）",
            responses = {@ApiResponse(description = "成功信息", content = @Content(schema = @Schema(implementation = StoreOrder.class)))})
    @NeedLogin
    @DistributedIdempotent(prefix = Constants.LOCK_STORE_CANCEL,
            key = "#orderId",
            message = "取消订单处理中，请不要高频连击")
    @ManageAuditLog(module = "商城订单", action = "取消订单")
    public Result<StoreOrder> cancel(@PathVariable Long orderId) {
        return Result.success(storeOrderService.cancelOrder(getCurrentUserId(), orderId));
    }

    @PostMapping("/page")
    @Operation(summary = "分页查询订单（通过role区分买家/卖家视角）",
            responses = {@ApiResponse(description = "成功信息", content = @Content(schema = @Schema(implementation = StoreOrder.class)))})
    @NeedLogin
    public Result<Page<StoreOrder>> page(@RequestBody @Valid OrderQueryVO queryVO) {
        queryVO.setPageNumber(queryVO.getPageNumber() - 1);
        if (queryVO.getPageNumber() < 0) {
            queryVO.setPageNumber(0);
        }
        Page<StoreOrder> page = storeOrderService.queryOrderPage(getCurrentUserId(), queryVO);
        return Result.success(page, page.getTotalElements());
    }

    @GetMapping("/detail/{orderId}")
    @Operation(summary = "订单详情",
            responses = {@ApiResponse(description = "成功信息", content = @Content(schema = @Schema(implementation = StoreOrder.class)))})
    @NeedLogin
    public Result<StoreOrder> detail(@PathVariable Long orderId) {
        return Result.success(storeOrderService.getOrderDetail(orderId, getCurrentUserId()));
    }

    @GetMapping("/redeemCode/{orderId}")
    @Operation(summary = "买家获取核销码",
            responses = {@ApiResponse(description = "成功信息", content = @Content(schema = @Schema(implementation = String.class)))})
    @NeedLogin
    public Result<String> redeemCode(@PathVariable Long orderId) {
        return Result.success(storeOrderService.getRedeemCode(getCurrentUserId(), orderId));
    }

    @GetMapping("/refundStatusCodes")
    @Operation(summary = "获取退款相关状态码列表（供前端退款Tab筛选使用）",
            responses = {@ApiResponse(description = "成功信息", content = @Content(schema = @Schema(implementation = Integer.class)))})
    @NeedLogin
    public Result<java.util.List<Integer>> refundStatusCodes() {
        return Result.success(StoreOrderService.REFUND_STATUS_CODES);
    }
}
