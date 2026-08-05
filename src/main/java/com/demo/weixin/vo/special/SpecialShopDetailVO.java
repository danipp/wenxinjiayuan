package com.demo.weixin.vo.special;

import com.demo.weixin.entity.store.StoreGoods;
import com.demo.weixin.entity.store.StoreShop;
import com.demo.weixin.entity.special.ShopCoupon;
import com.demo.weixin.entity.special.ShopReview;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 社区特惠店铺详情VO
 * 包含店铺基本信息、优惠券列表、特惠服务项目（商品）、评价列表
 */
@Schema(name = "社区特惠店铺详情")
@Data
public class SpecialShopDetailVO {

    @Schema(description = "店铺信息")
    private StoreShop shopInfo;

    @Schema(description = "优惠券列表（含当前用户领取状态）")
    private List<ShopCoupon> couponList;

    @Schema(description = "特惠服务项目（店铺商品列表）")
    private List<StoreGoods> serviceItems;

    @Schema(description = "顾客评价列表")
    private List<ShopReview> reviews;

    @Schema(description = "评价总数")
    private Long reviewCount;
}
