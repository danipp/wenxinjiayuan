package com.demo.weixin.service.community;

import cn.hutool.core.util.StrUtil;
import com.demo.common.exception.BizException;
import com.demo.weixin.dao.community.CommunityDao;
import com.demo.weixin.entity.community.Community;
import com.demo.weixin.enums.community.CommunityStatusEnum;
import com.demo.weixin.vo.community.CommunityQueryVO;
import com.demo.weixin.vo.community.CommunitySaveVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 社区服务
 * 提供社区CRUD、分页查询、启用/禁用切换等功能。
 * 社区数据由管理员后台维护，C端用户通过列表接口获取可选社区。
 * 各业务模块通过 communityId 实现数据隔离。
 */
@Service
@Slf4j
public class CommunityService {

    @Autowired
    private CommunityDao communityDao;

    /**
     * 新增社区
     *
     * @param vo 社区保存入参
     * @return 创建后的社区
     */
    public Community createCommunity(CommunitySaveVO vo) {
        // 校验社区名称唯一性
        Community exist = communityDao.findOne(Criteria.where("name").is(vo.getName()));
        if (exist != null) {
            throw new BizException("社区名称已存在");
        }
        Community community = new Community();
        community.setName(vo.getName());
        community.setAddress(vo.getAddress());
        community.setLongitude(vo.getLongitude());
        community.setLatitude(vo.getLatitude());
        community.setContactName(vo.getContactName());
        community.setContactPhone(vo.getContactPhone());
        community.setDescription(vo.getDescription());
        community.setLogo(vo.getLogo());
        // 默认启用
        community.setStatus(vo.getStatus() != null ? vo.getStatus() : CommunityStatusEnum.ACTIVE.getCode());
        community.setSort(vo.getSort() != null ? vo.getSort() : 0);
        communityDao.insertDocument(community);
        log.info("创建社区，communityId={}，name={}", community.getCommunityId(), community.getName());
        return community;
    }

    /**
     * 编辑社区
     *
     * @param vo 社区保存入参（communityId必传）
     * @return 更新后的社区
     */
    public Community updateCommunity(CommunitySaveVO vo) {
        if (vo.getCommunityId() == null) {
            throw new BizException("社区ID不能为空");
        }
        Community community = communityDao.findById(vo.getCommunityId());
        if (community == null) {
            throw new BizException("社区不存在");
        }
        // 名称唯一性校验（排除自身）
        if (StrUtil.isNotBlank(vo.getName()) && !vo.getName().equals(community.getName())) {
            Community exist = communityDao.findOne(Criteria.where("name").is(vo.getName()));
            if (exist != null) {
                throw new BizException("社区名称已存在");
            }
        }
        Update update = new Update();
        if (StrUtil.isNotBlank(vo.getName())) {
            update.set("name", vo.getName());
        }
        if (vo.getAddress() != null) {
            update.set("address", vo.getAddress());
        }
        if (vo.getLongitude() != null) {
            update.set("longitude", vo.getLongitude());
        }
        if (vo.getLatitude() != null) {
            update.set("latitude", vo.getLatitude());
        }
        if (vo.getContactName() != null) {
            update.set("contactName", vo.getContactName());
        }
        if (vo.getContactPhone() != null) {
            update.set("contactPhone", vo.getContactPhone());
        }
        if (vo.getDescription() != null) {
            update.set("description", vo.getDescription());
        }
        if (vo.getLogo() != null) {
            update.set("logo", vo.getLogo());
        }
        if (vo.getStatus() != null) {
            update.set("status", vo.getStatus());
        }
        if (vo.getSort() != null) {
            update.set("sort", vo.getSort());
        }
        communityDao.updateOneDocument(Criteria.where("communityId").is(vo.getCommunityId()), update);
        log.info("编辑社区，communityId={}", vo.getCommunityId());
        return communityDao.findById(vo.getCommunityId());
    }

    /**
     * 删除社区（逻辑删除）
     *
     * @param communityId 社区ID
     */
    public void deleteCommunity(Long communityId) {
        Community community = communityDao.findById(communityId);
        if (community == null) {
            throw new BizException("社区不存在");
        }
        communityDao.deleteDocument(communityId);
        log.info("删除社区，communityId={}", communityId);
    }

    /**
     * 切换社区状态（启用/禁用）
     *
     * @param communityId 社区ID
     */
    public void toggleStatus(Long communityId) {
        Community community = communityDao.findById(communityId);
        if (community == null) {
            throw new BizException("社区不存在");
        }
        Integer newStatus = CommunityStatusEnum.ACTIVE.getCode().equals(community.getStatus())
                ? CommunityStatusEnum.INACTIVE.getCode()
                : CommunityStatusEnum.ACTIVE.getCode();
        communityDao.updateOneDocument(
                Criteria.where("communityId").is(communityId),
                new Update().set("status", newStatus));
        log.info("切换社区状态，communityId={}，newStatus={}", communityId, newStatus);
    }

    /**
     * 获取社区详情
     *
     * @param communityId 社区ID
     * @return 社区详情
     */
    public Community getCommunityDetail(Long communityId) {
        Community community = communityDao.findById(communityId);
        if (community == null) {
            throw new BizException("社区不存在");
        }
        return community;
    }

    /**
     * 分页查询社区（管理员视角，可查全部状态）
     *
     * @param vo 查询条件
     * @return 社区分页列表
     */
    public Page<Community> queryCommunityPage(CommunityQueryVO vo) {
        Pageable pageable = PageRequest.of(vo.getPageNumber() - 1, vo.getPageSize(),
                Sort.by(Sort.Order.asc("sort"), Sort.Order.desc("createTime")));
        Criteria criteria = new Criteria();
        // 状态筛选
        if (vo.getStatus() != null) {
            criteria.and("status").is(vo.getStatus());
        }
        // 关键词模糊搜索
        if (StrUtil.isNotBlank(vo.getKeyword())) {
            criteria.and("name").regex(Pattern.quote(vo.getKeyword()));
        }
        return communityDao.findDocumentPage(criteria, pageable);
    }

    /**
     * 获取启用的社区列表（C端用户选择社区用）
     * 只返回启用状态的社区，按排序权重和创建时间排列。
     *
     * @return 启用的社区列表
     */
    public List<Community> getActiveCommunityList() {
        return communityDao.findDocumentList(
                Criteria.where("status").is(CommunityStatusEnum.ACTIVE.getCode()),
                Sort.Order.asc("sort"),
                Sort.Order.desc("createTime"));
    }
}
