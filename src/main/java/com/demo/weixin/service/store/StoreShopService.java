package com.demo.weixin.service.store;

import cn.hutool.core.util.StrUtil;
import com.demo.common.exception.BizException;
import com.demo.weixin.dao.store.StoreShopDao;
import com.demo.weixin.entity.store.StoreShop;
import com.demo.weixin.vo.store.ShopCreateVO;
import com.demo.weixin.vo.store.ShopPageQueryVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

/**
 * 商城店铺服务
 */
@Service
@Slf4j
public class StoreShopService {

    @Autowired
    private StoreShopDao storeShopDao;

    /**
     * 创建或编辑店铺（每个卖家只能有一个店铺）
     */
    public StoreShop saveOrUpdateShop(Long sellerUserId, ShopCreateVO vo) {
        if (StrUtil.isBlank(vo.getName())) {
            throw new BizException("店铺名称不能为空");
        }
        // 编辑已有店铺
        if (vo.getShopId() != null) {
            StoreShop shop = storeShopDao.findById(vo.getShopId());
            if (shop == null) {
                throw new BizException("店铺不存在");
            }
            if (!shop.getSellerUserId().equals(sellerUserId)) {
                throw new BizException("无权编辑他人店铺");
            }
            shop.setName(vo.getName());
            shop.setLogo(vo.getLogo());
            shop.setPhone(vo.getPhone());
            shop.setAddress(vo.getAddress());
            shop.setDescription(vo.getDescription());
            // [新增 2026-08-03 17:40] 更新社区ID
            shop.setCommunityId(vo.getCommunityId());
            storeShopDao.saveOrUpdate(shop);
            return shop;
        }
        // 新建店铺：检查是否已有店铺
        StoreShop existShop = storeShopDao.findOne(Criteria.where("sellerUserId").is(sellerUserId));
        if (existShop != null) {
            throw new BizException("每个卖家只能创建一个店铺");
        }
        StoreShop shop = new StoreShop();
        shop.setSellerUserId(sellerUserId);
        shop.setName(vo.getName());
        shop.setLogo(vo.getLogo());
        shop.setPhone(vo.getPhone());
        shop.setAddress(vo.getAddress());
        shop.setDescription(vo.getDescription());
        shop.setStatus(1);
        shop.setGoodsCount(0);
        // [新增 2026-07-31 17:21] 初始化统计冗余字段
        shop.setFollowCount(0);
        shop.setFansCount(0);
        shop.setMonthlySales(0);
        // [新增 2026-08-03 17:40] 设置社区ID用于数据隔离
        shop.setCommunityId(vo.getCommunityId());
        storeShopDao.insertDocument(shop);
        log.info("创建店铺，shopId={}，sellerUserId={}", shop.getShopId(), sellerUserId);
        return shop;
    }

    /**
     * 获取店铺详情
     */
    public StoreShop getShopDetail(Long shopId) {
        StoreShop shop = storeShopDao.findById(shopId);
        if (shop == null) {
            throw new BizException("店铺不存在");
        }
        return shop;
    }

    /**
     * 根据卖家用户ID获取店铺
     */
    public StoreShop getShopBySeller(Long sellerUserId) {
        return storeShopDao.findOne(Criteria.where("sellerUserId").is(sellerUserId));
    }

    /**
     * 店铺分页查询（C端列表）
     * [新增 2026-08-03 18:55] 支持关键词搜索、社区隔离、状态过滤
     *
     * @param queryVO 查询入参
     * @return 分页结果
     */
    public Page<StoreShop> getShopPage(ShopPageQueryVO queryVO) {
        // 前端页码从1开始，MongoDB从0开始
        int pageIndex = (queryVO.getPageNumber() != null && queryVO.getPageNumber() > 0)
                ? queryVO.getPageNumber() - 1 : 0;
        int pageSize = (queryVO.getPageSize() != null && queryVO.getPageSize() > 0)
                ? queryVO.getPageSize() : 20;

        PageRequest pageable = PageRequest.of(pageIndex, pageSize,
                Sort.by(Sort.Order.desc("monthlySales"), Sort.Order.desc("createTime")));

        Criteria criteria = new Criteria();
        // 社区数据隔离
        if (queryVO.getCommunityId() != null) {
            criteria.and("communityId").is(queryVO.getCommunityId());
        }
        // 店铺状态过滤
        if (queryVO.getStatus() != null) {
            criteria.and("status").is(queryVO.getStatus());
        } else {
            // C端默认只展示营业中的店铺
            criteria.and("status").is(1);
        }
        // 关键词模糊搜索（店铺名称）
        if (StrUtil.isNotBlank(queryVO.getKeyword())) {
            Pattern pattern = Pattern.compile(queryVO.getKeyword(),
                    java.util.regex.Pattern.CASE_INSENSITIVE);
            criteria.and("name").regex(pattern);
        }

        return storeShopDao.findDocumentPage(criteria, pageable);
    }

