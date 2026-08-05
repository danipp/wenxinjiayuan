package com.demo.weixin.service.store;

import cn.hutool.core.util.StrUtil;
import com.demo.common.exception.BizException;
import com.demo.weixin.dao.store.StoreGoodsDao;
import com.demo.weixin.entity.store.StoreGoods;
import com.demo.weixin.entity.store.StoreShop;
import com.demo.weixin.enums.store.GoodsStatusEnum;
import com.demo.weixin.enums.store.GoodsSceneEnum;
import com.demo.weixin.vo.store.GoodsCreateVO;
import com.demo.weixin.vo.store.GoodsQueryVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 商城商品服务
 */
@Service
@Slf4j
public class StoreGoodsService {

    @Autowired
    private StoreGoodsDao storeGoodsDao;
    @Autowired
    private StoreShopService storeShopService;

    /**
     * 创建商品
     * [变更 2026-07-31 18:04] goodsType 只管支付方式，scene 管商品场景。
     * 相框专属字段由 scene=frame 判断写入，不再由 goodsType=4 判断。
     *
     * @param sellerUserId 卖家用户ID（当前登录用户）
     * @param vo           商品创建入参
     */
    public StoreGoods createGoods(Long sellerUserId, GoodsCreateVO vo) {
        // S4: 校验shopId是否属于当前用户
        StoreShop shop = storeShopService.getShopBySeller(sellerUserId);
        if (shop == null) {
            throw new BizException("您还没有店铺，请先创建店铺");
        }
        if (vo.getShopId() == null || !vo.getShopId().equals(shop.getShopId())) {
            throw new BizException("无权操作此店铺的商品");
        }
        validateGoodsVO(vo);
        StoreGoods goods = new StoreGoods();
        goods.setShopId(vo.getShopId());
        goods.setTitle(vo.getTitle());
        goods.setDescription(vo.getDescription());
        goods.setCoverImage(vo.getCoverImage());
        goods.setCarouselImages(vo.getCarouselImages());
        goods.setPointsPrice(vo.getPointsPrice() != null ? vo.getPointsPrice() : 0);
        goods.setCashPrice(vo.getCashPrice() != null ? vo.getCashPrice() : BigDecimal.ZERO);
        goods.setOriginalPrice(vo.getOriginalPrice());
        goods.setStock(vo.getStock() != null ? vo.getStock() : 0);
        goods.setSalesCount(0);
        goods.setGoodsType(vo.getGoodsType());
        goods.setStatus(GoodsStatusEnum.ON_SALE.getCode());
        goods.setCategory(vo.getCategory());
        goods.setSpecs(vo.getSpecs());
        // [变更 2026-07-31 18:04] scene 字段设置
        goods.setScene(vo.getScene());
        // 相框专属字段（scene=frame时写入，其他场景忽略）
        if (GoodsSceneEnum.FRAME.getCode().equals(vo.getScene())) {
            goods.setFrameNo(vo.getFrameNo());
            goods.setFrameSize(vo.getFrameSize());
            goods.setSceneDesc(vo.getSceneDesc());
            goods.setDelivery(vo.getDelivery());
            goods.setFeatures(vo.getFeatures());
        }
        // [新增 2026-08-03 17:30] 从店铺继承社区ID用于数据隔离
        goods.setCommunityId(shop.getCommunityId());
        goods.setCommunityName(shop.getCommunityName());
        storeGoodsDao.insertDocument(goods);
        log.info("创建商品，goodsId={}，title={}，goodsType={}", goods.getGoodsId(), goods.getTitle(), vo.getGoodsType());
        return goods;
    }

