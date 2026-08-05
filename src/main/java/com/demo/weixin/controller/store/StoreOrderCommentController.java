package com.demo.weixin.controller.store;

import com.demo.common.core.result.Result;
import com.demo.weixin.annotation.DistributedIdempotent;
import com.demo.weixin.annotation.NeedLogin;
import com.demo.weixin.constant.Constants;
import com.demo.weixin.controller.BaseController;
import com.demo.weixin.entity.store.StoreOrderComment;
import com.demo.weixin.service.store.StoreOrderCommentService;
import com.demo.weixin.vo.store.CommentCreateVO;
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
 * 商城-订单评价控制器
 */
@RestController
@Tag(name = "商城-订单评价")
@RequestMapping("/api/store/comment")
@Slf4j
public class StoreOrderCommentController extends BaseController {

    @Autowired
    private StoreOrderCommentService storeOrderCommentService;

    @PostMapping("/create")
    @Operation(summary = "创建订单评价",
            responses = {@ApiResponse(description = "成功信息", content = @Content(schema = @Schema(implementation = StoreOrderComment.class)))})
    @NeedLogin
    @DistributedIdempotent(prefix = Constants.LOCK_STORE_COMMENT,
            key = "#vo.orderId",
            message = "评价提交中，请不要高频连击")
    public Result<StoreOrderComment> create(@RequestBody @Valid CommentCreateVO vo) {
        return Result.success(storeOrderCommentService.createComment(getCurrentUserId(), vo));
    }

    @GetMapping("/goodsPage/{goodsId}")
    @Operation(summary = "商品评价列表（分页）",
            responses = {@ApiResponse(description = "成功信息", content = @Content(schema = @Schema(implementation = StoreOrderComment.class)))})
    @NeedLogin
    public Result<Page<StoreOrderComment>> goodsPage(
            @PathVariable Long goodsId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        page = page - 1;
        if (page < 0) {
            page = 0;
        }
        Page<StoreOrderComment> pageResult = storeOrderCommentService.queryGoodsComments(goodsId, page, size);
        return Result.success(pageResult, pageResult.getTotalElements());
    }
}
