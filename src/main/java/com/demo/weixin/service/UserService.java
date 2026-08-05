package com.demo.weixin.service;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.demo.common.exception.BizException;
import com.demo.weixin.constant.Constants;
import com.demo.weixin.dao.UserDao;
import com.demo.weixin.entity.User;
import com.demo.weixin.enums.user.UserRoleEnum;
import com.demo.weixin.enums.user.VolunteerStatusEnum;
import com.demo.weixin.service.store.UserPointsService;
import com.demo.weixin.vo.SignInfoVo;
import com.demo.weixin.vo.UserPublicInfoVO;
import com.demo.weixin.vo.VolunteerImportVO;
import com.demo.weixin.vo.VolunteerQueryVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

/**
 * <p>
 * 用户 服务实现类
 * </p>
 *
 * @author zane
 */
@Service
@Slf4j
public class UserService {

    @Autowired
    private UserDao userDao;
    @Autowired
    private RedisTemplate<String, String> stringRedisTemplate;
    @Autowired
    private UserPointsService userPointsService;

    // [新增 2026-08-03 18:40] 签到奖励配置常量
    private static final int DAILY_SIGN_REWARD = 5;    // 每日签到奖励积分
    private static final int WEEKLY_SIGN_REWARD = 20;  // 满周签到奖励积分

    public void setUserSession(String token, User user) {
        stringRedisTemplate.opsForValue().set(Constants.REDIS_SESSION_USER + ":" + token, JSONUtil.toJsonStr(user), Constants.SESSION_EXPIRE_TIME, TimeUnit.SECONDS);
        stringRedisTemplate.opsForValue().set(Constants.REDIS_SESSION_USER + ":" + user.getUserId(), token, Constants.SESSION_EXPIRE_TIME, TimeUnit.SECONDS);
    }

    public void resetUserSession(User user) {
        String oldToken = stringRedisTemplate.opsForValue().get(Constants.REDIS_SESSION_USER + ":" + user.getUserId());
        if (StringUtils.hasText(oldToken)) {
            setUserSession(oldToken,user);
        }
    }



    public List<User> findUserList(Criteria criteria) {
        return userDao.findDocumentList(criteria);
    }

    public List<Map> findDocumentMapByFields(Criteria criteria, String... fieldName) {
        return userDao.findDocumentMapByFields(criteria, fieldName);
    }


    public User getUser(Long userId) {
        return userDao.findById(userId);
    }

    public User updateMap(Map<String, Object> updateMap) {
        return userDao.updateDocument(updateMap);
    }

    public User insertUser(User user) {
        return userDao.insertDocument(user);
    }
    public User saveOrUpdate(User user) {
        return userDao.saveOrUpdate(user);
    }

    public String getSignKey(Long userId) {
        // [修复 2026-08-03 18:40] 修复周六签到周基准计算错误：原逻辑周六会跳到下一周
        // 统一以本周一作为周基准，保证周一到周日使用同一个key
        LocalDate date = LocalDate.now();
        int dayOfWeek = date.getDayOfWeek().getValue(); // 1-7（周一=1，周日=7）
        LocalDate monday = date.minusDays(dayOfWeek - 1);
        return Constants.PROJECT_NAME + ":sign:" + userId + ":" + monday;
    }

    /**
     * 获取签到奖励领取状态key
     * [新增 2026-08-03 18:40] 用于标记本周奖励是否已领取
     */
    private String getSignRewardKey(Long userId) {
        LocalDate date = LocalDate.now();
        int dayOfWeek = date.getDayOfWeek().getValue();
        LocalDate monday = date.minusDays(dayOfWeek - 1);
        return Constants.PROJECT_NAME + ":sign:reward:" + userId + ":" + monday;
    }

