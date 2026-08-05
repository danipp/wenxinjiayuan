package com.demo.weixin.service.special;

import com.demo.weixin.dao.store.StoreShopDao;
import com.demo.weixin.entity.store.StoreGoods;
import com.demo.weixin.entity.store.StoreShop;
import com.demo.weixin.entity.special.ShopCoupon;
import com.demo.weixin.entity.special.ShopReview;
import com.demo.weixin.service.store.StoreGoodsService;
import com.demo.weixin.service.store.StoreShopService;
import com.demo.weixin.vo.special.SpecialShopDetailVO;
import com.demo.weixin.vo.special.SpecialShopQueryVO;
import com.demo.common.exception.BizException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 社区特惠店铺服务
 * 提供分类筛选、排序、分页查询店铺列表，以及店铺详情聚合查询
 */
@Service
@Slf4j
public class SpecialShopService {

    @Autowired
    private StoreShopDao storeShopDao;
    @Autowired
    private StoreShopService storeShopService;
    @Autowired
    private StoreGoodsService storeGoodsService;
    @Autowired
    private ShopCouponService shopCouponService;
    @Autowired
    private ShopReviewService shopReviewService;

    /** 新品判定天数（7天内创建的店铺标记为新品） */
    private static final int NEW_SHOP_DAYS = 7;
    /** 高评分阈值 */
    private static final double HIGH_RATING_THRESHOLD = 4.8;
    /** 详情页评价展示条数 */
    private static final int DETAIL_REVIEW_LIMIT = 5;

    /**
     * 社区特惠店铺分页查询
     * 支持分类筛选、关键词搜索、多维排序、高评分/新品过滤
     *
     * @param queryVO 查询入参
     * @return 店铺分页结果
     */
    public Page<StoreShop> getShopPage(SpecialShopQueryVO queryVO) {
        int pageIndex = (queryVO.getPageNumber() != null && queryVO.getPageNumber() > 0)
                ? queryVO.getPageNumber() - 1 : 0;
        int pageSize = (queryVO.getPageSize() != null && queryVO.getPageSize() > 0)
                ? queryVO.getPageSize() : 10;

        // 构建排序
        Sort sort = buildSort(queryVO.getSort());
        PageRequest pageable = PageRequest.of(pageIndex, pageSize, sort);

        // 构建查询条件
        Criteria criteria = new Criteria();
        // C端只展示营业中的店铺
        criteria.and("status").is(1);
        // 社区数据隔离
        if (queryVO.getCommunityId() != null) {
            criteria.and("communityId").is(queryVO.getCommunityId());
        }
        // 一级分类筛选
        if (queryVO.getCat1Id() != null) {
            criteria.and("cat1Id").is(queryVO.getCat1Id());
        }
        // 二级分类筛选
        if (queryVO.getCat2Id() != null) {
            criteria.and("cat2Id").is(queryVO.getCat2Id());
        }
        // 关键词模糊搜索
        if (queryVO.getKeyword() != null && !queryVO.getKeyword().trim().isEmpty()) {
            java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(
                    queryVO.getKeyword(), java.util.regex.Pattern.CASE_INSENSITIVE);
            criteria.and("name").regex(pattern);
        }
        // 高评分过滤（>=4.8）
        if (Boolean.TRUE.equals(queryVO.getHighRating())) {
            criteria.and("rating").gte(HIGH_RATING_THRESHOLD);
        }
        // 新品过滤（7天内创建）
        if (Boolean.TRUE.equals(queryVO.getIsNew())) {
            Date sevenDaysAgo = getDaysAgo(NEW_SHOP_DAYS);
            criteria.and("createTime").gte(sevenDaysAgo);
        }

        Page<StoreShop> page = storeShopDao.findDocumentPage(criteria, pageable);
        // 填充 isNew 标记
        for (StoreShop shop : page.getContent()) {
            shop.setIsNew(computeIsNew(shop.getCreateTime()));
        }
        return page;
    }

    /**
     * 构建排序条件
     */
    private Sort buildSort(String sortStr) {
        if (sortStr == null || sortStr.isEmpty()) {
            // 默认按月销量降序、创建时间降序
            return Sort.by(Sort.Order.desc("monthlySales"), Sort.Order.desc("createTime"));
        }
        switch (sortStr) {
            case "price_asc":
                return Sort.by(Sort.Order.asc("startPrice"));
            case "price_desc":
                return Sort.by(Sort.Order.desc("startPrice"));
            case "rating":
                return Sort.by(Sort.Order.desc("rating"));
            case "sales":
                return Sort.by(Sort.Order.desc("monthlySales"));
            default:
                return Sort.by(Sort.Order.desc("monthlySales"), Sort.Order.desc("createTime"));
        }
    }

    /**
     * 获取店铺详情（聚合查询）
     * 包含店铺信息、优惠券列表、特惠服务项目（商品）、评价列表
     *
     * @param shopId 店铺ID
     * @param userId 当前用户ID（用于标记优惠券领取状态）
     * @return 店铺详情VO
     */
    public SpecialShopDetailVO getShopDetail(Long shopId, Long userId) {
        StoreShop shop = storeShopService.getShopDetail(shopId);
        shop.setIsNew(computeIsNew(shop.getCreateTime()));

        SpecialShopDetailVO vo = new SpecialShopDetailVO();
        vo.setShopInfo(shop);

        // 查询优惠券列表（标记当前用户领取状态）
        List<ShopCoupon> coupons = shopCouponService.getActiveCouponsByShopId(shopId, userId);
        vo.setCouponList(coupons);

        // 查询特惠服务项目（店铺上架商品）
        List<StoreGoods> goods = storeGoodsService.queryShopGoods(shopId);
        vo.setServiceItems(goods);

        // 查询评价（最近5条）
        List<ShopReview> reviews = shopReviewService.getRecentReviews(shopId, DETAIL_REVIEW_LIMIT);
        vo.setReviews(reviews);
        vo.setReviewCount(shopReviewService.countByShopId(shopId));

        return vo;
    }

    /**
     * 计算是否新品（7天内创建）
     */
    private Boolean computeIsNew(Date createTime) {
        if (createTime == null) {
            return false;
        }
        Date sevenDaysAgo = getDaysAgo(NEW_SHOP_DAYS);
        return createTime.after(sevenDaysAgo);
    }

    /**
     * 获取N天前的日期
     */
    private Date getDaysAgo(int days) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -days);
        return cal.getTime();
    }
}
