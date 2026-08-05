package com.demo.weixin.service.assistance;

import cn.hutool.core.util.StrUtil;
import com.demo.common.exception.BizException;
import com.demo.weixin.dao.assistance.CharityEnterpriseDao;
import com.demo.weixin.entity.assistance.CharityEnterprise;
import com.demo.weixin.enums.assistance.EnterpriseStatusEnum;
import com.demo.weixin.vo.assistance.EnterpriseQueryVO;
import com.demo.weixin.vo.assistance.EnterpriseSaveVO;
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
import java.util.regex.Pattern;

/**
 * 爱心企业服务
 * 提供企业新增/编辑、分页查询、上下架切换、逻辑删除、捐赠统计更新等功能。
 */
@Service
@Slf4j
public class CharityEnterpriseService {

    @Autowired
    private CharityEnterpriseDao charityEnterpriseDao;

    /**
     * 保存/编辑企业
     * enterpriseId为空时新增（设置status=ACTIVE，累计捐赠归零），有值时编辑已有企业。
     *
     * @param vo 企业保存入参
     * @return 保存后的企业记录
     */
    public CharityEnterprise saveOrUpdateEnterprise(EnterpriseSaveVO vo) {
        if (vo.getEnterpriseId() == null) {
            // 新增企业
            CharityEnterprise enterprise = new CharityEnterprise();
            enterprise.setName(vo.getName());
            enterprise.setLogo(vo.getLogo());
            enterprise.setDescription(vo.getDescription());
            enterprise.setContactName(vo.getContactName());
            enterprise.setContactPhone(vo.getContactPhone());
            enterprise.setAddress(vo.getAddress());
            enterprise.setSort(vo.getSort());
            // [新增 2026-08-03 17:40] 设置社区ID用于数据隔离
            enterprise.setCommunityId(vo.getCommunityId());
            enterprise.setStatus(EnterpriseStatusEnum.ACTIVE.getCode());
            enterprise.setTotalDonationAmount(BigDecimal.ZERO);
            enterprise.setTotalDonationCount(0);
            charityEnterpriseDao.insertDocument(enterprise);
            log.info("新增爱心企业，enterpriseId={}，name={}", enterprise.getEnterpriseId(), vo.getName());
            return enterprise;
        }
        // 编辑企业
        CharityEnterprise existing = charityEnterpriseDao.findById(vo.getEnterpriseId());
        if (existing == null) {
            throw new BizException("企业不存在");
        }
        charityEnterpriseDao.updateOneDocument(
                Criteria.where("enterpriseId").is(vo.getEnterpriseId()),
                new Update()
                        .set("name", vo.getName())
                        .set("logo", vo.getLogo())
                        .set("description", vo.getDescription())
                        .set("contactName", vo.getContactName())
                        .set("contactPhone", vo.getContactPhone())
                        .set("address", vo.getAddress())
                        .set("sort", vo.getSort())
                        .set("communityId", vo.getCommunityId()));
        log.info("编辑爱心企业，enterpriseId={}，name={}", vo.getEnterpriseId(), vo.getName());
        return charityEnterpriseDao.findById(vo.getEnterpriseId());
    }

    /**
     * 分页查询企业
     * 支持按状态筛选、名称模糊搜索（Pattern.quote转义防正则注入），按sort升序、createTime降序排列。
     *
     * @param vo 查询条件
     * @return 企业分页结果
     */
    public Page<CharityEnterprise> queryEnterprisePage(EnterpriseQueryVO vo) {
        Pageable pageable = PageRequest.of(vo.getPageNumber(), vo.getPageSize(),
                Sort.by(Sort.Order.asc("sort"), Sort.Order.desc("createTime")));
        Criteria criteria = new Criteria();
        // [新增 2026-08-03 17:30] 社区数据隔离：按communityId过滤
        if (vo.getCommunityId() != null) {
            criteria.and("communityId").is(vo.getCommunityId());
        }
        // 状态筛选
        if (StrUtil.isNotBlank(vo.getStatus())) {
            criteria.and("status").is(vo.getStatus());
        }
        // 名称模糊搜索（使用Pattern.quote转义，防止正则注入）
        if (StrUtil.isNotBlank(vo.getName())) {
            criteria.and("name").regex(Pattern.quote(vo.getName()));
        }
        return charityEnterpriseDao.findDocumentPage(criteria, pageable);
    }

    /**
     * 上下架切换
     * active↔inactive相互切换。
     *
     * @param enterpriseId 企业ID
     */
    public void toggleStatus(Long enterpriseId) {
        CharityEnterprise enterprise = charityEnterpriseDao.findById(enterpriseId);
        if (enterprise == null) {
            throw new BizException("企业不存在");
        }
        // 根据当前状态切换
        String newStatus = EnterpriseStatusEnum.ACTIVE.getCode().equals(enterprise.getStatus())
                ? EnterpriseStatusEnum.INACTIVE.getCode()
                : EnterpriseStatusEnum.ACTIVE.getCode();
        charityEnterpriseDao.updateOneDocument(
                Criteria.where("enterpriseId").is(enterpriseId),
                new Update().set("status", newStatus));
        log.info("切换企业状态，enterpriseId={}，{}→{}", enterpriseId, enterprise.getStatus(), newStatus);
    }

    /**
     * 逻辑删除企业
     *
     * @param enterpriseId 企业ID
     */
    public void deleteEnterprise(Long enterpriseId) {
        CharityEnterprise enterprise = charityEnterpriseDao.findById(enterpriseId);
        if (enterprise == null) {
            throw new BizException("企业不存在");
        }
        charityEnterpriseDao.deleteDocument(enterpriseId);
        log.info("逻辑删除企业，enterpriseId={}", enterpriseId);
    }

    /**
     * 更新企业捐赠统计
     * totalDonationAmount += amount，totalDonationCount += 1，使用$inc原子更新保证并发安全。
     *
     * @param enterpriseId 企业ID
     * @param amount       本次捐赠金额/估值
     */
    public void updateDonationStats(Long enterpriseId, BigDecimal amount) {
        if (enterpriseId == null) {
            return;
        }
        BigDecimal safeAmount = amount != null ? amount : BigDecimal.ZERO;
        Boolean success = charityEnterpriseDao.updateOneDocument(
                Criteria.where("enterpriseId").is(enterpriseId),
                new Update()
                        .inc("totalDonationAmount", safeAmount)
                        .inc("totalDonationCount", 1));
        if (!success) {
            log.warn("更新企业捐赠统计失败，enterpriseId={}，amount={}", enterpriseId, safeAmount);
        }
    }
}