    public int getSignCount(Long userId) {
        String signKey = getSignKey(userId);
        Map<Object, Object> map = stringRedisTemplate.opsForHash().entries(signKey);
        int signCount = 0;
        for (int i = 1; i <= 7; i++) {
            String v = (String) map.getOrDefault(String.valueOf(i), "0");
            if ("1".equals(v)) {
                signCount++;
            }
        }
        log.debug("用户 {} 本周签到次数: {}", userId, signCount);
        return signCount;
    }

    /**
     * 执行每日签到
     * [新增 2026-08-03 18:40] 每天签到一次，签到成功奖励5积分
     *
     * @param userId 用户ID
     * @return 签到信息
     */
    public SignInfoVo doSign(Long userId) {
        String signKey = getSignKey(userId);
        int dayOfWeek = LocalDate.now().getDayOfWeek().getValue(); // 1-7
        String dayKey = String.valueOf(dayOfWeek);

        // 检查今日是否已签到
        String signed = (String) stringRedisTemplate.opsForHash().get(signKey, dayKey);
        if ("1".equals(signed)) {
            throw new BizException("今日已签到，明天再来吧");
        }

        // 写入签到记录
        stringRedisTemplate.opsForHash().put(signKey, dayKey, "1");
        // 设置key过期时间为8天（覆盖一个完整自然周+缓冲）
        stringRedisTemplate.expire(signKey, 8, TimeUnit.DAYS);

        // 发放每日签到积分奖励
        userPointsService.add(userId, DAILY_SIGN_REWARD, "每日签到奖励", null);
        log.info("用户 {} 签到成功，奖励积分 {}，dayOfWeek={}", userId, DAILY_SIGN_REWARD, dayOfWeek);

        return getSignInfo(userId);
    }

    /**
     * 获取本周签到信息
     * [新增 2026-08-03 18:40] 返回签到状态、签到次数、奖励领取状态等
     *
     * @param userId 用户ID
     * @return 签到信息
     */
    public SignInfoVo getSignInfo(Long userId) {
        String signKey = getSignKey(userId);
        Map<Object, Object> map = stringRedisTemplate.opsForHash().entries(signKey);
        int dayOfWeek = LocalDate.now().getDayOfWeek().getValue();

        SignInfoVo vo = new SignInfoVo();
        List<Boolean> weekSignStatus = new ArrayList<>();
        int signCount = 0;
        boolean signedToday = false;

        for (int i = 1; i <= 7; i++) {
            String v = (String) map.getOrDefault(String.valueOf(i), "0");
            boolean signed = "1".equals(v);
            weekSignStatus.add(signed);
            if (signed) {
                signCount++;
            }
            if (i == dayOfWeek && signed) {
                signedToday = true;
            }
        }

        vo.setSignCount(signCount);
        vo.setSignedToday(signedToday);
        vo.setWeekSignStatus(weekSignStatus);
        vo.setDailyRewardPoints(DAILY_SIGN_REWARD);
        vo.setWeeklyRewardPoints(WEEKLY_SIGN_REWARD);

        // 检查奖励是否已领取
        String rewardKey = getSignRewardKey(userId);
        String rewardClaimed = stringRedisTemplate.opsForValue().get(rewardKey);
        vo.setRewardClaimed("1".equals(rewardClaimed));

        // 满足7天签到且未领取奖励时可领取
        vo.setClaimReward(signCount >= 7 && !"1".equals(rewardClaimed));

        return vo;
    }

    /**
     * 领取满周签到奖励
     * [新增 2026-08-03 18:40] 满7天签到后可领取20积分额外奖励，每周限领一次
     *
     * @param userId 用户ID
     * @return 签到信息
     */
    public SignInfoVo claimSignReward(Long userId) {
        SignInfoVo info = getSignInfo(userId);
        if (!Boolean.TRUE.equals(info.getClaimReward())) {
            throw new BizException("签到未满7天或奖励已领取，无法领取");
        }

        // 标记奖励已领取（8天过期，覆盖一个自然周）
        String rewardKey = getSignRewardKey(userId);
        stringRedisTemplate.opsForValue().set(rewardKey, "1", 8, TimeUnit.DAYS);

        // 发放满周奖励积分
        userPointsService.add(userId, WEEKLY_SIGN_REWARD, "满周签到奖励", null);
        log.info("用户 {} 领取满周签到奖励，积分 {}", userId, WEEKLY_SIGN_REWARD);

        return getSignInfo(userId);
    }

