package com.demo.weixin.controller.store;

import com.demo.common.core.result.Result;
import com.demo.weixin.annotation.ManageAuditLog;
import com.demo.weixin.annotation.NeedLogin;
import com.demo.weixin.controller.BaseController;
import com.demo.weixin.entity.store.StoreCollection;
import com.demo.weixin.entity.store.StoreGoods;
import com.demo.weixin.service.store.StoreCollectionService;
import com.demo.weixin.service.store.StoreGoodsService;
import com.demo.weixin.vo.store.CollectionToggleVO;
import com.demo.weixin.vo.store.GoodsCreateVO;
import com.demo.weixin.vo.store.GoodsQueryVO;
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

import java.util.List;

/**
 * 商城-商品控制器
 * 提供商品CRUD、上下架、收藏等C端接口。
 */
@RestController
@Tag(name = "商城-商品")
@RequestMapping("/api/store/goods")
@Slf4j
public class StoreGoodsController extends BaseController {

    @Autowired
    private StoreGoodsService storeGoodsService;
    @Autowired
    private StoreCollectionService storeCollectionService;

    @GetMapping("/detail/{goodsId}")
    @Operation(summary = "商品详情",
            responses = {@ApiResponse(description = "成功信息", content = @Content(schema = @Schema(implementation = StoreGoods.class)))})
    @NeedLogin
    public Result<StoreGoods> detail(@PathVariable Long goodsId) {
        return Result.success(storeGoodsService.getGoodsDetail(goodsId));
    }

    @PostMapping("/page")
    @Operation(summary = "分页查询商品列表",
            responses = {@ApiResponse(description = "成功信息", content = @Content(schema = @Schema(implementation = StoreGoods.class)))})
    @NeedLogin
    public Result<Page<StoreGoods>> page(@RequestBody @Valid GoodsQueryVO queryVO) {
        // 前端页码从1开始，MongoDB从0开始
        queryVO.setPageNumber(queryVO.getPageNumber() - 1);
        if (queryVO.getPageNumber() < 0) {
            queryVO.setPageNumber(0);
        }
        Page<StoreGoods> page = storeGoodsService.queryGoodsPage(queryVO);
        return Result.success(page, page.getTotalElements());
    }

    @PostMapping("/create")
    @Operation(summary = "创建商品（卖家）",
            responses = {@ApiResponse(description = "成功信息", content = @Content(schema = @Schema(implementation = StoreGoods.class)))})
    @NeedLogin
    public Result<StoreGoods> create(@RequestBody @Valid GoodsCreateVO vo) {
        return Result.success(storeGoodsService.createGoods(getCurrentUserId(), vo));
    }

    @PostMapping("/edit")
    @Operation(summary = "编辑商品（卖家）",
            responses = {@ApiResponse(description = "成功信息", content = @Content(schema = @Schema(implementation = StoreGoods.class)))})
    @NeedLogin
    public Result<StoreGoods> edit(@RequestBody @Valid GoodsCreateVO vo) {
        return Result.success(storeGoodsService.updateGoods(getCurrentUserId(), vo));
    }

    @PostMapping("/toggleStatus/{goodsId}")
    @Operation(summary = "商品上下架切换（卖家）",
            responses = {@ApiResponse(description = "成功信息")})
    @NeedLogin
    public Result<Void> toggleStatus(@PathVariable Long goodsId) {
        storeGoodsService.toggleStatus(goodsId);
        return Result.success();
    }

    @DeleteMapping("/delete/{goodsId}")
    @Operation(summary = "删除商品（卖家）",
            description = "demo: DELETE /api/store/goods/delete/123456，逻辑删除商品",
            responses = {@ApiResponse(description = "成功信息")})
    @NeedLogin
    @ManageAuditLog(module = "商城-商品", action = "删除商品")
    public Result<Void> delete(@PathVariable Long goodsId) {
        storeGoodsService.deleteGoods(goodsId);
        return Result.success();
    }

    @PostMapping("/collect")
    @Operation(summary = "收藏/取消收藏（商品或店铺）",
            responses = {@ApiResponse(description = "成功信息", content = @Content(schema = @Schema(implementation = Boolean.class)))})
    @NeedLogin
    public Result<Boolean> collect(@RequestBody @Valid CollectionToggleVO vo) {
        boolean collected = storeCollectionService.toggleCollection(getCurrentUserId(), vo);
        return Result.success(collected);
    }

    @GetMapping("/collections")
    @Operation(summary = "我的收藏列表（分页，含商品/店铺详情）",
            description = "demo: GET /api/store/goods/collections?targetType=1&page=1&size=10，targetType: 1商品 2店铺，不传则查全部。返回data.content含收藏记录，每条附带goods或shop详情",
            responses = {@ApiResponse(description = "成功信息", content = @Content(schema = @Schema(implementation = StoreCollection.class)))})
    @NeedLogin
    public Result<Page<StoreCollection>> collections(
            @RequestParam(required = false) Integer targetType,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        page = page - 1;
        if (page < 0) {
            page = 0;
        }
        Page<StoreCollection> pageResult = storeCollectionService.queryCollections(
                getCurrentUserId(), targetType, page, size);
        return Result.success(pageResult);
    }

    @GetMapping("/isCollected")
    @Operation(summary = "检查是否已收藏",
            responses = {@ApiResponse(description = "成功信息", content = @Content(schema = @Schema(implementation = Boolean.class)))})
    @NeedLogin
    public Result<Boolean> isCollected(
            @RequestParam Long targetId,
            @RequestParam Integer targetType) {
        return Result.success(storeCollectionService.isCollected(
                getCurrentUserId(), targetId, targetType));
    }
}
