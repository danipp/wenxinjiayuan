package com.demo.weixin.controller.store;

import com.demo.common.core.result.Result;
import com.demo.weixin.annotation.NeedLogin;
import com.demo.weixin.controller.BaseController;
import com.demo.weixin.entity.store.StoreGoods;
import com.demo.weixin.entity.store.StoreShop;
import com.demo.weixin.service.store.StoreGoodsService;
import com.demo.weixin.service.store.StoreShopService;
import com.demo.weixin.vo.store.ShopCreateVO;
import com.demo.weixin.vo.store.ShopPageQueryVO;
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
 * 商城-店铺控制器
 * 提供店铺详情、创建编辑、店铺商品列表等接口。
 */
@RestController
@Tag(name = "商城-店铺")
@RequestMapping("/api/store/shop")
@Slf4j
public class StoreShopController extends BaseController {

    @Autowired
    private StoreShopService storeShopService;
    @Autowired
    private StoreGoodsService storeGoodsService;

    @GetMapping("/detail/{shopId}")
    @Operation(summary = "店铺详情",
            responses = {@ApiResponse(description = "成功信息", content = @Content(schema = @Schema(implementation = StoreShop.class)))})
    @NeedLogin
    public Result<StoreShop> detail(@PathVariable Long shopId) {
        return Result.success(storeShopService.getShopDetail(shopId));
    }

    /**
     * 店铺分页列表（C端）
     * [新增 2026-08-03 18:55] 支持关键词搜索、社区隔离、状态过滤，按月销量降序排列
     * demo: POST /api/store/shop/page
     */
    @PostMapping("/page")
    @Operation(summary = "店铺分页列表（C端）",
            description = "demo: POST /api/store/shop/page，按月销量降序排列，支持关键词搜索和社区隔离",
            responses = {@ApiResponse(description = "成功信息", content = @Content(schema = @Schema(implementation = StoreShop.class)))})
    @NeedLogin
    public Result<Page<StoreShop>> page(@RequestBody ShopPageQueryVO queryVO) {
        queryVO.setPageNumber(queryVO.getPageNumber() - 1);

        if (queryVO.getPageNumber() < 0) {

            queryVO.setPageNumber(0);

        }
        Page<StoreShop> page = storeShopService.getShopPage(queryVO);
        return Result.success(page, page.getTotalElements());
    }

    @PostMapping("/save")
    @Operation(summary = "创建/编辑店铺（卖家）",
            responses = {@ApiResponse(description = "成功信息", content = @Content(schema = @Schema(implementation = StoreShop.class)))})
    @NeedLogin
    public Result<StoreShop> save(@RequestBody @Valid ShopCreateVO vo) {
        return Result.success(storeShopService.saveOrUpdateShop(getCurrentUserId(), vo));
    }

    @GetMapping("/my")
    @Operation(summary = "获取我的店铺",
            responses = {@ApiResponse(description = "成功信息", content = @Content(schema = @Schema(implementation = StoreShop.class)))})
    @NeedLogin
    public Result<StoreShop> myShop() {
        StoreShop shop = storeShopService.getShopBySeller(getCurrentUserId());
        return Result.success(shop != null ? shop : new StoreShop());
    }

    @GetMapping("/{shopId}/goods")
    @Operation(summary = "店铺商品列表",
            responses = {@ApiResponse(description = "成功信息", content = @Content(schema = @Schema(implementation = StoreGoods.class)))})
    @NeedLogin
    public Result<List<StoreGoods>> shopGoods(@PathVariable Long shopId) {
        return Result.success(storeGoodsService.queryShopGoods(shopId));
    }

    @PostMapping("/toggleStatus/{shopId}")
    @Operation(summary = "店铺营业状态切换（卖家）",
            responses = {@ApiResponse(description = "成功信息")})
    @NeedLogin
    public Result<Void> toggleStatus(@PathVariable Long shopId) {
        storeShopService.toggleStatus(shopId, getCurrentUserId());
        return Result.success();
    }
}