    public User findOne(Criteria criteria) {
        return userDao.findOne(criteria);
    }

    /**
     * 获取用户公开信息（脱敏）
     * [新增 2026-08-03 19:00] 仅返回昵称、头像、简介等公开字段，不暴露手机号、openId等敏感信息
     *
     * @param userId 目标用户ID
     * @return 用户公开信息VO
     */
    public UserPublicInfoVO getPublicInfo(Long userId) {
        User user = getUser(userId);
        if (user == null) {
            throw new BizException("用户不存在");
        }
        UserPublicInfoVO vo = new UserPublicInfoVO();
        vo.setUserId(user.getUserId());
        vo.setNickName(user.getNickName());
        vo.setAvatar(user.getAvatar());
        vo.setDescription(user.getDescription());
        vo.setCommunityId(user.getCommunityId());
        vo.setCommunityName(user.getCommunityName());
        return vo;
    }

    // ==================== 志愿者模块 [新增 2026-08-03 21:00] ====================

    /**
     * 志愿者ID身份认证
     * [变更 2026-08-03 21:30] 所有用户必须先通过loginByCode建立会话，再在auth环节输入志愿者ID
     * 校验志愿者ID存在且状态正常后，将当前User的role改为志愿者、绑定volunteerId
     * 如果管理员预录入的志愿者记录（无openId）存在，将其信息合并到当前用户后删除孤立记录
     *
     * @param userId      当前登录用户的userId（已有openId和会话）
     * @param volunteerId 用户输入的志愿者ID
     */
    public void bindVolunteerIdentity(Long userId, String volunteerId) {
        // 查找管理员预录入的志愿者记录
        User volunteerRecord = findOne(Criteria.where("volunteerId").is(volunteerId));
        if (volunteerRecord == null) {
            throw new BizException("志愿者ID不存在，请检查后重试");
        }
        // 校验志愿者状态
        if (volunteerRecord.getVolunteerStatus() != null
                && volunteerRecord.getVolunteerStatus().equals(VolunteerStatusEnum.DISABLED.getCode())) {
            throw new BizException("志愿者账号已停用，请联系管理员");
        }

        // 将志愿者身份信息更新到当前用户（当前用户已有openId和会话，保留openId不变）
        Update update = new Update();
        update.set("role", UserRoleEnum.VOLUNTEER.getCode());
        update.set("volunteerId", volunteerId);
        update.set("volunteerStatus", VolunteerStatusEnum.ACTIVE.getCode());
        // 合并预录入志愿者的手机号和社区信息（当前用户没有时才取志愿者的）
        User currentUser = getUser(userId);
        if (StrUtil.isBlank(currentUser.getCellphone()) && StrUtil.isNotBlank(volunteerRecord.getCellphone())) {
            update.set("cellphone", volunteerRecord.getCellphone());
        }
        if (currentUser.getCommunityId() == null && volunteerRecord.getCommunityId() != null) {
            update.set("communityId", volunteerRecord.getCommunityId());
            update.set("communityName", volunteerRecord.getCommunityName());
        }
        if (StrUtil.isBlank(currentUser.getNickName()) && StrUtil.isNotBlank(volunteerRecord.getNickName())) {
            update.set("nickName", volunteerRecord.getNickName());
        }
        userDao.updateOneDocument(Criteria.where("userId").is(userId), update);

        // 删除管理员预录入的孤立志愿者记录（避免重复，当前用户已继承其身份）
        if (!volunteerRecord.getUserId().equals(userId)) {
            userDao.deleteDocument(volunteerRecord.getUserId());
            log.info("删除孤立的志愿者预录入记录，oldUserId={}，volunteerId={}", volunteerRecord.getUserId(), volunteerId);
        }
        log.info("志愿者身份认证成功，userId={}，volunteerId={}", userId, volunteerId);
    }

