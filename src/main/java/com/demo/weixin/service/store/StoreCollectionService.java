package com.demo.weixin.service.store;

import com.demo.weixin.dao.store.StoreCollectionDao;
import com.demo.weixin.dao.store.StoreGoodsDao;
import com.demo.weixin.dao.store.StoreShopDao;
import com.demo.weixin.entity.store.StoreCollection;
import com.demo.weixin.entity.store.StoreGoods;
import com.demo.weixin.entity.store.StoreShop;
import com.demo.weixin.enums.store.CollectionTypeEnum;
import com.demo.weixin.vo.store.CollectionToggleVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 商城收藏服务
 * 复用同一张表存储商品收藏和店铺收藏，通过 targetType 区分。
 */
@Service
@Slf4j
public class StoreCollectionService {

    @Autowired
    private StoreCollectionDao storeCollectionDao;
    @Autowired
    private StoreGoodsDao storeGoodsDao;
    @Autowired
    private StoreShopDao storeShopDao;

    /**
     * 切换收藏状态（已收藏则取消，未收藏则新增）
     * M13: 使用条件删除实现原子操作，避免查询-判断-操作之间的竞态条件。
     * 先尝试条件删除已存在的收藏记录，删除成功说明已收藏（返回false=取消），
     * 删除0条说明未收藏，则插入新记录（返回true=已收藏）。
     *
     * @return true=已收藏，false=已取消
     */
    public boolean toggleCollection(Long userId, CollectionToggleVO vo) {
        CollectionTypeEnum typeEnum = CollectionTypeEnum.getByCode(vo.getTargetType());
        if (typeEnum == null) {
            throw new com.demo.common.exception.BizException("无效的收藏类型");
        }
        // M13: 原子操作 - 先尝试条件删除（逻辑删除）已存在的收藏记录
        Criteria criteria = Criteria.where("userId").is(userId)
                .and("targetId").is(vo.getTargetId())
                .and("targetType").is(vo.getTargetType());
        Boolean deleted = storeCollectionDao.updateOneDocument(
                criteria, new Update().set("del_flag", true));
        if (Boolean.TRUE.equals(deleted)) {
            // 删除成功，说明之前已收藏，现在取消收藏
            log.info("取消收藏，userId={}，targetId={}，type={}", userId, vo.getTargetId(), typeEnum.getDesc());
            return false;
        }
        // 删除0条说明未收藏，新增收藏
        StoreCollection collection = new StoreCollection();
        collection.setUserId(userId);
        collection.setTargetId(vo.getTargetId());
        collection.setTargetType(vo.getTargetType());
        storeCollectionDao.insertDocument(collection);
        log.info("新增收藏，userId={}，targetId={}，type={}", userId, vo.getTargetId(), typeEnum.getDesc());
        return true;
    }

    /**
     * 查询用户收藏列表（分页，附带商品/店铺信息）
     * [变更 2026-08-04 11:00] 改为返回Page并批量填充被收藏对象的详情：
     * targetType=1 时填充 goods 字段，targetType=2 时填充 shop 字段。
     * 使用批量查询避免N+1问题。
     *
     * @param userId     用户ID
     * @param targetType 收藏类型（1商品 2店铺，null=全部）
     * @param page       页码（从0开始）
     * @param size       每页条数
     * @return 收藏分页结果（含被收藏对象详情）
     */
    public Page<StoreCollection> queryCollections(Long userId, Integer targetType, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc("createTime")));
        Criteria criteria = Criteria.where("userId").is(userId);
        if (targetType != null) {
            criteria.and("targetType").is(targetType);
        }
        Page<StoreCollection> pageResult = storeCollectionDao.findDocumentPage(criteria, pageable);
        List<StoreCollection> content = pageResult.getContent();
        if (content.isEmpty()) {
            return pageResult;
        }

        // 批量查询商品信息（targetType=1）
        Set<Long> goodsIds = content.stream()
                .filter(c -> c.getTargetType() != null && c.getTargetType() == 1)
                .map(StoreCollection::getTargetId)
                .collect(Collectors.toSet());
        Map<Long, StoreGoods> goodsMap = Map.of();
        if (!goodsIds.isEmpty()) {
            List<StoreGoods> goodsList = storeGoodsDao.findDocumentList(
                    Criteria.where("goodsId").in(goodsIds));
            goodsMap = goodsList.stream()
                    .collect(Collectors.toMap(StoreGoods::getGoodsId, g -> g, (a, b) -> a));
        }

        // 批量查询店铺信息（targetType=2）
        Set<Long> shopIds = content.stream()
                .filter(c -> c.getTargetType() != null && c.getTargetType() == 2)
                .map(StoreCollection::getTargetId)
                .collect(Collectors.toSet());
        Map<Long, StoreShop> shopMap = Map.of();
        if (!shopIds.isEmpty()) {
            List<StoreShop> shopList = storeShopDao.findDocumentList(
                    Criteria.where("shopId").in(shopIds));
            shopMap = shopList.stream()
                    .collect(Collectors.toMap(StoreShop::getShopId, s -> s, (a, b) -> a));
        }

        // 填充附带信息
        for (StoreCollection c : content) {
            if (c.getTargetType() != null && c.getTargetType() == 1) {
                c.setGoods(goodsMap.get(c.getTargetId()));
            } else if (c.getTargetType() != null && c.getTargetType() == 2) {
                c.setShop(shopMap.get(c.getTargetId()));
            }
        }

        return pageResult;
    }

    /**
     * 查询用户收藏总数
     */
    public long countCollections(Long userId, Integer targetType) {
        Criteria criteria = Criteria.where("userId").is(userId);
        if (targetType != null) {
            criteria.and("targetType").is(targetType);
        }
        return storeCollectionDao.count(criteria);
    }

    /**
     * 判断是否已收藏
     */
    public boolean isCollected(Long userId, Long targetId, Integer targetType) {
        StoreCollection exist = storeCollectionDao.findOne(
                Criteria.where("userId").is(userId)
                        .and("targetId").is(targetId)
                        .and("targetType").is(targetType));
        return exist != null;
    }
}
