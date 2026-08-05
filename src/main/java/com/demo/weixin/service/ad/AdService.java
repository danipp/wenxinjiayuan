package com.demo.weixin.service.ad;

import cn.hutool.core.util.StrUtil;
import com.demo.common.exception.BizException;
import com.demo.weixin.dao.ad.AdDao;
import com.demo.weixin.entity.ad.Ad;
import com.demo.weixin.vo.ad.AdSaveVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 广告位服务
 * 提供广告的增删改查及C端按位置查询有效广告列表的能力。
 */
@Service
@Slf4j
public class AdService {

    @Autowired
    private AdDao adDao;

    /** 广告状态-上架 */
    private static final int STATUS_ON = 1;
    /** 广告状态-下架 */
    private static final int STATUS_OFF = 2;

    /**
     * 新增/编辑广告
     * adId 为空时新增，非空时更新。
     *
     * @param vo 广告保存入参
     * @return 保存后的广告实体
     */
    public Ad saveOrUpdate(AdSaveVO vo) {
        validateAdVO(vo);
        Ad ad;
        if (vo.getAdId() != null) {
            // 编辑
            ad = adDao.findById(vo.getAdId());
            if (ad == null) {
                throw new BizException("广告不存在");
            }
        } else {
            // 新增
            ad = new Ad();
            ad.setStatus(STATUS_ON);
            ad.setSortNum(vo.getSortNum() != null ? vo.getSortNum() : 0);
        }
        ad.setPosition(vo.getPosition());
        ad.setTitle(vo.getTitle());
        ad.setImageUrl(vo.getImageUrl());
        ad.setLinkType(vo.getLinkType() != null ? vo.getLinkType() : 0);
        ad.setLinkValue(vo.getLinkValue());
        if (vo.getSortNum() != null) {
            ad.setSortNum(vo.getSortNum());
        }
        ad.setStartTime(vo.getStartTime());
        ad.setEndTime(vo.getEndTime());
        adDao.saveOrUpdate(ad);
        log.info("保存广告，adId={}，position={}，title={}", ad.getAdId(), ad.getPosition(), ad.getTitle());
        return ad;
    }

    /**
     * C端查询指定位置的有效广告列表
     * 条件：上架状态 + 投放时间有效 + 按sortNum升序
     *
     * @param position 广告位标识
     * @return 有效广告列表
     */
    public List<Ad> getActiveAdsByPosition(String position) {
        if (StrUtil.isBlank(position)) {
            throw new BizException("广告位标识不能为空");
        }
        Date now = new Date();
        // 基础条件：广告位 + 上架状态
        Criteria baseCriteria = Criteria.where("position").is(position)
                .and("status").is(STATUS_ON);
        // 投放时间过滤：startTime为空或<=now
        Criteria startTimeCriteria = new Criteria().orOperator(
                Criteria.where("startTime").is(null),
                Criteria.where("startTime").lte(now));
        // 投放时间过滤：endTime为空或>=now
        Criteria endTimeCriteria = new Criteria().orOperator(
                Criteria.where("endTime").is(null),
                Criteria.where("endTime").gte(now));
        // 组合所有条件
        Criteria criteria = new Criteria().andOperator(baseCriteria, startTimeCriteria, endTimeCriteria);
        return adDao.findDocumentList(criteria, Sort.Order.asc("sortNum"));
    }

    /**
     * 分页查询广告列表（管理端）
     */
    public List<Ad> getAdList(String position) {
        Criteria criteria = new Criteria();
        if (StrUtil.isNotBlank(position)) {
            criteria.and("position").is(position);
        }
        return adDao.findDocumentList(criteria, Sort.Order.desc("createTime"));
    }

    /**
     * 获取广告详情
     */
    public Ad getAdDetail(Long adId) {
        Ad ad = adDao.findById(adId);
        if (ad == null) {
            throw new BizException("广告不存在");
        }
        return ad;
    }

    /**
     * 广告上下架切换
     */
    public void toggleStatus(Long adId) {
        Ad ad = adDao.findById(adId);
        if (ad == null) {
            throw new BizException("广告不存在");
        }
        Integer newStatus = ad.getStatus() == STATUS_ON ? STATUS_OFF : STATUS_ON;
        adDao.updateOneDocument(
                Criteria.where("adId").is(adId),
                new org.springframework.data.mongodb.core.query.Update().set("status", newStatus));
        log.info("广告状态切换，adId={}，新状态={}", adId, newStatus);
    }

    /**
     * 逻辑删除广告
     */
    public void deleteAd(Long adId) {
        adDao.deleteDocument(adId);
        log.info("删除广告，adId={}", adId);
    }

    /**
     * 校验广告保存参数
     */
    private void validateAdVO(AdSaveVO vo) {
        if (StrUtil.isBlank(vo.getPosition())) {
            throw new BizException("广告位标识不能为空");
        }
        if (StrUtil.isBlank(vo.getImageUrl())) {
            throw new BizException("广告图片不能为空");
        }
    }
}