    /**
     * 编辑商品
     *
     * @param sellerUserId 卖家用户ID（当前登录用户）
     * @param vo           商品编辑入参
     */
    public StoreGoods updateGoods(Long sellerUserId, GoodsCreateVO vo) {
        if (vo.getGoodsId() == null) {
            throw new BizException("商品ID不能为空");
        }
        StoreGoods goods = storeGoodsDao.findById(vo.getGoodsId());
        if (goods == null) {
            throw new BizException("商品不存在");
        }
        // S4: 校验shopId是否属于当前用户
        StoreShop shop = storeShopService.getShopBySeller(sellerUserId);
        if (shop == null) {
            throw new BizException("您还没有店铺，请先创建店铺");
        }
        if (goods.getShopId() == null || !goods.getShopId().equals(shop.getShopId())) {
            throw new BizException("无权操作此店铺的商品");
        }
        if (StrUtil.isNotBlank(vo.getTitle())) {
            goods.setTitle(vo.getTitle());
        }
        if (vo.getDescription() != null) {
            goods.setDescription(vo.getDescription());
        }
        if (vo.getCoverImage() != null) {
            goods.setCoverImage(vo.getCoverImage());
        }
        if (vo.getCarouselImages() != null) {
            goods.setCarouselImages(vo.getCarouselImages());
        }
        if (vo.getPointsPrice() != null) {
            goods.setPointsPrice(vo.getPointsPrice());
        }
        if (vo.getCashPrice() != null) {
            goods.setCashPrice(vo.getCashPrice());
        }
        if (vo.getOriginalPrice() != null) {
            goods.setOriginalPrice(vo.getOriginalPrice());
        }
        if (vo.getStock() != null) {
            goods.setStock(vo.getStock());
        }
        if (vo.getGoodsType() != null) {
            goods.setGoodsType(vo.getGoodsType());
        }
        // [变更 2026-07-31 18:04] scene 字段编辑
        if (vo.getScene() != null) {
            goods.setScene(vo.getScene());
        }
        if (vo.getCategory() != null) {
            goods.setCategory(vo.getCategory());
        }
        if (vo.getSpecs() != null) {
            goods.setSpecs(vo.getSpecs());
        }
        // 相框专属字段编辑（仅当商品场景为相框时生效）
        if (GoodsSceneEnum.FRAME.getCode().equals(goods.getScene())) {
            if (vo.getFrameNo() != null) {
                goods.setFrameNo(vo.getFrameNo());
            }
            if (vo.getFrameSize() != null) {
                goods.setFrameSize(vo.getFrameSize());
            }
            if (vo.getSceneDesc() != null) {
                goods.setSceneDesc(vo.getSceneDesc());
            }
            if (vo.getDelivery() != null) {
                goods.setDelivery(vo.getDelivery());
            }
            if (vo.getFeatures() != null) {
                goods.setFeatures(vo.getFeatures());
            }
        }
        storeGoodsDao.saveOrUpdate(goods);
        return goods;
    }

    /**
     * 商品上下架
     */
    public void toggleStatus(Long goodsId) {
        StoreGoods goods = storeGoodsDao.findById(goodsId);
        if (goods == null) {
            throw new BizException("商品不存在");
        }
        Integer newStatus = Integer.valueOf(GoodsStatusEnum.ON_SALE.getCode()).equals(goods.getStatus())
                ? GoodsStatusEnum.OFF_SHELF.getCode()
                : GoodsStatusEnum.ON_SALE.getCode();
        storeGoodsDao.updateOneDocument(
                Criteria.where("goodsId").is(goodsId),
                new Update().set("status", newStatus));
    }

    /**
     * 获取商品详情
     */
    public StoreGoods getGoodsDetail(Long goodsId) {
        StoreGoods goods = storeGoodsDao.findById(goodsId);
        if (goods == null) {
            throw new BizException("商品不存在");
        }
        return goods;
    }

    /**
     * 分页查询商品（C端只查上架商品）
     */
    public Page<StoreGoods> queryGoodsPage(GoodsQueryVO queryVO) {
        Pageable pageable = PageRequest.of(queryVO.getPageNumber(), queryVO.getPageSize(),
                Sort.by(Sort.Order.desc("createTime")));
        Criteria criteria = new Criteria();
        // [新增 2026-08-03 17:30] 社区数据隔离：按communityId过滤
        if (queryVO.getCommunityId() != null) {
            criteria.and("communityId").is(queryVO.getCommunityId());
        }
        // C端默认只查上架商品
        if (queryVO.getStatus() != null) {
            criteria.and("status").is(queryVO.getStatus());
        } else {
            criteria.and("status").is(GoodsStatusEnum.ON_SALE.getCode());
        }
        if (queryVO.getShopId() != null) {
            criteria.and("shopId").is(queryVO.getShopId());
        }
        if (StrUtil.isNotBlank(queryVO.getCategory())) {
            criteria.and("category").is(queryVO.getCategory());
        }
        if (queryVO.getGoodsType() != null) {
            criteria.and("goodsType").is(queryVO.getGoodsType());
        }
        // [新增 2026-07-31 18:04] 按商品场景筛选，对应前端商城 tab
        if (StrUtil.isNotBlank(queryVO.getScene())) {
            criteria.and("scene").is(queryVO.getScene());
        }
        if (StrUtil.isNotBlank(queryVO.getKeyword())) {
            // M6: 使用Pattern.quote转义关键词，防止正则注入
            criteria.and("title").regex(Pattern.quote(queryVO.getKeyword()));
        }
        return storeGoodsDao.findDocumentPage(criteria, pageable);
    }

