package com.demo.weixin.constant;

/**
 * @author jagger
 */
public interface Constants {

    String PROJECT_NAME = "WARM_HOME";

    int ERROR_LIMIT_COUNT = 10;   //2小时内操作限制次数
    int LIMIT_FREQUENT_HOUR_INSTANCE = 2;   //操作频繁 限制时间
    long SESSION_EXPIRE_TIME = 60 * 60 * 24 * 365;
    //req header params
    String TOKEN = "token"; //用户-header头token字段
    String PLATFORM = "plat-form"; //用户-header头token字段
    String POS_ID = "pos-id"; //用户-header头token字段
    String STORE_ID = "storeid"; //用户-header头token字段
    String WIFI_ID = "wifi-id"; //用户-header头token字段
    String GOODS_TYPE = "goods-type"; //用户-header头token字段
    String ROLE = "role"; //用户当前操作角色-header头（agent/developer/merchant）
    String TOKEN_ADMIN = "dtoken"; //代理-header头token字段
    String REDIS_SESSION_USER = PROJECT_NAME + ":token:session";    //用户登录信息存储token
    String REDIS_SESSION_ADMIN = PROJECT_NAME + ":admin:token:session";    //用户登录信息存储token
    String USER_FROZEN = PROJECT_NAME + ":user_frozen"; //用户冻结redis标志
    String ADMIN_USER_FROZEN = PROJECT_NAME + ":admin:user_frozen"; //用户冻结redis标志
    String LIMIT_COUNT = PROJECT_NAME + ":limit:count"; //操作频繁默认redis路径

    // ==================== 商城分布式幂等锁前缀 ====================
    String LOCK_STORE_CREATE_ORDER = "lock:store:createOrder";        //商城-创建订单
    String LOCK_STORE_VERIFY = "lock:store:verify";                   //商城-核销订单
    String LOCK_STORE_REFUND = "lock:store:refund";                   //商城-退款处理（同意退款）
    String LOCK_STORE_REQUEST_REFUND = "lock:store:requestRefund";    //商城-申请退款
    String LOCK_STORE_REJECT_REFUND = "lock:store:rejectRefund";      //商城-拒绝退款
    String LOCK_STORE_CANCEL = "lock:store:cancel";                   //商城-取消订单
    String LOCK_STORE_COMMENT = "lock:store:comment";                 //商城-创建评价

    // ==================== 活动分布式幂等锁前缀 ====================
    String LOCK_ACTIVITY_CREATE = "lock:activity:create";        //活动-创建活动
    String LOCK_ACTIVITY_SIGNUP = "lock:activity:signup";         //活动-报名
    String LOCK_ACTIVITY_COMMENT = "lock:activity:comment";       //活动-写评价
    String LOCK_ACTIVITY_PHOTO_LIKE = "lock:activity:photoLike";  //活动-照片点赞
    String LOCK_ACTIVITY_PHOTO_UPLOAD = "lock:activity:photo:upload:";  //活动-照片上传

    // ==================== 个人中心分布式幂等锁前缀 ====================
    String LOCK_MINE_CHECKIN = "lock:mine:checkin";              //个人中心-打卡
    String LOCK_MINE_CONTACT_SAVE = "lock:mine:contactSave";     //个人中心-保存联系人
    String LOCK_MINE_FOLLOW = "lock:mine:follow";                //个人中心-关注
    String LOCK_MINE_DEMAND_CREATE = "lock:mine:demandCreate";   //个人中心-发布需求
    String LOCK_MINE_DEMAND_ACCEPT = "lock:mine:demandAccept";   //个人中心-接单
    String LOCK_MINE_DEMAND_EVALUATE = "lock:mine:demandEvaluate"; //个人中心-评价需求

    // ==================== 居民认证分布式幂等锁前缀 ====================
    String LOCK_MINE_CERTIFICATION_SUBMIT = "lock:mine:certificationSubmit"; //个人中心-提交认证

    // ==================== 服务对象分布式幂等锁前缀 ====================
    String LOCK_MINE_SERVICE_MEMBER_SAVE = "lock:mine:serviceMemberSave"; //个人中心-保存服务对象
    String LOCK_MINE_UNFOLLOW = "lock:mine:unfollow:";                //个人中心-取消关注
    String LOCK_MINE_DEMAND_COMPLETE = "lock:mine:demand:complete:";  //个人中心-完成需求

    // ==================== 消费帮扶分布式幂等锁前缀 ====================
    String LOCK_ASSISTANCE_DONATION = "lock:assistance:donation";     //消费帮扶-提交捐赠申请
    String LOCK_ASSISTANCE_APPLY = "lock:assistance:apply";           //消费帮扶-提交帮扶申请
    String LOCK_ASSISTANCE_CLAIM = "lock:assistance:claim";           //消费帮扶-物资申领

    // ==================== 社区分布式幂等锁前缀 ====================
    // [新增 2026-08-03 17:00] 社区管理操作锁
    String LOCK_COMMUNITY_SAVE = "lock:community:save";               //社区-新增/编辑社区

    // ==================== 签到分布式幂等锁前缀 ====================
    // [新增 2026-08-03 18:40] 签到及领取奖励操作锁
    String LOCK_MINE_SIGN = "lock:mine:sign";               //个人中心-每日签到
    String LOCK_MINE_SIGN_REWARD = "lock:mine:signReward";  //个人中心-领取签到奖励

    // ==================== 通知管理分布式幂等锁前缀 ====================
    // [新增 2026-08-03 19:30] 通知管理操作锁
    String LOCK_NOTICE_SAVE = "lock:notice:save";           //通知-新增/编辑通知

    // ==================== 志愿者管理分布式幂等锁前缀 ====================
    // [新增 2026-08-03 21:00] 志愿者管理操作锁
    String LOCK_VOLUNTEER_IMPORT = "lock:volunteer:import"; //志愿者-录入
    String LOCK_VOLUNTEER_UPDATE = "lock:volunteer:update"; //志愿者-编辑

    // ==================== 社区特惠分布式幂等锁前缀 ====================
    // [新增 2026-08-04 10:00] 社区特惠模块操作锁
    String LOCK_SPECIAL_CATEGORY_SAVE = "lock:special:categorySave";   //社区特惠-分类保存
    String LOCK_SPECIAL_COUPON_SAVE = "lock:special:couponSave";       //社区特惠-优惠券保存
    String LOCK_SPECIAL_COUPON_CLAIM = "lock:special:couponClaim";     //社区特惠-领取优惠券
    String LOCK_SPECIAL_REVIEW_CREATE = "lock:special:reviewCreate";   //社区特惠-创建评价
}
