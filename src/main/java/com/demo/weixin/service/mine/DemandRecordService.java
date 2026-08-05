package com.demo.weixin.service.mine;

import cn.hutool.core.util.StrUtil;
import com.demo.common.exception.BizException;
import com.demo.weixin.dao.mine.DemandRecordDao;
import com.demo.weixin.entity.mine.DemandRecord;
import com.demo.weixin.enums.mine.DemandStatusEnum;
import com.demo.weixin.vo.mine.DemandCreateVO;
import com.demo.weixin.vo.mine.DemandEvaluateVO;
import com.demo.weixin.vo.mine.DemandQueryVO;
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

/**
 * 需求/帮忙记录服务
 * 需求发布记录和帮忙记录共用 DemandRecord 表，通过查询条件区分视角：
 * - 需求发布记录（role=1）：查询 publisherUserId = 当前用户
 * - 帮忙记录（role=2）：查询 helperUserId = 当前用户
 * 状态流转：待帮忙 → 已接单 → 待评价 → 已完成 / 已过期
 */
@Service
@Slf4j
public class DemandRecordService {

    /** 视角：发布者 */
    private static final int ROLE_PUBLISHER = 1;
    /** 视角：帮忙者 */
    private static final int ROLE_HELPER = 2;

    @Autowired
    private DemandRecordDao demandRecordDao;

    /**
     * 分页查询需求/帮忙记录
     * 通过 role 区分视角，支持按状态、需求类型筛选和排序。
     *
     * @param userId  当前用户ID
     * @param queryVO 查询条件
     * @return 需求记录分页结果
     */
    public Page<DemandRecord> getDemandPage(Long userId, DemandQueryVO queryVO) {
        Sort sort = buildSort(queryVO.getSort());
        Pageable pageable = PageRequest.of(queryVO.getPageNumber(), queryVO.getPageSize(), sort);
        Criteria criteria = new Criteria();
        // [新增 2026-08-03 17:30] 社区数据隔离：按communityId过滤
        if (queryVO.getCommunityId() != null) {
            criteria.and("communityId").is(queryVO.getCommunityId());
        }
        // 视角区分
        Integer role = queryVO.getRole() != null ? queryVO.getRole() : ROLE_PUBLISHER;
        if (role == ROLE_HELPER) {
            // 帮忙记录：helperUserId = 当前用户
            criteria.and("helperUserId").is(userId);
        } else {
            // 需求发布记录：publisherUserId = 当前用户
            criteria.and("publisherUserId").is(userId);
        }
        // 状态筛选
        if (StrUtil.isNotBlank(queryVO.getStatus()) && !"all".equalsIgnoreCase(queryVO.getStatus())) {
            DemandStatusEnum statusEnum = parseStatus(queryVO.getStatus());
            if (statusEnum != null) {
                criteria.and("status").is(statusEnum.getCode());
            }
        }
        // 需求类型筛选
        if (StrUtil.isNotBlank(queryVO.getRequirement())) {
            criteria.and("requirement").is(queryVO.getRequirement());
        }
        return demandRecordDao.findDocumentPage(criteria, pageable);
    }

    /**
     * 公共需求列表（好事广场）
     * 查询状态为待帮忙的需求，排除当前用户自己发布的。
     *
     * @param userId   当前用户ID（排除自己的需求）
     * @param queryVO  查询条件（支持需求类型筛选、排序、分页）
     * @return 需求分页列表
     */
    public Page<DemandRecord> getPublicDemandPage(Long userId, DemandQueryVO queryVO) {
        // 构建排序条件（复用 buildSort 方法）
        Sort sort = buildSort(queryVO.getSort());
        Pageable pageable = PageRequest.of(queryVO.getPageNumber(), queryVO.getPageSize(), sort);
        Criteria criteria = new Criteria();
        // [新增 2026-08-03 17:30] 社区数据隔离：按communityId过滤
        if (queryVO.getCommunityId() != null) {
            criteria.and("communityId").is(queryVO.getCommunityId());
        }
        // 查询条件：状态为待帮忙
        criteria.and("status").is(DemandStatusEnum.PENDING.getCode());
        // 排除当前用户自己发布的需求
        criteria.and("publisherUserId").ne(userId);
        // 支持按需求类型筛选
        if (StrUtil.isNotBlank(queryVO.getRequirement())) {
            criteria.and("requirement").is(queryVO.getRequirement());
        }
        return demandRecordDao.findDocumentPage(criteria, pageable);
    }

