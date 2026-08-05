package com.demo.weixin.service;

import cn.hutool.core.util.StrUtil;
import com.demo.weixin.dao.SystemAuditLogDao;
import com.demo.weixin.entity.SystemAuditLog;
import com.demo.weixin.vo.AuditLogQueryVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * <p>
 * 系统审计日志
 * </p>
 *
 * @author Zane
 */
@Service
@Slf4j
public class SystemAuditLogService {

    @Autowired
    private SystemAuditLogDao systemAuditLogDao;

    @Async("logTaskExecutor")
    public void saveLogAsync(SystemAuditLog auditLog) {
        try {
            systemAuditLogDao.insertDocument(auditLog);
        } catch (Exception e) {
            log.error("异步保存系统审计日志失败，错误信息: {}", e.getMessage(), e);
        }
    }

    public Page<SystemAuditLog> queryLogPage(AuditLogQueryVO queryVO) {

        String orderBy = "updateTime";
        Pageable pageable = PageRequest.of(queryVO.getPageNumber(), queryVO.getPageSize(), Sort.by(Sort.Order.desc(orderBy)));

        Criteria criteria = new Criteria();
        // 模块筛选
        if (StrUtil.isNotBlank(queryVO.getModule())) {
            criteria.and("module").is(queryVO.getModule());
        }
        // 操作员筛选
        if (StrUtil.isNotBlank(queryVO.getOperatorName())) {
            criteria.and("operatorName").regex(queryVO.getOperatorName());
        }
        // 状态筛选
        if (queryVO.getStatus() != null) {
            criteria.and("status").is(queryVO.getStatus());
        }
        // 时间范围筛选
        if (queryVO.getStartTime() != null && queryVO.getEndTime() != null) {
            criteria.and("createTime").gte(queryVO.getStartTime()).lte(queryVO.getEndTime());
        }

        return systemAuditLogDao.findDocumentPage(criteria, pageable);

    }

    public long clearOldLogs(Date beforeDate) {
        Criteria criteria = Criteria.where("createTime").lt(beforeDate);
        long count = systemAuditLogDao.count(criteria);
        systemAuditLogDao.deleteDocumentPhisiclly(criteria);
        log.info("成功清理 {} 条 {} 之前的历史审计日志", count, beforeDate);
        return count;
    }
}
