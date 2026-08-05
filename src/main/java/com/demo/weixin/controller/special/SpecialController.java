package com.demo.weixin.controller.special;

import com.demo.common.core.result.Result;
import com.demo.weixin.annotation.AdminNeedLogin;
import com.demo.weixin.annotation.DistributedIdempotent;
import com.demo.weixin.annotation.ManageAuditLog;
import com.demo.weixin.annotation.NeedLogin;
import com.demo.weixin.constant.Constants;
import com.demo.weixin.controller.BaseController;
import com.demo.weixin.entity.User;
import com.demo.weixin.entity.special.ShopCoupon;
import com.demo.weixin.entity.special.ShopReview;
import com.demo.weixin.entity.special.SpecialCategory;
import com.demo.weixin.entity.store.StoreShop;
import com.demo.weixin.service.special.ShopCouponService;
import com.demo.weixin.service.special.ShopReviewService;
import com.demo.weixin.service.special.SpecialCategoryService;
import com.demo.weixin.service.special.SpecialShopService;
import com.demo.weixin.vo.special.*;
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
 * 社区特惠模块控制器
 * 包含C端接口（分类树、店铺列表、店铺详情、领券、评价）和管理端接口（分类CRUD、优惠券CRUD）
 */
@RestController
@Tag(name = "社区特惠")
@RequestMapping("/api/special")
@Slf4j
public class SpecialController extends BaseController {

    @Autowired
    private SpecialCategoryService specialCategoryService;
    @Autowired
    private SpecialShopService specialShopService;
    @Autowired
    private ShopCouponService shopCouponService;
    @Autowired
    private ShopReviewService shopReviewService;

    // ==================== C端接口 ====================

    /**
     * 获取分类树
     * 返回启用状态的一级分类及其子分类，支持社区隔离
     */
    @GetMapping("/categories")
    @Operation(summary = "获取社区特惠分类树",
            description = "demo: GET /api/special/categories?communityId=1，返回一级分类列表含子分类",
            responses = {@ApiResponse(description = "成功信息", content = @Content(schema = @Schema(implementation = SpecialCategoryVO.class)))})
    @NeedLogin
    public Result<List<SpecialCategoryVO>> getCategoryTree(
            @RequestParam(required = false) Long communityId) {
        return Result.success(specialCategoryService.getCategoryTree(communityId));
    }

    /**
     * 社区特惠店铺分页列表
     * 支持分类筛选、关键词搜索、多维排序、高评分/新品过滤
     */
    @PostMapping("/shop/list")
    @Operation(summary = "社区特惠店铺列表（分页）",
            description = "demo: POST /api/special/shop/list，{\"cat1Id\":1,\"cat2Id\":2,\"sort\":\"sales\",\"pageNumber\":1,\"pageSize\":10}",
            responses = {@ApiResponse(description = "成功信息", content = @Content(schema = @Schema(implementation = StoreShop.class)))})
    @NeedLogin
    public Result<Page<StoreShop>> shopList(@RequestBody SpecialShopQueryVO queryVO) {
        queryVO.setPageNumber(queryVO.getPageNumber() - 1);

        if (queryVO.getPageNumber() < 0) {

            queryVO.setPageNumber(0);

        }
        return Result.success(specialShopService.getShopPage(queryVO));
    }

    /**
     * 获取店铺详情
     * 包含店铺信息、优惠券列表（含领取状态）、特惠服务项目（商品）、评价列表
     */
    @GetMapping("/shop/detail/{shopId}")
    @Operation(summary = "获取社区特惠店铺详情",
            description = "demo: GET /api/special/shop/detail/401，返回店铺信息、优惠券、服务项目、评价",
            responses = {@ApiResponse(description = "成功信息", content = @Content(schema = @Schema(implementation = SpecialShopDetailVO.class)))})
    @NeedLogin
    public Result<SpecialShopDetailVO> shopDetail(@PathVariable Long shopId) {
        return Result.success(specialShopService.getShopDetail(shopId, getCurrentUserId()));
    }

    /**
     * 领取优惠券
     * 同一用户同一优惠券只能领取一次，支持幂等防重
     */
    @PostMapping("/coupon/claim/{couponId}")
    @Operation(summary = "领取店铺优惠券",
            description = "demo: POST /api/special/coupon/claim/101，领取优惠券ID=101的代金券",
            responses = {@ApiResponse(description = "成功信息", content = @Content(schema = @Schema(implementation = String.class)))})
    @NeedLogin
    @DistributedIdempotent(prefix = Constants.LOCK_SPECIAL_COUPON_CLAIM, message = "正在领取中，请勿重复点击")
    public Result<String> claimCoupon(@PathVariable Long couponId) {
        shopCouponService.claimCoupon(getCurrentUserId(), couponId);
        return Result.success("领取成功");
    }

    /**
     * 查询店铺评价列表（分页）
     */
    @PostMapping("/review/list")
    @Operation(summary = "查询店铺评价列表（分页）",
            description = "demo: POST /api/special/review/list，{\"shopId\":401,\"pageNumber\":1,\"pageSize\":10}",
            responses = {@ApiResponse(description = "成功信息", content = @Content(schema = @Schema(implementation = ShopReview.class)))})
    @NeedLogin
    public Result<Page<ShopReview>> reviewList(@RequestBody ShopReviewQueryVO queryVO) {
        queryVO.setPageNumber(queryVO.getPageNumber() - 1);

        if (queryVO.getPageNumber() < 0) {

            queryVO.setPageNumber(0);

        }
        return Result.success(shopReviewService.getReviewPage(queryVO));
    }