    /**
     * 管理员录入志愿者
     * [新增 2026-08-03 21:00] 创建User记录，role=2(志愿者)，volunteerStatus=1(正常)
     * 可同时录入手机号，后续志愿者通过微信手机号授权登录时会自动关联到此记录
     *
     * @param vo 志愿者录入入参
     * @return 创建后的User
     */
    public User importVolunteer(VolunteerImportVO vo) {
        // 校验志愿者ID唯一性
        User exist = findOne(Criteria.where("volunteerId").is(vo.getVolunteerId()));
        if (exist != null) {
            throw new BizException("志愿者ID已存在：" + vo.getVolunteerId());
        }
        // 如果录入了手机号，校验手机号是否已关联其他志愿者
        if (StrUtil.isNotBlank(vo.getCellphone())) {
            User existPhone = findOne(Criteria.where("cellphone").is(vo.getCellphone())
                    .and("role").is(UserRoleEnum.VOLUNTEER.getCode()));
            if (existPhone != null) {
                throw new BizException("手机号已关联志愿者：" + existPhone.getVolunteerId());
            }
        }
        User user = new User();
        user.setRole(UserRoleEnum.VOLUNTEER.getCode());
        user.setVolunteerId(vo.getVolunteerId());
        user.setVolunteerStatus(VolunteerStatusEnum.ACTIVE.getCode());
        user.setNickName(StrUtil.isNotBlank(vo.getNickName()) ? vo.getNickName() : "志愿者" + vo.getVolunteerId());
        user.setCellphone(vo.getCellphone());
        user.setCommunityId(vo.getCommunityId());
        user.setCommunityName(vo.getCommunityName());
        user = insertUser(user);
        log.info("录入志愿者，userId={}，volunteerId={}，cellphone={}",
                user.getUserId(), vo.getVolunteerId(), vo.getCellphone());
        return user;
    }

    /**
     * 编辑志愿者信息
     *
     * @param vo 志愿者录入入参（userId必传）
     * @return 更新后的User
     */
    public User updateVolunteer(VolunteerImportVO vo) {
        if (vo.getUserId() == null) {
            throw new BizException("用户ID不能为空");
        }
        User user = getUser(vo.getUserId());
        if (user == null) {
            throw new BizException("用户不存在");
        }
        Update update = new Update();
        if (StrUtil.isNotBlank(vo.getVolunteerId())) {
            // 校验志愿者ID唯一性（排除自身）
            User exist = findOne(Criteria.where("volunteerId").is(vo.getVolunteerId()));
            if (exist != null && !exist.getUserId().equals(vo.getUserId())) {
                throw new BizException("志愿者ID已存在：" + vo.getVolunteerId());
            }
            update.set("volunteerId", vo.getVolunteerId());
        }
        if (StrUtil.isNotBlank(vo.getNickName())) {
            update.set("nickName", vo.getNickName());
        }
        if (vo.getCellphone() != null) {
            update.set("cellphone", vo.getCellphone());
        }
        if (vo.getCommunityId() != null) {
            update.set("communityId", vo.getCommunityId());
        }
        if (vo.getCommunityName() != null) {
            update.set("communityName", vo.getCommunityName());
        }
        userDao.updateOneDocument(Criteria.where("userId").is(vo.getUserId()), update);
        log.info("编辑志愿者，userId={}", vo.getUserId());
        return getUser(vo.getUserId());
    }

