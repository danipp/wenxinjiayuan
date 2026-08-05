package com.demo.weixin.service.notice;

import cn.hutool.core.util.StrUtil;
import com.demo.common.exception.BizException;
import com.demo.weixin.dao.notice.NoticeDao;
import com.demo.weixin.entity.community.Community;
import com.demo.weixin.entity.notice.Notice;
import com.demo.weixin.enums.notice.NoticeStatusEnum;
import com.demo.weixin.enums.notice.NoticeTypeEnum;
import com.demo.weixin.service.community.CommunityService;
import com.demo.weixin.vo.notice.NoticeQueryVO;
import com.demo.weixin.vo.notice.NoticeSaveVO;
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
 * 消息通知服务
 * [新增 2026-08-03 19:30] 提供通知CRUD、分页查询、C端有效通知查询等功能。
 * 通知数据由管理员后台维护，C端首页轮播展示。
 * 支持社区数据隔离（communityId为null时为全局通知，所有社区可见）。
 */
@Service
@Slf4j
public class NoticeService {

    @Autowired
    private NoticeDao noticeDao;
    @Autowired
    private CommunityService communityService;

    /**
     * 新增通知
     *
     * @param vo 通知保存入参
     * @return 创建后的通知
     */
    public Notice createNotice(NoticeSaveVO vo) {
        validateNoticeType(vo.getType());
        Notice notice = new Notice();
        notice.setTitle(vo.getTitle());
        notice.setContent(vo.getContent());
        notice.setType(vo.getType());
        notice.setLinkType(vo.getLinkType() != null ? vo.getLinkType() : 0);
        notice.setLinkValue(vo.getLinkValue());
        notice.setSortNum(vo.getSortNum() != null ? vo.getSortNum() : 0);
        // 默认上架
        notice.setStatus(vo.getStatus() != null ? vo.getStatus() : NoticeStatusEnum.PUBLISHED.getCode());
        notice.setStartTime(vo.getStartTime());
        notice.setEndTime(vo.getEndTime());
        // 社区关联：传入communityId时填充冗余字段
        if (vo.getCommunityId() != null) {
            notice.setCommunityId(vo.getCommunityId());
            Community community = communityService.getCommunityDetail(vo.getCommunityId());
            notice.setCommunityName(community.getName());
        }
        noticeDao.insertDocument(notice);
        log.info("创建通知，noticeId={}，title={}", notice.getNoticeId(), notice.getTitle());
        return notice;
    }

    /**
     * 编辑通知
     *
     * @param vo 通知保存入参（noticeId必传）
     * @return 更新后的通知
     */
    public Notice updateNotice(NoticeSaveVO vo) {
        if (vo.getNoticeId() == null) {
            throw new BizException("通知ID不能为空");
        }
        Notice notice = noticeDao.findById(vo.getNoticeId());
        if (notice == null) {
            throw new BizException("通知不存在");
        }
        if (vo.getType() != null) {
            validateNoticeType(vo.getType());
        }
        Update update = new Update();
        if (StrUtil.isNotBlank(vo.getTitle())) {
            update.set("title", vo.getTitle());
        }
        if (vo.getContent() != null) {
            update.set("content", vo.getContent());
        }
        if (vo.getType() != null) {
            update.set("type", vo.getType());
        }
        if (vo.getLinkType() != null) {
            update.set("linkType", vo.getLinkType());
        }
        if (vo.getLinkValue() != null) {
            update.set("linkValue", vo.getLinkValue());
        }
        if (vo.getSortNum() != null) {
            update.set("sortNum", vo.getSortNum());
        }
        if (vo.getStatus() != null) {
            update.set("status", vo.getStatus());
        }
        if (vo.getStartTime() != null) {
            update.set("startTime", vo.getStartTime());
        }
        if (vo.getEndTime() != null) {
            update.set("endTime", vo.getEndTime());
        }
        // 社区关联更新
        if (vo.getCommunityId() != null) {
            update.set("communityId", vo.getCommunityId());
            Community community = communityService.getCommunityDetail(vo.getCommunityId());
            update.set("communityName", community.getName());
        }
        noticeDao.updateOneDocument(Criteria.where("noticeId").is(vo.getNoticeId()), update);
        log.info("编辑通知，noticeId={}", vo.getNoticeId());
        return noticeDao.findById(vo.getNoticeId());
    }