    /**
     * 获取需求详情（公共视角）
     * 不校验归属，任何人都可以查看。
     *
     * @param demandId 需求ID
     * @return 需求记录
     */
    public DemandRecord getDemandDetail(Long demandId) {
        // 直接根据需求ID查询，不存在抛异常
        DemandRecord record = demandRecordDao.findById(demandId);
        if (record == null) {
            throw new BizException("需求不存在");
        }
        return record;
    }

    /**
     * 发布需求
     * 新需求状态为待帮忙，role 标记为发布者视角。
     *
     * @param userId 发布者用户ID
     * @param vo     需求创建入参
     * @return 创建后的需求记录
     */
    public DemandRecord createDemand(Long userId, DemandCreateVO vo) {
        if (StrUtil.isBlank(vo.getTitle())) {
            throw new BizException("需求标题不能为空");
        }
        DemandRecord record = new DemandRecord();
        record.setPublisherUserId(userId);
        record.setTitle(vo.getTitle());
        record.setContent(vo.getContent());
        record.setLocation(vo.getLocation());
        record.setServiceTime(vo.getServiceTime());
        // 时间模式：negotiate双方协商（serviceTime/specificTime为空） specific指定时间（specificTime必填）
        record.setTimeType(vo.getTimeType());
        record.setSpecificTime(vo.getSpecificTime());
        record.setRequirement(vo.getRequirement());
        record.setStatus(DemandStatusEnum.PENDING.getCode());
        record.setRole(ROLE_PUBLISHER);
        // 冗余存储服务对象信息，不受ServiceMember变更/删除影响
        record.setMemberName(vo.getMemberName());
        record.setMemberPhone(vo.getMemberPhone());
        record.setMemberAddress(vo.getMemberAddress());
        record.setMemberDetailAddress(vo.getMemberDetailAddress());
        record.setRemark(vo.getRemark());
        // [新增 2026-08-03 17:40] 设置社区ID用于数据隔离
        record.setCommunityId(vo.getCommunityId());
        demandRecordDao.insertDocument(record);
        log.info("发布需求，demandId={}，publisherUserId={}，title={}",
                record.getDemandId(), userId, record.getTitle());
        return record;
    }

    /**
     * 接单（帮忙者接单）
     * 校验：需求是否存在、状态是否为待帮忙、是否自己接自己的单。
     * 接单后状态流转为已接单，helperUserId 记录帮忙者。
     *
     * @param userId   帮忙者用户ID
     * @param demandId 需求ID
     * @return 更新后的需求记录
     */
    public DemandRecord acceptDemand(Long userId, Long demandId) {
        DemandRecord record = demandRecordDao.findById(demandId);
        if (record == null) {
            throw new BizException("需求不存在");
        }
        // 校验状态：必须为待帮忙
        if (record.getStatus() != DemandStatusEnum.PENDING.getCode()) {
            throw new BizException("当前需求状态不可接单");
        }
        // 校验：不能接自己发布的需求
        if (record.getPublisherUserId().equals(userId)) {
            throw new BizException("不能接自己发布的需求");
        }
        // 原子更新：状态为待帮忙且帮忙者未设置时才接单成功，防止并发重复接单
        Boolean success = demandRecordDao.updateOneDocument(
                Criteria.where("demandId").is(demandId)
                        .and("status").is(DemandStatusEnum.PENDING.getCode())
                        .and("helperUserId").is(null),
                new Update()
                        .set("helperUserId", userId)
                        .set("status", DemandStatusEnum.HELPING.getCode()));
        if (!success) {
            throw new BizException("接单失败，需求可能已被他人接单");
        }
        log.info("接单成功，demandId={}，helperUserId={}", demandId, userId);
        return demandRecordDao.findById(demandId);
    }