    /**
     * 创建店铺评价
     * 评价创建后自动更新店铺评分冗余字段
     */
    @PostMapping("/review/create")
    @Operation(summary = "创建店铺评价",
            description = "demo: POST /api/special/review/create，{\"shopId\":401,\"rating\":5,\"content\":\"服务很好\"}",
            responses = {@ApiResponse(description = "成功信息", content = @Content(schema = @Schema(implementation = ShopReview.class)))})
    @NeedLogin
    @DistributedIdempotent(prefix = Constants.LOCK_SPECIAL_REVIEW_CREATE, message = "评价提交中，请勿重复点击")
    public Result<ShopReview> createReview(@RequestBody @Valid ShopReviewCreateVO vo) {
        User user = getUser();
        return Result.success(shopReviewService.createReview(
                user.getUserId(), user.getNickName(), user.getAvatar(), vo));
    }

    // ==================== 管理端接口 ====================

    /**
     * 管理端：创建或编辑分类
     */
    @PostMapping("/admin/category/save")
    @Operation(summary = "管理端-创建或编辑社区特惠分类",
            description = "demo: POST /api/special/admin/category/save，{\"parentId\":0,\"name\":\"特惠好物\",\"icon\":\"url\",\"sort\":1}",
            responses = {@ApiResponse(description = "成功信息", content = @Content(schema = @Schema(implementation = SpecialCategory.class)))})
    @AdminNeedLogin
    @ManageAuditLog(module = "社区特惠-分类管理", action = "保存分类")
    @DistributedIdempotent(prefix = Constants.LOCK_SPECIAL_CATEGORY_SAVE, message = "分类保存中，请勿重复提交")
    public Result<SpecialCategory> saveCategory(@RequestBody SpecialCategory category) {
        return Result.success(specialCategoryService.saveOrUpdate(category));
    }

    /**
     * 管理端：查询分类列表
     */
    @GetMapping("/admin/category/list")
    @Operation(summary = "管理端-查询分类列表",
            description = "demo: GET /api/special/admin/category/list?communityId=1",
            responses = {@ApiResponse(description = "成功信息", content = @Content(schema = @Schema(implementation = SpecialCategory.class)))})
    @AdminNeedLogin
    public Result<List<SpecialCategory>> categoryList(@RequestParam(required = false) Long communityId) {
        return Result.success(specialCategoryService.getList(communityId));
    }

    /**
     * 管理端：删除分类
     */
    @DeleteMapping("/admin/category/delete/{categoryId}")
    @Operation(summary = "管理端-删除分类",
            description = "demo: DELETE /api/special/admin/category/delete/1",
            responses = {@ApiResponse(description = "成功信息", content = @Content(schema = @Schema(implementation = Void.class)))})
    @AdminNeedLogin
    @ManageAuditLog(module = "社区特惠-分类管理", action = "删除分类")
    public Result<Void> deleteCategory(@PathVariable Long categoryId) {
        specialCategoryService.delete(categoryId);
        return Result.success();
    }

    /**
     * 管理端：创建或编辑优惠券
     */
    @PostMapping("/admin/coupon/save")
    @Operation(summary = "管理端-创建或编辑优惠券",
            description = "demo: POST /api/special/admin/coupon/save，{\"shopId\":401,\"title\":\"满100减10\",\"money\":10,\"minSpend\":100,\"total\":100}",
            responses = {@ApiResponse(description = "成功信息", content = @Content(schema = @Schema(implementation = ShopCoupon.class)))})
    @AdminNeedLogin
    @ManageAuditLog(module = "社区特惠-优惠券管理", action = "保存优惠券")
    @DistributedIdempotent(prefix = Constants.LOCK_SPECIAL_COUPON_SAVE, message = "优惠券保存中，请勿重复提交")
    public Result<ShopCoupon> saveCoupon(@RequestBody @Valid ShopCouponCreateVO vo) {
        return Result.success(shopCouponService.saveOrUpdate(vo));
    }

    /**
     * 管理端：查询优惠券列表
     */
    @GetMapping("/admin/coupon/list")
    @Operation(summary = "管理端-查询优惠券列表",
            description = "demo: GET /api/special/admin/coupon/list?shopId=401",
            responses = {@ApiResponse(description = "成功信息", content = @Content(schema = @Schema(implementation = ShopCoupon.class)))})
    @AdminNeedLogin
    public Result<List<ShopCoupon>> couponList(@RequestParam(required = false) Long shopId) {
        return Result.success(shopCouponService.getList(shopId));
    }

    /**
     * 管理端：删除优惠券
     */
    @DeleteMapping("/admin/coupon/delete/{couponId}")
    @Operation(summary = "管理端-删除优惠券",
            description = "demo: DELETE /api/special/admin/coupon/delete/1",
            responses = {@ApiResponse(description = "成功信息", content = @Content(schema = @Schema(implementation = Void.class)))})
    @AdminNeedLogin
    @ManageAuditLog(module = "社区特惠-优惠券管理", action = "删除优惠券")
    public Result<Void> deleteCoupon(@PathVariable Long couponId) {
        shopCouponService.delete(couponId);
        return Result.success();
    }
}
