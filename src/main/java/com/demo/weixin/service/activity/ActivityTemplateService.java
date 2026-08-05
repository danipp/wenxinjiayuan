package com.demo.weixin.service.activity;

import com.demo.common.exception.BizException;
import com.demo.weixin.dao.activity.ActivityTemplateDao;
import com.demo.weixin.entity.activity.ActivityTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

/**
 * 活动模板服务
 * 提供模板列表查询、模板详情、使用模板（计数+1）功能。
 * 用户基于模板创建活动时调用 useTemplate，返回模板数据并递增使用次数。
 */
@Service
@Slf4j
public class ActivityTemplateService {

    @Autowired
    private ActivityTemplateDao activityTemplateDao;

    /**
     * 获取活动模板列表（分页）
     * 按排序权重和使用次数降序排列。
     *
     * @param pageNumber 页码（从0开始）
     * @param pageSize   每页条数
     * @param category   分类筛选（可选）
     * @return 分页模板列表
     */
    public Page<ActivityTemplate> getTemplateList(Integer pageNumber, Integer pageSize, String category) {
        Sort sort = Sort.by(
                Sort.Order.desc("sort"),
                Sort.Order.desc("usedCount"));
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        Criteria criteria = new Criteria();
        if (category != null && !category.isEmpty()) {
            criteria.and("category").is(category);
        }
        return activityTemplateDao.findDocumentPage(criteria, pageable);
    }

    /**
     * 获取模板详情
     *
     * @param templateId 模板ID
     * @return 模板详情
     */
    public ActivityTemplate getTemplateDetail(Long templateId) {
        ActivityTemplate template = activityTemplateDao.findById(templateId);
        if (template == null) {
            throw new BizException("活动模板不存在");
        }
        return template;
    }

    /**
     * 使用模板创建活动（递增使用次数并返回模板数据）
     *
     * @param templateId 模板ID
     * @return 模板详情（用于填充创建活动表单）
     */
    public ActivityTemplate useTemplate(Long templateId) {
        ActivityTemplate template = activityTemplateDao.findById(templateId);
        if (template == null) {
            throw new BizException("活动模板不存在");
        }
        // 递增使用次数
        activityTemplateDao.updateOneDocument(
                Criteria.where("templateId").is(templateId),
                new Update().inc("usedCount", 1));
        // $inc 返回的是递增前的旧值，这里手动将返回对象的 usedCount +1，使调用方拿到最新值
        int currentUsedCount = template.getUsedCount() == null ? 0 : template.getUsedCount();
        template.setUsedCount(currentUsedCount + 1);
        log.info("使用活动模板，templateId={}，title={}，usedCount={}", templateId, template.getTitle(), template.getUsedCount());
        return template;
    }
}