    /**
     * 确认服务完成
     * 校验：需求是否存在、状态是否为已接单。
     * 确认后状态从已接单流转为待评价，等待发布者评价。
     *
     * @param userId   操作用户ID（发布者或帮忙者均可确认完成）
     * @param demandId 需求ID
     * @return 更新后的需求记录
     */
    public DemandRecord completeDemand(Long userId, Long demandId) {
        DemandRecord record = demandRecordDao.findById(demandId);
        if (record == null) {
            throw new BizException("需求不存在");
        }
        // 校验状态：必须为已接单
        if (record.getStatus() != DemandStatusEnum.HELPING.getCode()) {
            throw new BizException("当前需求状态不可完成");
        }
        // 校验操作权限：发布者或帮忙者均可
        boolean isPublisher = record.getPublisherUserId().equals(userId);
        boolean isHelper = record.getHelperUserId() != null && record.getHelperUserId().equals(userId);
        if (!isPublisher && !isHelper) {
            throw new BizException("无权操作该需求");
        }
        // 原子状态更新：仅当状态为已接单时才流转为待评价，防止并发重复完成
        Boolean success = demandRecordDao.updateOneDocument(
                Criteria.where("demandId").is(demandId)
                        .and("status").is(DemandStatusEnum.HELPING.getCode()),
                new Update()
                        .set("status", DemandStatusEnum.TO_EVALUATE.getCode())
                        .set("updateTime", new Date()));
        if (!success) {
            // 修改行数为0，说明状态已变更（非已接单），拒绝重复操作
            throw new BizException("需求状态已变更");
        }
        log.info("确认服务完成，demandId={}，userId={}，状态流转为待评价", demandId, userId);
        return demandRecordDao.findById(demandId);
    }

    /**
     * 评价需求
     * 发布者对帮忙者的服务进行评价，评价后状态从待评价流转为已完成。
     * 只有发布者可以评价。
     *
     * @param userId 评价用户ID（必须为发布者）
     * @param vo     评价入参
     * @return 更新后的需求记录
     */
    public DemandRecord evaluateDemand(Long userId, DemandEvaluateVO vo) {
        if (vo.getDemandId() == null) {
            throw new BizException("需求ID不能为空");
        }
        if (vo.getRating() == null || vo.getRating() < 1 || vo.getRating() > 5) {
            throw new BizException("评分必须在1-5之间");
        }
        DemandRecord record = demandRecordDao.findById(vo.getDemandId());
        if (record == null) {
            throw new BizException("需求不存在");
        }
        // 校验状态：必须为待评价
        if (record.getStatus() != DemandStatusEnum.TO_EVALUATE.getCode()) {
            throw new BizException("当前需求状态不可评价");
        }
        // 校验操作权限：只有发布者可以评价
        if (!record.getPublisherUserId().equals(userId)) {
            throw new BizException("只有需求发布者可以评价");
        }
        // 原子状态更新：仅当状态为待评价时才流转为已完成，防止并发重复评价
        Boolean success = demandRecordDao.updateOneDocument(
                Criteria.where("demandId").is(vo.getDemandId())
                        .and("status").is(DemandStatusEnum.TO_EVALUATE.getCode()),
                new Update()
                        .set("status", DemandStatusEnum.COMPLETED.getCode())
                        .set("rating", vo.getRating())
                        .set("evaluateContent", vo.getContent())
                        .set("evaluateTime", new Date()));
        if (!success) {
            // 修改行数为0，说明需求尚未完成或已被评价
            throw new BizException("需求尚未完成或已评价");
        }
        log.info("评价需求，demandId={}，userId={}，rating={}", vo.getDemandId(), userId, vo.getRating());
        return demandRecordDao.findById(vo.getDemandId());
    }

    /**
     * 构建排序条件
     *
     * @param sort 排序方式：asc升序 / desc降序
     * @return Sort 对象
     */
    private Sort buildSort(String sort) {
        if (StrUtil.isNotBlank(sort) && "asc".equalsIgnoreCase(sort)) {
            return Sort.by(Sort.Order.asc("serviceTime"));
        }
        // 默认按服务时间降序
        return Sort.by(Sort.Order.desc("serviceTime"));
    }

    /**
     * 将前端状态字符串解析为枚举
     *
     * @param status 状态字符串
     * @return 状态枚举
     */
    private DemandStatusEnum parseStatus(String status) {
        switch (status.toLowerCase()) {
            case "pending":
                return DemandStatusEnum.PENDING;
            case "helping":
                return DemandStatusEnum.HELPING;
            case "toevaluate":
                return DemandStatusEnum.TO_EVALUATE;
            case "completed":
                return DemandStatusEnum.COMPLETED;
            case "expired":
                return DemandStatusEnum.EXPIRED;
            default:
                return null;
        }
    }
}
