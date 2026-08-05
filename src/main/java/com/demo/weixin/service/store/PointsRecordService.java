package com.demo.weixin.service.store;

import com.demo.weixin.dao.store.PointsRecordDao;
import com.demo.weixin.entity.store.PointsRecord;
import com.demo.weixin.vo.store.PointsRecordQueryVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

/**
 * 积分流水服务
 * [新增 2026-08-03 19:10] 提供积分流水分页查询功能
 */
@Service
@Slf4j
public class PointsRecordService {

    @Autowired
    private PointsRecordDao pointsRecordDao;

    /**
     * 分页查询用户积分流水
     *
     * @param userId  用户ID
     * @param queryVO 查询入参
     * @return 分页结果
     */
    public Page<PointsRecord> getRecordPage(Long userId, PointsRecordQueryVO queryVO) {
        // 前端页码从1开始，MongoDB从0开始
        int pageIndex = (queryVO.getPageNumber() != null && queryVO.getPageNumber() > 0)
                ? queryVO.getPageNumber() - 1 : 0;
        int pageSize = (queryVO.getPageSize() != null && queryVO.getPageSize() > 0)
                ? queryVO.getPageSize() : 20;

        PageRequest pageable = PageRequest.of(pageIndex, pageSize,
                Sort.by(Sort.Order.desc("createTime")));

        Criteria criteria = Criteria.where("userId").is(userId);
        if (queryVO.getType() != null) {
            criteria.and("type").is(queryVO.getType());
        }

        return pointsRecordDao.findDocumentPage(criteria, pageable);
    }
}
