package com.demo.weixin.service.special;

import com.demo.weixin.dao.special.ShopReviewDao;
import com.demo.weixin.entity.special.ShopReview;
import com.demo.weixin.entity.store.StoreShop;
import com.demo.weixin.service.store.StoreShopService;
import com.demo.weixin.vo.special.ShopReviewCreateVO;
import com.demo.weixin.vo.special.ShopReviewQueryVO;
import com.demo.common.exception.BizException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 店铺评价服务
 * 用户对社区特惠店铺进行评价，评价后自动更新店铺评分冗余字段
 */
@Service
@Slf4j
public class ShopReviewService {

    @Autowired
    private ShopReviewDao shopReviewDao;
    @Autowired
    private StoreShopService storeShopService;

    /**
     * 创建店铺评价
     * 评价创建后自动更新店铺评分（冗余字段）
     *
     * @param userId   评价用户ID
     * @param userNickName 用户昵称
     * @param userAvatar 用户头像
     * @param vo       评价入参
     * @return 创建的评价
     */
    public ShopReview createReview(Long userId, String userNickName, String userAvatar, ShopReviewCreateVO vo) {
        // 校验店铺存在性
        StoreShop shop = storeShopService.getShopDetail(vo.getShopId());

        ShopReview review = new ShopReview();
        review.setShopId(vo.getShopId());
        review.setUserId(userId);
        review.setUserName(userNickName);
        review.setUserAvatar(userAvatar);
        review.setRating(vo.getRating());
        review.setContent(vo.getContent());
        review.setImages(vo.getImages());
        review.setCommunityId(shop.getCommunityId());
        review = shopReviewDao.insertDocument(review);

        // 异步更新店铺评分冗余字段
        updateShopRating(vo.getShopId());
        log.info("创建店铺评价，reviewId={}，shopId={}，userId={}，rating={}",
                review.getReviewId(), vo.getShopId(), userId, vo.getRating());
        return review;
    }

    /**
     * 查询店铺评价列表（分页）
     *
     * @param queryVO 查询入参
     * @return 评价分页结果
     */
    public Page<ShopReview> getReviewPage(ShopReviewQueryVO queryVO) {
        int pageIndex = (queryVO.getPageNumber() != null && queryVO.getPageNumber() > 0)
                ? queryVO.getPageNumber() - 1 : 0;
        int pageSize = (queryVO.getPageSize() != null && queryVO.getPageSize() > 0)
                ? queryVO.getPageSize() : 10;

        PageRequest pageable = PageRequest.of(pageIndex, pageSize,
                Sort.by(Sort.Order.desc("createTime")));

        Criteria criteria = new Criteria();
        if (queryVO.getShopId() != null) {
            criteria.and("shopId").is(queryVO.getShopId());
        }
        if (queryVO.getCommunityId() != null) {
            criteria.and("communityId").is(queryVO.getCommunityId());
        }

        return shopReviewDao.findDocumentPage(criteria, pageable);
    }

    /**
     * 查询店铺的最近评价（用于店铺详情页，取最新5条）
     *
     * @param shopId 店铺ID
     * @param limit  返回条数
     * @return 评价列表
     */
    public List<ShopReview> getRecentReviews(Long shopId, int limit) {
        return shopReviewDao.findDocumentList(
                Criteria.where("shopId").is(shopId),
                Sort.by(Sort.Order.desc("createTime")),
                limit);
    }

    /**
     * 统计店铺评价总数
     *
     * @param shopId 店铺ID
     * @return 评价总数
     */
    public long countByShopId(Long shopId) {
        return shopReviewDao.count(Criteria.where("shopId").is(shopId));
    }

    /**
     * 更新店铺评分冗余字段
     * 计算店铺所有评价的平均评分，更新到 StoreShop.rating
     *
     * @param shopId 店铺ID
     */
    private void updateShopRating(Long shopId) {
        try {
            List<ShopReview> reviews = shopReviewDao.findDocumentList(
                    Criteria.where("shopId").is(shopId));
            if (reviews.isEmpty()) {
                return;
            }
            double avgRating = reviews.stream()
                    .mapToInt(ShopReview::getRating)
                    .average()
                    .orElse(0.0);
            // 保留一位小数
            double rounded = Math.round(avgRating * 10.0) / 10.0;
            storeShopService.updateShopRating(shopId, rounded);
        } catch (Exception e) {
            log.warn("更新店铺评分失败，shopId={}", shopId, e);
        }
    }
}
