package com.demo.weixin.service.special;

import com.demo.weixin.dao.special.ShopCouponDao;
import com.demo.weixin.dao.special.ShopCouponRecordDao;
import com.demo.weixin.entity.special.ShopCoupon;
import com.demo.weixin.entity.special.ShopCouponRecord;
import com.demo.weixin.enums.special.CouponRecordStatusEnum;
import com.demo.weixin.enums.special.CouponStatusEnum;
import com.demo.weixin.vo.special.ShopCouponCreateVO;
import com.demo.common.exception.BizException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 店铺优惠券服务
 * 管理优惠券的创建、查询、领取、核销
 */
@Service
@Slf4j
public class ShopCouponService {

    @Autowired
    private ShopCouponDao shopCouponDao;
    @Autowired
    private ShopCouponRecordDao shopCouponRecordDao;

    /**
     * 创建或编辑优惠券（管理端）
     *
     * @param vo 优惠券入参
     * @return 保存后的优惠券
     */
    public ShopCoupon saveOrUpdate(ShopCouponCreateVO vo) {
        ShopCoupon coupon;
        if (vo.getCouponId() != null) {
            // 编辑
            coupon = shopCouponDao.findById(vo.getCouponId());
            if (coupon == null) {
                throw new BizException("优惠券不存在");
            }
        } else {
            // 新建
            coupon = new ShopCoupon();
            coupon.setClaimedCount(0);
            coupon.setStatus(CouponStatusEnum.ACTIVE.getCode());
        }
        coupon.setShopId(vo.getShopId());
        coupon.setTitle(vo.getTitle());
        coupon.setMoney(vo.getMoney());
        coupon.setMinSpend(vo.getMinSpend() != null ? vo.getMinSpend() : java.math.BigDecimal.ZERO);
        coupon.setTotal(vo.getTotal() != null ? vo.getTotal() : 0);
        coupon.setStartTime(vo.getStartTime());
        coupon.setEndTime(vo.getEndTime());
        return shopCouponDao.saveOrUpdate(coupon);
    }

    /**
     * 查询店铺的有效优惠券列表（C端）
     * 同时标记当前用户是否已领取
     *
     * @param shopId 店铺ID
     * @param userId 当前用户ID
     * @return 优惠券列表（含claimed字段）
     */
    public List<ShopCoupon> getActiveCouponsByShopId(Long shopId, Long userId) {
        // 查询店铺的有效优惠券
        Date now = new Date();
        List<ShopCoupon> coupons = shopCouponDao.findDocumentList(
                Criteria.where("shopId").is(shopId)
                        .and("status").is(CouponStatusEnum.ACTIVE.getCode()),
                Sort.Order.asc("createTime"));

        // 查询当前用户已领取的优惠券记录
        if (userId != null && !coupons.isEmpty()) {
            List<ShopCouponRecord> records = shopCouponRecordDao.findDocumentList(
                    Criteria.where("userId").is(userId)
                            .and("shopId").is(shopId));
            // 标记领取状态
            for (ShopCoupon coupon : coupons) {
                boolean claimed = records.stream()
                        .anyMatch(r -> r.getCouponId().equals(coupon.getCouponId()));
                coupon.setClaimed(claimed);
            }
        }
        return coupons;
    }

    /**
     * 用户领取优惠券
     * 防重复领取：同一用户同一优惠券只能领取一次
     *
     * @param userId   用户ID
     * @param couponId 优惠券ID
     * @return 领券记录
     */
    public ShopCouponRecord claimCoupon(Long userId, Long couponId) {
        // 校验优惠券存在且有效
        ShopCoupon coupon = shopCouponDao.findById(couponId);
        if (coupon == null) {
            throw new BizException("优惠券不存在");
        }
        if (!coupon.getStatus().equals(CouponStatusEnum.ACTIVE.getCode())) {
            throw new BizException("优惠券已下架或已过期");
        }
        // 校验有效期
        Date now = new Date();
        if (coupon.getEndTime() != null && now.after(coupon.getEndTime())) {
            throw new BizException("优惠券已过期");
        }
        // 校验发行量
        if (coupon.getTotal() != null && coupon.getTotal() > 0
                && coupon.getClaimedCount() != null
                && coupon.getClaimedCount() >= coupon.getTotal()) {
            throw new BizException("优惠券已被领完");
        }
        // 校验是否已领取
        ShopCouponRecord existRecord = shopCouponRecordDao.findOne(
                Criteria.where("couponId").is(couponId)
                        .and("userId").is(userId));
        if (existRecord != null) {
            throw new BizException("您已领取过该优惠券");
        }

        // 创建领券记录
        ShopCouponRecord record = new ShopCouponRecord();
        record.setCouponId(couponId);
        record.setShopId(coupon.getShopId());
        record.setUserId(userId);
        record.setStatus(CouponRecordStatusEnum.UNUSED.getCode());
        record.setClaimTime(now);
        record = shopCouponRecordDao.insertDocument(record);

        // 更新优惠券已领取数量
        shopCouponDao.updateOneDocument(
                Criteria.where("couponId").is(couponId),
                new Update().inc("claimedCount", 1));

        log.info("用户领取优惠券，userId={}，couponId={}，shopId={}", userId, couponId, coupon.getShopId());
        return record;
    }

    /**
     * 管理端：查询优惠券列表
     *
     * @param shopId 店铺ID（可选）
     * @return 优惠券列表
     */
    public List<ShopCoupon> getList(Long shopId) {
        Criteria criteria = new Criteria();
        if (shopId != null) {
            criteria.and("shopId").is(shopId);
        }
        return shopCouponDao.findDocumentList(criteria,
                Sort.Order.desc("createTime"));
    }

    /**
     * 管理端：删除优惠券（下架）
     *
     * @param couponId 优惠券ID
     */
    public void delete(Long couponId) {
        ShopCoupon coupon = shopCouponDao.findById(couponId);
        if (coupon == null) {
            throw new BizException("优惠券不存在");
        }
        shopCouponDao.deleteDocument(couponId);
        log.info("删除优惠券，couponId={}，title={}", couponId, coupon.getTitle());
    }
}