    /**
     * 查询店铺商品列表
     */
    public List<StoreGoods> queryShopGoods(Long shopId) {
        return storeGoodsDao.findDocumentList(
                Criteria.where("shopId").is(shopId)
                        .and("status").is(GoodsStatusEnum.ON_SALE.getCode()),
                Sort.Order.desc("createTime"));
    }

    /**
     * 扣减库存（原子操作，下单时调用）
     * 使用条件更新保证并发安全：只有库存>=购买量时才成功
     *
     * @return true=扣减成功，false=库存不足
     */
    public Boolean deductStock(Long goodsId, int count) {
        Criteria criteria = Criteria.where("goodsId").is(goodsId)
                .and("stock").gte(count)
                .and("status").is(GoodsStatusEnum.ON_SALE.getCode());
        Update update = new Update()
                .inc("stock", -count)
                .inc("salesCount", count);
        return storeGoodsDao.updateOneDocument(criteria, update);
    }

    /**
     * 恢复库存（取消/退款时调用）
     * M7: 条件更新，只有salesCount >= count时才恢复，防止salesCount变为负数
     */
    public void restoreStock(Long goodsId, int count) {
        Criteria criteria = Criteria.where("goodsId").is(goodsId)
                .and("salesCount").gte(count);
        Update update = new Update()
                .inc("stock", count)
                .inc("salesCount", -count);
        Boolean success = storeGoodsDao.updateOneDocument(criteria, update);
        if (!success) {
            log.warn("库存恢复失败，salesCount不足，goodsId={}，count={}", goodsId, count);
        }
    }

    /**
     * 逻辑删除商品
     */
    public void deleteGoods(Long goodsId) {
        storeGoodsDao.deleteDocument(goodsId);
    }

    /**
     * 校验商品创建参数
     * [变更 2026-07-31 18:04] 相框专属校验改为由 scene=frame 判断，不再由 goodsType=4 判断。
     * L2: 增加价格、库存等字段的合法性校验。
     */
    private void validateGoodsVO(GoodsCreateVO vo) {
        if (StrUtil.isBlank(vo.getTitle())) {
            throw new BizException("商品标题不能为空");
        }
        if (vo.getGoodsType() == null) {
            throw new BizException("商品类型不能为空");
        }
        // [新增 2026-07-31 18:04] 校验商品场景
        if (StrUtil.isBlank(vo.getScene())) {
            throw new BizException("商品场景不能为空");
        }
        if (GoodsSceneEnum.getByCode(vo.getScene()) == null) {
            throw new BizException("无效的商品场景：" + vo.getScene());
        }
        // L2: 商品价格及库存合法性校验
        Integer pointsPrice = vo.getPointsPrice() != null ? vo.getPointsPrice() : 0;
        BigDecimal cashPrice = vo.getCashPrice() != null ? vo.getCashPrice() : BigDecimal.ZERO;
        if (pointsPrice < 0) {
            throw new BizException("积分价格不能为负数");
        }
        if (cashPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new BizException("现金价格不能为负数");
        }
        if (vo.getOriginalPrice() != null && vo.getOriginalPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new BizException("原价不能为负数");
        }
        if (vo.getStock() != null && vo.getStock() < 0) {
            throw new BizException("库存不能为负数");
        }
        // [变更 2026-07-31 18:04] 混合支付时积分和现金都必须大于0
        if (Integer.valueOf(3).equals(vo.getGoodsType())) {
            if (pointsPrice <= 0) {
                throw new BizException("混合支付商品的积分价格必须大于0");
            }
            if (cashPrice.compareTo(BigDecimal.ZERO) <= 0) {
                throw new BizException("混合支付商品的现金价格必须大于0");
            }
        } else if (pointsPrice == 0 && cashPrice.compareTo(BigDecimal.ZERO) == 0) {
            // 非混合支付时，积分和现金不能同时为0
            throw new BizException("积分价格和现金价格不能同时为0");
        }
        // 相框场景专属校验（由 scene=frame 判断，不再由 goodsType=4 判断）
        if (GoodsSceneEnum.FRAME.getCode().equals(vo.getScene())) {
            if (StrUtil.isBlank(vo.getFrameNo())) {
                throw new BizException("相框编号不能为空");
            }
            if (StrUtil.isBlank(vo.getFrameSize())) {
                throw new BizException("相框尺寸不能为空");
            }
        }
    }
}
