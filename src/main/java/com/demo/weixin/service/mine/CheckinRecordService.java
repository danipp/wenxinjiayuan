package com.demo.weixin.service.mine;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.demo.common.exception.BizException;
import com.demo.weixin.dao.mine.CheckinRecordDao;
import com.demo.weixin.entity.mine.CheckinRecord;
import com.demo.weixin.vo.mine.CheckinRecordQueryVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 打卡记录服务
 * 提供打卡记录的分页查询、累计统计和创建功能。
 */
@Service
@Slf4j
public class CheckinRecordService {

    @Autowired
    private CheckinRecordDao checkinRecordDao;

    /**
     * 分页查询当前用户的打卡记录
     *
     * @param userId  当前用户ID
     * @param queryVO 分页查询入参
     * @return 打卡记录分页结果
     */
    public Page<CheckinRecord> getCheckinPage(Long userId, CheckinRecordQueryVO queryVO) {
        Pageable pageable = PageRequest.of(queryVO.getPageNumber(), queryVO.getPageSize(),
                Sort.by(Sort.Order.desc("checkinTime")));
        Criteria criteria = Criteria.where("userId").is(userId);
        return checkinRecordDao.findDocumentPage(criteria, pageable);
    }

    /**
     * 获取用户累计打卡数
     *
     * @param userId 用户ID
     * @return 累计打卡次数
     */
    public long getCheckinCount(Long userId) {
        return checkinRecordDao.count(Criteria.where("userId").is(userId));
    }

    /**
     * 创建打卡记录
     * 打卡时间自动设置为当前时间，状态默认为1（成功）。
     *
     * @param userId     用户ID
     * @param frameNo    相框编号
     * @param frameName  相框名称
     * @param frameImage 相框图片URL
     * @param location   打卡位置
     * @return 创建后的打卡记录
     */
    public CheckinRecord createCheckinRecord(Long userId, String frameNo, String frameName,
                                             String frameImage, String location) {
        if (StrUtil.isBlank(frameNo)) {
            throw new BizException("相框编号不能为空");
        }
        // 重复打卡防护：同一用户同一相框当天只能打卡一次（createTime 在今天 00:00:00 ~ 23:59:59）
        Date now = new Date();
        CheckinRecord existCheckin = checkinRecordDao.findOne(
                Criteria.where("userId").is(userId)
                        .and("frameNo").is(frameNo)
                        .and("createTime").gte(DateUtil.beginOfDay(now)).lte(DateUtil.endOfDay(now)));
        if (existCheckin != null) {
            throw new BizException("今日已打卡，请明天再来");
        }
        CheckinRecord record = new CheckinRecord();
        record.setUserId(userId);
        record.setFrameNo(frameNo);
        record.setFrameName(frameName);
        record.setFrameImage(frameImage);
        record.setLocation(location);
        record.setCheckinTime(new Date());
        record.setStatus(1);
        checkinRecordDao.insertDocument(record);
        log.info("创建打卡记录，recordId={}，userId={}，frameNo={}", record.getRecordId(), userId, frameNo);
        return record;
    }
}