    /**
     * 删除通知（逻辑删除）
     *
     * @param noticeId 通知ID
     */
    public void deleteNotice(Long noticeId) {
        Notice notice = noticeDao.findById(noticeId);
        if (notice == null) {
            throw new BizException("通知不存在");
        }
        noticeDao.deleteDocument(noticeId);
        log.info("删除通知，noticeId={}", noticeId);
    }

    /**
     * 切换通知状态（上架/下架）
     *
     * @param noticeId 通知ID
     */
    public void toggleStatus(Long noticeId) {
        Notice notice = noticeDao.findById(noticeId);
        if (notice == null) {
            throw new BizException("通知不存在");
        }
        Integer newStatus = NoticeStatusEnum.PUBLISHED.getCode().equals(notice.getStatus())
                ? NoticeStatusEnum.UNPUBLISHED.getCode()
                : NoticeStatusEnum.PUBLISHED.getCode();
        noticeDao.updateOneDocument(
                Criteria.where("noticeId").is(noticeId),
                new Update().set("status", newStatus));
        log.info("切换通知状态，noticeId={}，newStatus={}", noticeId, newStatus);
    }

    /**
     * 获取通知详情
     *
     * @param noticeId 通知ID
     * @return 通知详情
     */
    public Notice getNoticeDetail(Long noticeId) {
        Notice notice = noticeDao.findById(noticeId);
        if (notice == null) {
            throw new BizException("通知不存在");
        }
        return notice;
    }

    /**
     * 分页查询通知（管理员视角，可查全部状态）
     *
     * @param vo 查询条件
     * @return 通知分页列表
     */
    public Page<Notice> queryNoticePage(NoticeQueryVO vo) {
        int pageIndex = (vo.getPageNumber() != null && vo.getPageNumber() > 0)
                ? vo.getPageNumber() - 1 : 0;
        int pageSize = (vo.getPageSize() != null && vo.getPageSize() > 0)
                ? vo.getPageSize() : 20;
        Pageable pageable = PageRequest.of(pageIndex, pageSize,
                Sort.by(Sort.Order.asc("sortNum"), Sort.Order.desc("createTime")));

        Criteria criteria = new Criteria();
        if (vo.getType() != null) {
            criteria.and("type").is(vo.getType());
        }
        if (vo.getStatus() != null) {
            criteria.and("status").is(vo.getStatus());
        }
        if (vo.getCommunityId() != null) {
            criteria.and("communityId").is(vo.getCommunityId());
        }
        if (StrUtil.isNotBlank(vo.getKeyword())) {
            criteria.and("title").regex(Pattern.quote(vo.getKeyword()));
        }
        return noticeDao.findDocumentPage(criteria, pageable);
    }

    /**
     * C端-获取首页轮播通知列表
     * [新增 2026-08-03 19:30] 查询当前有效的上架通知，按社区+有效期过滤
     * 规则：status=上架 且 当前时间在投放时间范围内 且 (communityId匹配 或 communityId为null的全局通知)
     *
     * @param communityId 当前社区ID（可为null，表示未选择社区时只看全局通知）
     * @return 有效通知列表
     */
    public List<Notice> getActiveNoticeList(Long communityId) {
        Date now = new Date();
        // 使用 andOperator 组合多个独立条件，避免 and/or 混用导致 Criteria 错乱
        Criteria statusCriteria = Criteria.where("status").is(NoticeStatusEnum.PUBLISHED.getCode());
        // 投放时间：startTime为null或<=now
        Criteria startCriteria = new Criteria().orOperator(
                Criteria.where("startTime").is(null),
                Criteria.where("startTime").lte(now)
        );
        // 投放时间：endTime为null或>=now
        Criteria endCriteria = new Criteria().orOperator(
                Criteria.where("endTime").is(null),
                Criteria.where("endTime").gte(now)
        );
        // 社区隔离：communityId匹配 或 全局通知(communityId为null)
        Criteria communityCriteria = new Criteria().orOperator(
                Criteria.where("communityId").is(communityId),
                Criteria.where("communityId").is(null)
        );
        Criteria finalCriteria = new Criteria().andOperator(
                statusCriteria, startCriteria, endCriteria, communityCriteria
        );

        return noticeDao.findDocumentList(finalCriteria,
                Sort.Order.asc("sortNum"),
                Sort.Order.desc("createTime"));
    }

    /**
     * 校验通知类型
     */
    private void validateNoticeType(Integer type) {
        if (type != null && NoticeTypeEnum.getByCode(type) == null) {
            throw new BizException("无效的通知类型");
        }
    }
}