    /**
     * 分页查询志愿者列表（管理员视角）
     *
     * @param vo 查询条件
     * @return 志愿者分页列表
     */
    public Page<User> queryVolunteerPage(VolunteerQueryVO vo) {
        int pageIndex = (vo.getPageNumber() != null && vo.getPageNumber() > 0)
                ? vo.getPageNumber() - 1 : 0;
        int pageSize = (vo.getPageSize() != null && vo.getPageSize() > 0)
                ? vo.getPageSize() : 20;
        Pageable pageable = PageRequest.of(pageIndex, pageSize,
                Sort.by(Sort.Order.desc("createTime")));

        Criteria criteria = Criteria.where("role").is(UserRoleEnum.VOLUNTEER.getCode());
        if (vo.getVolunteerStatus() != null) {
            criteria.and("volunteerStatus").is(vo.getVolunteerStatus());
        }
        if (vo.getCommunityId() != null) {
            criteria.and("communityId").is(vo.getCommunityId());
        }
        if (StrUtil.isNotBlank(vo.getKeyword())) {
            String kw = Pattern.quote(vo.getKeyword());
            criteria.andOperator(
                    new Criteria().orOperator(
                            Criteria.where("volunteerId").regex(kw),
                            Criteria.where("nickName").regex(kw)
                    )
            );
        }
        return userDao.findDocumentPage(criteria, pageable);
    }

    /**
     * 切换志愿者状态（启用/停用）
     *
     * @param userId 用户ID
     */
    public void toggleVolunteerStatus(Long userId) {
        User user = getUser(userId);
        if (user == null) {
            throw new BizException("用户不存在");
        }
        if (!UserRoleEnum.VOLUNTEER.getCode().equals(user.getRole())) {
            throw new BizException("该用户不是志愿者");
        }
        Integer newStatus = VolunteerStatusEnum.ACTIVE.getCode().equals(user.getVolunteerStatus())
                ? VolunteerStatusEnum.DISABLED.getCode()
                : VolunteerStatusEnum.ACTIVE.getCode();
        userDao.updateOneDocument(
                Criteria.where("userId").is(userId),
                new Update().set("volunteerStatus", newStatus));
        log.info("切换志愿者状态，userId={}，newStatus={}", userId, newStatus);
    }

    /**
     * 根据手机号查找志愿者记录
     * [新增 2026-08-03 21:00] 微信手机号授权登录时，检查该手机号是否已关联志愿者
     * 如果关联到志愿者记录且该记录无openId，则将openId更新到志愿者记录上，实现身份关联
     *
     * @param cellphone 手机号
     * @return 志愿者User记录（无匹配返回null）
     */
    public User findVolunteerByPhone(String cellphone) {
        if (StrUtil.isBlank(cellphone)) {
            return null;
        }
        return findOne(Criteria.where("cellphone").is(cellphone)
                .and("role").is(UserRoleEnum.VOLUNTEER.getCode()));
    }

    /**
     * 将志愿者记录合并到当前用户
     * [新增 2026-08-03 21:30] 手机号匹配到志愿者记录时，将志愿者身份信息合并到当前用户
     * 当前用户已有openId和会话，保留这些不变，只继承志愿者的role/volunteerId/volunteerStatus
     * 然后删除孤立的志愿者预录入记录
     *
     * @param currentUserId 当前登录用户ID（有openId）
     * @param volunteerUserId 管理员预录入的志愿者记录ID
     */
    public void mergeVolunteerToCurrentUser(Long currentUserId, Long volunteerUserId) {
        User volunteer = getUser(volunteerUserId);
        if (volunteer == null) {
            return;
        }
        Update update = new Update();
        update.set("role", UserRoleEnum.VOLUNTEER.getCode());
        update.set("volunteerId", volunteer.getVolunteerId());
        update.set("volunteerStatus", volunteer.getVolunteerStatus());
        userDao.updateOneDocument(Criteria.where("userId").is(currentUserId), update);
        // 删除孤立的志愿者预录入记录
        userDao.deleteDocument(volunteerUserId);
        log.info("志愿者记录合并到当前用户，currentUserId={}，volunteerUserId={}，volunteerId={}",
                currentUserId, volunteerUserId, volunteer.getVolunteerId());
    }
}