    /**
     * 更新店铺商品数量（冗余字段同步）
     */
    public void updateGoodsCount(Long shopId, int count) {
        storeShopDao.updateOneDocument(
                Criteria.where("shopId").is(shopId),
                new Update().inc("goodsCount", count));
    }

    // [新增 2026-07-31 17:21] 关注/粉丝/月销量冗余字段更新方法

    /**
     * 更新卖家店铺的关注数（卖家关注了某人时 +1，取关时 -1）
     * 关注数 = FollowRecord 中 followerUserId=sellerUserId 的记录数
     *
     * @param sellerUserId 卖家用户ID
     * @param delta        变化量（+1 或 -1）
     */
    public void updateFollowCount(Long sellerUserId, int delta) {
        StoreShop shop = getShopBySeller(sellerUserId);
        if (shop == null) {
            // 用户没有店铺，无需更新
            return;
        }
        storeShopDao.updateOneDocument(
                Criteria.where("shopId").is(shop.getShopId()),
                new Update().inc("followCount", delta));
    }

    /**
     * 更新卖家店铺的粉丝数（被人关注时 +1，被人取关时 -1）
     * 粉丝数 = FollowRecord 中 targetUserId=sellerUserId 的记录数
     * 因关注=收藏，粉丝数同时代表收藏数
     *
     * @param sellerUserId 卖家用户ID（被关注者）
     * @param delta        变化量（+1 或 -1）
     */
    public void updateFansCount(Long sellerUserId, int delta) {
        StoreShop shop = getShopBySeller(sellerUserId);
        if (shop == null) {
            return;
        }
        storeShopDao.updateOneDocument(
                Criteria.where("shopId").is(shop.getShopId()),
                new Update().inc("fansCount", delta));
    }

    /**
     * 更新店铺月销量（订单核销完成时调用）
     *
     * @param shopId 店铺ID
     * @param count  销量增量（通常为订单购买数量）
     */
    public void updateMonthlySales(Long shopId, int count) {
        if (shopId == null) {
            return;
        }
        storeShopDao.updateOneDocument(
                Criteria.where("shopId").is(shopId),
                new Update().inc("monthlySales", count));
    }

    /**
     * 更新店铺评分（冗余字段）
     * [新增 2026-08-04 10:00] 评价创建后由 ShopReviewService 调用，更新平均评分
     *
     * @param shopId 店铺ID
     * @param rating 评分值
     */
    public void updateShopRating(Long shopId, double rating) {
        if (shopId == null) {
            return;
        }
        storeShopDao.updateOneDocument(
                Criteria.where("shopId").is(shopId),
                new Update().set("rating", rating));
    }

    /**
     * 店铺上下架状态切换
     */
    public void toggleStatus(Long shopId, Long sellerUserId) {
        StoreShop shop = storeShopDao.findById(shopId);
        if (shop == null) {
            throw new BizException("店铺不存在");
        }
        if (!shop.getSellerUserId().equals(sellerUserId)) {
            throw new BizException("无权操作他人店铺");
        }
        // M8: 使用Integer.valueOf避免拆箱NPE
        Integer newStatus = Integer.valueOf(1).equals(shop.getStatus()) ? 2 : 1;
        storeShopDao.updateOneDocument(
                Criteria.where("shopId").is(shopId),
                new Update().set("status", newStatus));
    }
}
