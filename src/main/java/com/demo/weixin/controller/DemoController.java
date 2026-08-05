package com.demo.weixin.controller;

import com.demo.common.core.result.Result;
import com.demo.common.core.util.SpringBeanContextHolder;
import com.demo.common.exception.BizException;
import com.demo.weixin.annotation.NeedLogin;
import com.demo.weixin.controller.BaseController;
import com.demo.weixin.dao.activity.ActivityTemplateDao;
import com.demo.weixin.dao.mine.DemandRecordDao;
import com.demo.weixin.entity.activity.Activity;
import com.demo.weixin.entity.activity.ActivityTemplate;
import com.demo.weixin.entity.mine.DemandRecord;
import com.demo.weixin.entity.store.StoreGoods;
import com.demo.weixin.entity.store.StoreShop;
import com.demo.weixin.enums.activity.ActivityTypeEnum;
import com.demo.weixin.enums.mine.CertificationStatusEnum;
import com.demo.weixin.enums.mine.DemandStatusEnum;
import com.demo.weixin.service.UserService;
import com.demo.weixin.service.activity.ActivityCommentService;
import com.demo.weixin.service.activity.ActivityPhotoService;
import com.demo.weixin.service.activity.ActivityService;
import com.demo.weixin.service.ad.AdService;
import com.demo.weixin.service.mine.CheckinRecordService;
import com.demo.weixin.service.mine.DemandRecordService;
import com.demo.weixin.service.mine.EmergencyContactService;
import com.demo.weixin.service.mine.ResidentCertificationService;
import com.demo.weixin.service.mine.ServiceMemberService;
import com.demo.weixin.service.store.PaymentService;
import com.demo.weixin.service.store.StoreGoodsService;
import com.demo.weixin.service.store.StoreShopService;
import com.demo.weixin.service.store.UserPointsService;
import com.demo.weixin.service.assistance.AssistanceApplyService;
import com.demo.weixin.service.assistance.AssistanceStatService;
import com.demo.weixin.service.assistance.CharityEnterpriseService;
import com.demo.weixin.service.assistance.DonationService;
import com.demo.weixin.service.assistance.GoodsClaimService;
import com.demo.weixin.service.community.CommunityService;
import com.demo.weixin.service.notice.NoticeService;
import com.demo.weixin.vo.activity.ActivityCommentVO;
import com.demo.weixin.vo.activity.ActivityCreateVO;
import com.demo.weixin.vo.ad.AdSaveVO;
import com.demo.weixin.vo.mine.CertificationAuditVO;
import com.demo.weixin.vo.mine.DemandCreateVO;
import com.demo.weixin.vo.mine.EmergencyContactVO;
import com.demo.weixin.vo.mine.ResidentCertificationVO;
import com.demo.weixin.vo.mine.ServiceMemberVO;
import com.demo.weixin.vo.demo.AddPointsVO;
import com.demo.weixin.vo.demo.SimulateAuditVO;
import com.demo.weixin.vo.demo.SimulatePayVO;
import com.demo.weixin.vo.demo.SimulateRefundVO;
import com.demo.weixin.vo.store.GoodsCreateVO;
import com.demo.weixin.vo.store.ShopCreateVO;
import com.demo.weixin.vo.assistance.AssistanceApplyVO;
import com.demo.weixin.vo.assistance.AssistanceAuditVO;
import com.demo.weixin.vo.assistance.DonationApplyVO;
import com.demo.weixin.vo.assistance.DonationAuditVO;
import com.demo.weixin.vo.assistance.EnterpriseSaveVO;
import com.demo.weixin.vo.assistance.GoodsClaimVO;
import com.demo.weixin.vo.community.CommunitySaveVO;
import com.demo.weixin.vo.notice.NoticeSaveVO;
import com.demo.weixin.vo.VolunteerImportVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 调试演示控制器
 * <p>
 * 仅在开发（devp）和测试（test）环境可用，生产环境自动拒绝。
 * 用途：
 * 1. 生成商城测试数据（店铺、商品、积分），供前端联调
 * 2. 模拟微信支付回调（本地开发无真实回调时使用）
 * 3. 给当前用户充值积分（测试兑换流程）
 * </p>
 */
@RestController
@Tag(name = "调试-演示数据")
@RequestMapping("/api/demo")
@Slf4j
public class DemoController extends BaseController {

    @Autowired
    private StoreGoodsService storeGoodsService;
    @Autowired
    private StoreShopService storeShopService;
    @Autowired
    private UserPointsService userPointsService;
    @Autowired
    private PaymentService paymentService;
    @Autowired
    private ActivityService activityService;
    @Autowired
    private ActivityCommentService activityCommentService;
    @Autowired
    private ActivityPhotoService activityPhotoService;
    @Autowired
    private CheckinRecordService checkinRecordService;
    @Autowired
    private EmergencyContactService emergencyContactService;
    @Autowired
    private DemandRecordService demandRecordService;
    @Autowired
    private AdService adService;
    @Autowired
    private ResidentCertificationService residentCertificationService;
    @Autowired
    private ServiceMemberService serviceMemberService;
    @Autowired
    private ActivityTemplateDao activityTemplateDao;
    @Autowired
    private DemandRecordDao demandRecordDao;
    // [新增 2026-08-02] 消费帮扶模块服务
    @Autowired
    private DonationService donationService;
    @Autowired
    private AssistanceApplyService assistanceApplyService;
    @Autowired
    private CharityEnterpriseService charityEnterpriseService;
    @Autowired
    private GoodsClaimService goodsClaimService;
    @Autowired
    private AssistanceStatService assistanceStatService;
    // [新增 2026-08-03 17:50] 社区管理服务
    @Autowired
    private CommunityService communityService;
    // [新增 2026-08-03 19:30] 通知管理服务
    @Autowired
    private NoticeService noticeService;
    @Autowired
    private UserService userService;
    // [新增 2026-08-04 10:00] 社区特惠模块服务
    @Autowired
    private com.demo.weixin.service.special.SpecialCategoryService specialCategoryService;
    @Autowired
    private com.demo.weixin.service.special.ShopCouponService shopCouponService;
    @Autowired
    private com.demo.weixin.service.special.ShopReviewService shopReviewService;
    @Autowired
    private com.demo.weixin.dao.store.StoreShopDao storeShopDao;

    /**
     * 环境安全检查：仅开发/测试环境可用
     * 二次校验：applicationContext 未初始化时直接拒绝。
     * isDevpEnv()/isTestEnv() 在 applicationContext 为 null 时会误判为 true，存在越权风险，此处显式拦截。
     */
    private void checkEnv() {
        if (SpringBeanContextHolder.getApplicationContext() == null) {
            throw new BizException("调试接口仅在开发/测试环境可用");
        }
        if (!SpringBeanContextHolder.isDevpEnv() && !SpringBeanContextHolder.isTestEnv()) {
            throw new BizException("调试接口仅在开发/测试环境可用");
        }
    }

    @PostMapping("/initData")
    @Operation(summary = "生成商城测试数据（店铺+商品+积分）",
            responses = {@ApiResponse(description = "成功信息", content = @Content(schema = @Schema(implementation = String.class)))})
    @NeedLogin
    public Result<String> initData() {
        checkEnv();
        Long userId = getCurrentUserId();
        int createdCount = 0;

        // 1. 创建或获取测试店铺
        StoreShop shop = storeShopService.getShopBySeller(userId);
        if (shop == null) {
            ShopCreateVO shopVO = new ShopCreateVO();
            shopVO.setName("社区公益超市（测试）");
            shopVO.setLogo("https://demo.example.com/logo.png");
            shopVO.setPhone("13800138000");
            shopVO.setAddress("社区服务中心一楼");
            shopVO.setDescription("社区公益测试店铺，商品仅供调试使用");
            // [新增 2026-08-03 17:50] 关联第一个社区用于数据隔离
            List<com.demo.weixin.entity.community.Community> communities = communityService.getActiveCommunityList();
            if (!communities.isEmpty()) {
                shopVO.setCommunityId(communities.get(0).getCommunityId());
            }
            shop = storeShopService.saveOrUpdateShop(userId, shopVO);
            createdCount++;
        }

        // 2. 创建测试商品（覆盖各场景和支付方式）
        List<StoreGoods> existGoods = storeGoodsService.queryShopGoods(shop.getShopId());
        if (existGoods == null || existGoods.isEmpty()) {
            // 积分兑换商品1（志愿者商城场景）
            createdCount += createDemoGoods(userId, shop.getShopId(), "爱心毛巾（积分兑换）",
                    50, BigDecimal.ZERO, 1, 100, "日用百货", "volunteer");
            // 积分兑换商品2（积分商城场景）
            createdCount += createDemoGoods(userId, shop.getShopId(), "环保购物袋（积分兑换）",
                    120, BigDecimal.ZERO, 1, 50, "日用百货", "points");
            // 现金购买商品1（消费帮扶场景）
            createdCount += createDemoGoods(userId, shop.getShopId(), "社区特产蜂蜜（现金购买）",
                    0, new BigDecimal("39.90"), 2, 30, "食品生鲜", "assistance");
            // 现金购买商品2（消费帮扶场景）
            createdCount += createDemoGoods(userId, shop.getShopId(), "手工编织围巾（现金购买）",
                    0, new BigDecimal("68.00"), 2, 20, "服装鞋帽", "assistance");
            // [新增 2026-07-31 18:04] 混合支付商品1（志愿者商城场景：积分+现金）
            createdCount += createDemoGoods(userId, shop.getShopId(), "社区有机蔬菜礼盒（混合支付）",
                    30, new BigDecimal("29.90"), 3, 40, "食品生鲜", "volunteer");
            // [新增 2026-07-31 18:04] 混合支付商品2（消费帮扶场景：积分+现金）
            createdCount += createDemoGoods(userId, shop.getShopId(), "助农苹果一箱（混合支付）",
                    50, new BigDecimal("45.00"), 3, 25, "食品生鲜", "assistance");
            // 打卡相框商品（scene=frame，积分兑换）
            createdCount += createDemoFrameGoods(userId, shop.getShopId(), "NFC打卡相框-6寸（积分兑换）",
                    "FRAME-NFC-001", "6寸", "社区活动打卡", "社区自提",
                    200, 10);
            createdCount += createDemoFrameGoods(userId, shop.getShopId(), "NFC打卡相框-8寸（积分兑换）",
                    "FRAME-NFC-002", "8寸", "志愿服务打卡", "社区配送",
                    350, 5);
        }

        // 3. 给当前用户充值测试积分
        userPointsService.add(userId, 1000);
        createdCount++;

        log.info("测试数据生成完成，userId={}，创建条目数={}", userId, createdCount);
        return Result.success("测试数据生成成功，共创建 " + createdCount + " 条数据，已为当前用户充值1000积分");
    }

    /**
     * [新增 2026-08-02] 生成消费帮扶测试数据
     * 包含：爱心企业、捐赠申请、帮扶申请、物资申领、统计配置。
     */
    @PostMapping("/initAssistanceData")
    @Operation(summary = "生成消费帮扶测试数据（企业+捐赠+帮扶+申领+统计）",
            responses = {@ApiResponse(description = "成功信息", content = @Content(schema = @Schema(implementation = String.class)))})
    @NeedLogin
    public Result<String> initAssistanceData() {
        checkEnv();
        Long userId = getCurrentUserId();
        int createdCount = 0;

        // [新增 2026-08-03 17:50] 查询社区ID用于数据隔离
        List<com.demo.weixin.entity.community.Community> communities = communityService.getActiveCommunityList();
        Long communityId = !communities.isEmpty() ? communities.get(0).getCommunityId() : null;

        // 1. 初始化默认统计配置
        assistanceStatService.initDefaultStats();
        createdCount += 4;

        // 2. 创建爱心企业（2家）
        EnterpriseSaveVO enterprise1 = new EnterpriseSaveVO();
        enterprise1.setName("阳光食品有限公司");
        enterprise1.setLogo("https://demo.example.com/enterprise1.png");
        enterprise1.setDescription("致力于社区消费帮扶的爱心食品企业，累计捐赠物资超5000份");
        enterprise1.setContactName("张经理");
        enterprise1.setContactPhone("13800001111");
        enterprise1.setAddress("社区服务中心旁");
        enterprise1.setSort(1);
        enterprise1.setCommunityId(communityId);
        charityEnterpriseService.saveOrUpdateEnterprise(enterprise1);
        createdCount++;

        EnterpriseSaveVO enterprise2 = new EnterpriseSaveVO();
        enterprise2.setName("绿源农业合作社");
        enterprise2.setLogo("https://demo.example.com/enterprise2.png");
        enterprise2.setDescription("绿色农产品直供社区，助农扶贫专项合作企业");
        enterprise2.setContactName("李理事");
        enterprise2.setContactPhone("13800002222");
        enterprise2.setAddress("城郊农业园区");
        enterprise2.setSort(2);
        enterprise2.setCommunityId(communityId);
        charityEnterpriseService.saveOrUpdateEnterprise(enterprise2);
        createdCount++;

        // 3. 提交捐赠申请（2条：1条资金已审核通过，1条物资待审核）
        DonationApplyVO donation1 = new DonationApplyVO();
        donation1.setUserType("enterprise");
        donation1.setEnterpriseId(1L);
        donation1.setDonationType("money");
        donation1.setAmount(new BigDecimal("5000"));
        donation1.setContactName("张经理");
        donation1.setContactPhone("13800001111");
        donation1.setRemark("企业定向捐赠消费帮扶基金");
        donation1.setCommunityId(communityId);
        donationService.submitDonation(userId, donation1);
        createdCount++;

        DonationApplyVO donation2 = new DonationApplyVO();
        donation2.setUserType("individual");
        donation2.setDonationType("goods");
        donation2.setGoodsName("大米");
        donation2.setGoodsQuantity(20);
        donation2.setGoodsValue(new BigDecimal("600"));
        donation2.setContactName("王居民");
        donation2.setContactPhone("13800003333");
        donation2.setRemark("个人捐赠大米20袋");
        donation2.setCommunityId(communityId);
        donationService.submitDonation(userId, donation2);
        createdCount++;

        // 4. 提交帮扶申请（2条：1条待审核，1条已审核通过）
        AssistanceApplyVO apply1 = new AssistanceApplyVO();
        apply1.setApplicantName("刘困难");
        apply1.setApplicantPhone("13800004444");
        apply1.setAddress("社区3栋5单元");
        apply1.setFamilySituation("低保家庭，夫妻双方失业，有一子在读");
        apply1.setAssistanceType("living");
        apply1.setDifficultyDesc("家庭收入微薄，日常生活困难");
        apply1.setDesiredHelp("希望获得生活物资帮扶");
        apply1.setCommunityId(communityId);
        assistanceApplyService.submitApply(userId, apply1);
        createdCount++;

        AssistanceApplyVO apply2 = new AssistanceApplyVO();
        apply2.setApplicantName("陈大病");
        apply2.setApplicantPhone("13800005555");
        apply2.setAddress("社区7栋2单元");
        apply2.setFamilySituation("因重大手术产生高额医疗费用");
        apply2.setAssistanceType("medical");
        apply2.setDifficultyDesc("手术费用沉重，后续康复经济压力大");
        apply2.setDesiredHelp("希望获得医疗费用帮扶");
        apply2.setCommunityId(communityId);
        assistanceApplyService.submitApply(userId, apply2);
        createdCount++;

        // 5. 提交物资申领（2条：1条待审核，1条已审核通过）
        // 先查询消费帮扶场景的商品用于申领
        GoodsClaimVO claim1 = new GoodsClaimVO();
        claim1.setGoodsId(1L);
        claim1.setClaimCount(1);
        claim1.setClaimReason("家庭困难，申请领取爱心物资");
        claim1.setContactName("刘困难");
        claim1.setContactPhone("13800004444");
        claim1.setAddress("社区3栋5单元");
        goodsClaimService.submitClaim(userId, claim1);
        createdCount++;

        GoodsClaimVO claim2 = new GoodsClaimVO();
        claim2.setGoodsId(2L);
        claim2.setClaimCount(2);
        claim2.setClaimReason("医疗费用负担重，申请领取生活物资");
        claim2.setContactName("陈大病");
        claim2.setContactPhone("13800005555");
        claim2.setAddress("社区7栋2单元");
        goodsClaimService.submitClaim(userId, claim2);
        createdCount++;

        log.info("消费帮扶测试数据生成完成，userId={}，创建条目数={}", userId, createdCount);
        return Result.success("消费帮扶测试数据生成成功，共创建 " + createdCount + " 条数据（含4条统计配置、2家企业、2条捐赠、2条帮扶、2条申领）");
    }

    @PostMapping("/simulatePay")
    @Operation(summary = "模拟支付回调（本地开发无微信回调时使用）",
            responses = {@ApiResponse(description = "成功信息", content = @Content(schema = @Schema(implementation = String.class)))})
    @NeedLogin
    public Result<String> simulatePay(@RequestBody SimulatePayVO vo) {
        checkEnv();
        boolean success = vo.getSuccess() != null ? vo.getSuccess() : true;
        // Demo模拟回调无真实微信流水号，传null
        paymentService.handlePayCallback(vo.getOrderNum(), success, null);
        String msg = success ? "支付成功回调模拟完成" : "支付失败回调模拟完成（订单已取消）";
        log.info("模拟支付回调，orderNum={}，success={}", vo.getOrderNum(), success);
        return Result.success(msg);
    }

    @PostMapping("/simulateRefund")
    @Operation(summary = "模拟退款回调（本地开发无微信回调时使用）",
            responses = {@ApiResponse(description = "成功信息", content = @Content(schema = @Schema(implementation = String.class)))})
    @NeedLogin
    public Result<String> simulateRefund(@RequestBody SimulateRefundVO vo) {
        checkEnv();
        boolean success = vo.getSuccess() != null ? vo.getSuccess() : true;
        // 退款单号规则：outRefundNo = "R" + orderNum
        String outRefundNo = "R" + vo.getOrderNum();
        // Demo模拟回调无真实微信退款流水号，传null
        paymentService.handleRefundCallback(outRefundNo, success, null);
        String msg = success ? "退款成功回调模拟完成（订单已流转至已退款）" : "退款失败回调模拟完成（paymentStatus回退至REFUND_APPLY）";
        log.info("模拟退款回调，orderNum={}，outRefundNo={}，success={}", vo.getOrderNum(), outRefundNo, success);
        return Result.success(msg);
    }

    @PostMapping("/addPoints")
    @Operation(summary = "给当前用户充值积分（测试用）",
            responses = {@ApiResponse(description = "成功信息", content = @Content(schema = @Schema(implementation = Integer.class)))})
    @NeedLogin
    public Result<Integer> addPoints(@RequestBody AddPointsVO vo) {
        checkEnv();
        Integer points = vo.getPoints() != null ? vo.getPoints() : 1000;
        userPointsService.add(getCurrentUserId(), points);
        Integer balance = userPointsService.getOrCreate(getCurrentUserId()).getBalance();
        log.info("测试积分充值，userId={}，amount={}，当前余额={}", getCurrentUserId(), points, balance);
        return Result.success(balance);
    }

    @GetMapping("/points")
    @Operation(summary = "查询当前用户积分余额",
            responses = {@ApiResponse(description = "成功信息", content = @Content(schema = @Schema(implementation = Integer.class)))})
    @NeedLogin
    public Result<Integer> points() {
        checkEnv();
        return Result.success(userPointsService.getOrCreate(getCurrentUserId()).getBalance());
    }

    /**
     * 创建测试商品的辅助方法
     * [变更 2026-07-31 18:04] 新增 scene 参数，适配商品场景字段。
     *
     * @param scene       商品场景（volunteer/points/assistance/frame）
     * @return 1=创建成功
     */
    private int createDemoGoods(Long sellerUserId, Long shopId, String title, int pointsPrice,
                                 BigDecimal cashPrice, int goodsType, int stock, String category, String scene) {
        GoodsCreateVO vo = new GoodsCreateVO();
        vo.setShopId(shopId);
        vo.setTitle(title);
        vo.setDescription("这是测试商品，用于前端联调。" + title);
        vo.setCoverImage("https://demo.example.com/goods_" + System.currentTimeMillis() + ".png");
        vo.setCarouselImages(new ArrayList<>(Arrays.asList(
                "https://demo.example.com/goods_img1.png",
                "https://demo.example.com/goods_img2.png"
        )));
        vo.setPointsPrice(pointsPrice);
        vo.setCashPrice(cashPrice);
        vo.setOriginalPrice(cashPrice.compareTo(BigDecimal.ZERO) > 0
                ? cashPrice.multiply(new BigDecimal("1.5")) : BigDecimal.ZERO);
        vo.setStock(stock);
        vo.setGoodsType(goodsType);
        // [新增 2026-07-31 18:04] 设置商品场景
        vo.setScene(scene);
        vo.setCategory(category);
        vo.setSpecs("{\"来源\":\"社区直供\",\"保质期\":\"12个月\"}");
        storeGoodsService.createGoods(sellerUserId, vo);
        return 1;
    }

    /**
     * 创建打卡相框测试商品的辅助方法
     * GoodsCreateVO已支持相框专属字段，通过storeGoodsService.createGoods统一创建。
     *
     * @param shopId      店铺ID
     * @param title       商品标题
     * @param frameNo     相框编号
     * @param frameSize   规格尺寸
     * @param scene       适用场景
     * @param delivery    配送方式
     * @param pointsPrice 积分价格
     * @param stock       库存
     * @return 1=创建成功
     */
    private int createDemoFrameGoods(Long sellerUserId, Long shopId, String title, String frameNo,
                                      String frameSize, String scene, String delivery,
                                      int pointsPrice, int stock) {
        GoodsCreateVO vo = new GoodsCreateVO();
        vo.setShopId(shopId);
        vo.setTitle(title);
        vo.setDescription("NFC打卡相框，碰一碰即可完成社区活动打卡。" + title);
        vo.setCoverImage("https://demo.example.com/frame_" + System.currentTimeMillis() + ".png");
        vo.setCarouselImages(new ArrayList<>(Arrays.asList(
                "https://demo.example.com/frame_img1.png",
                "https://demo.example.com/frame_img2.png"
        )));
        vo.setPointsPrice(pointsPrice);
        vo.setCashPrice(BigDecimal.ZERO);
        vo.setOriginalPrice(BigDecimal.ZERO);
        vo.setStock(stock);
        // [变更 2026-07-31 18:04] goodsType 改为支付方式（1积分兑换），scene 标识相框场景
        vo.setGoodsType(1);  // 1=积分兑换
        vo.setScene("frame");  // 打卡相框场景
        vo.setCategory("打卡相框");
        vo.setSpecs("{\"材质\":\"亚克力\",\"NFC\":\"支持\"}");
        // 相框专属字段
        vo.setFrameNo(frameNo);
        vo.setFrameSize(frameSize);
        // [变更 2026-07-31 18:04] 原 scene 参数实际是场景描述，改用 sceneDesc 字段
        vo.setSceneDesc(scene);
        vo.setDelivery(delivery);
        vo.setFeatures(Arrays.asList("NFC碰一碰", "快速打卡", "活动记录"));
        storeGoodsService.createGoods(sellerUserId, vo);
        log.info("创建相框测试商品，frameNo={}", frameNo);
        return 1;
    }

    /**
     * 创建已完成帮忙记录的辅助方法
     * 正式流程需要其他用户发布需求→当前用户接单→完成→评价，demo直接构造已完成记录。
     * publisherUserId 使用一个不存在的虚拟用户ID（999999），helperUserId 设为当前用户，
     * 状态设为已完成并带评价，为达人排行榜提供聚合数据。
     *
     * @param helperUserId   帮忙者用户ID（当前用户）
     * @param title          需求标题
     * @param content        需求内容
     * @param location       服务地点
     * @param requirement    需求类型
     * @param rating         评价评分
     * @param evaluateContent 评价内容
     * @param communityId    所属社区ID（数据隔离用）
     * @return 1=创建成功
     */
    private int createDemoCompletedDemand(Long helperUserId, String title, String content,
                                           String location, String requirement,
                                           int rating, String evaluateContent, Long communityId) {
        DemandRecord record = new DemandRecord();
        // 虚拟发布者ID（不对应真实用户，仅用于demo数据）
        record.setPublisherUserId(999999L);
        record.setHelperUserId(helperUserId);
        record.setTitle(title);
        record.setContent(content);
        record.setLocation(location);
        record.setServiceTime(new Date(System.currentTimeMillis() - 86400000L));
        record.setRequirement(requirement);
        record.setStatus(DemandStatusEnum.COMPLETED.getCode());
        record.setRole(2);  // 帮忙者视角
        record.setRating(rating);
        record.setEvaluateContent(evaluateContent);
        record.setEvaluateTime(new Date());
        // [新增 2026-08-03 17:50] 设置社区ID用于数据隔离
        record.setCommunityId(communityId);
        demandRecordDao.insertDocument(record);
        log.info("创建已完成帮忙记录，demandId={}，helperUserId={}，title={}", record.getDemandId(), helperUserId, title);
        return 1;
    }

    // ==================== 活动模块测试数据 ====================

    /**
     * 生成活动测试数据
     * 创建2个活动（1个进行中、1个已结束），并为已结束活动生成评价和照片。
     */
    @PostMapping("/initActivityData")
    @Operation(summary = "生成活动测试数据（活动+评价+照片）",
            responses = {@ApiResponse(description = "成功信息", content = @Content(schema = @Schema(implementation = String.class)))})
    @NeedLogin
    public Result<String> initActivityData() {
        checkEnv();
        Long userId = getCurrentUserId();
        int createdCount = 0;

        // [新增 2026-08-03 17:50] 查询社区ID用于数据隔离
        List<com.demo.weixin.entity.community.Community> communities = communityService.getActiveCommunityList();
        Long communityId = !communities.isEmpty() ? communities.get(0).getCommunityId() : null;
        String communityName = !communities.isEmpty() ? communities.get(0).getName() : "中山一社区";

        // 1. 创建进行中的活动
        ActivityCreateVO ongoingVO = new ActivityCreateVO();
        ongoingVO.setTitle("中山一社区居民瓜子会");
        ongoingVO.setContent("欢迎大家携家带口来中山一社区参加线下居民嗑瓜子闲聊交友会！现场提供多口味爱心瓜子与茶水。");
        ongoingVO.setLocation("越秀区富力新天地中心");
        // 开始时间为1小时前，结束时间为2小时后
        ongoingVO.setStartTime(new Date(System.currentTimeMillis() - 3600000));
        ongoingVO.setEndTime(new Date(System.currentTimeMillis() + 7200000));
        ongoingVO.setCommunity(communityName);
        ongoingVO.setCommunityId(communityId);
        ongoingVO.setMaxLimit(50);
        ongoingVO.setCollectPhone(true);
        ongoingVO.setType(2);
        ongoingVO.setCoverImage("https://cdn.uviewui.com/uview/album/5.jpg");
        ongoingVO.setTag("社区活动");
        Activity ongoing = activityService.createActivity(userId, ongoingVO);
        createdCount++;

        // 2. 创建已结束的活动（可写评价和上传照片）
        ActivityCreateVO endedVO = new ActivityCreateVO();
        endedVO.setTitle("社区环保公益捡跑活动");
        endedVO.setContent("一起跑步一起环保，为社区环境贡献一份力量！活动已圆满结束。");
        endedVO.setLocation("社区公园环形跑道");
        // 开始时间为3天前，结束时间为2天前
        endedVO.setStartTime(new Date(System.currentTimeMillis() - 3 * 86400000L));
        endedVO.setEndTime(new Date(System.currentTimeMillis() - 2 * 86400000L));
        endedVO.setCommunity(communityName);
        endedVO.setCommunityId(communityId);
        endedVO.setMaxLimit(0);
        endedVO.setCollectPhone(false);
        endedVO.setType(2);
        endedVO.setCoverImage("https://cdn.uviewui.com/uview/album/6.jpg");
        endedVO.setTag("公益活动");
        Activity ended = activityService.createActivity(userId, endedVO);
        createdCount++;

        // 3. 为已结束活动创建测试评价
        ActivityCommentVO commentVO = new ActivityCommentVO();
        commentVO.setActivityId(ended.getActivityId());
        commentVO.setScore(5);
        commentVO.setEmoji("😆");
        commentVO.setStatusText("远超预期");
        commentVO.setContent("活动组织得很好，现场氛围很轻松，邻居们都聊得很开心。");
        try {
            activityCommentService.createComment(userId, commentVO);
            createdCount++;
        } catch (Exception e) {
            log.warn("创建测试评价失败（可能已存在）：{}", e.getMessage());
        }

        // 4. 为已结束活动上传测试照片
        try {
            activityPhotoService.uploadPhoto(userId, ended.getActivityId(),
                    "https://cdn.uviewui.com/uview/album/8.jpg");
            activityPhotoService.uploadPhoto(userId, ended.getActivityId(),
                    "https://cdn.uviewui.com/uview/album/6.jpg");
            createdCount += 2;
        } catch (Exception e) {
            log.warn("上传测试照片失败：{}", e.getMessage());
        }

        // 5. 创建招募活动（type=3，志愿者招募）
        ActivityCreateVO recruitVO = new ActivityCreateVO();
        recruitVO.setTitle("社区长者陪伴志愿者招募");
        recruitVO.setContent("招募志愿者为社区独居老人提供定期陪伴服务，每周探访2次，每次1小时。欢迎有爱心、有时间的邻居报名！");
        recruitVO.setLocation("中山一社区服务中心");
        // 招募活动周期较长：开始时间为明天，结束时间为30天后
        recruitVO.setStartTime(new Date(System.currentTimeMillis() + 86400000L));
        recruitVO.setEndTime(new Date(System.currentTimeMillis() + 30 * 86400000L));
        recruitVO.setCommunity(communityName);
        recruitVO.setCommunityId(communityId);
        recruitVO.setMaxLimit(20);
        recruitVO.setCollectPhone(true);
        recruitVO.setType(ActivityTypeEnum.RECRUITMENT.getCode());  // type=3 招募活动
        recruitVO.setCoverImage("https://cdn.uviewui.com/uview/album/9.jpg");
        recruitVO.setTag("志愿者招募");
        activityService.createActivity(userId, recruitVO);
        createdCount++;

        log.info("活动测试数据生成完成，userId={}，创建条目数={}", userId, createdCount);
        return Result.success("活动测试数据生成成功，共创建 " + createdCount + " 条数据（2个社区活动+1个招募活动+1条评价+2张照片）");
    }

    // ==================== 个人中心模块测试数据 ====================

    /**
     * 生成个人中心测试数据
     * 创建打卡记录、紧急联系人、需求记录。
     */
    @PostMapping("/initMineData")
    @Operation(summary = "生成个人中心测试数据（打卡+联系人+需求）",
            responses = {@ApiResponse(description = "成功信息", content = @Content(schema = @Schema(implementation = String.class)))})
    @NeedLogin
    public Result<String> initMineData() {
        checkEnv();
        Long userId = getCurrentUserId();
        int createdCount = 0;

        // [新增 2026-08-03 17:50] 查询社区ID用于数据隔离
        List<com.demo.weixin.entity.community.Community> communities = communityService.getActiveCommunityList();
        Long communityId = !communities.isEmpty() ? communities.get(0).getCommunityId() : null;

        // 1. 创建测试打卡记录
        long existCheckin = checkinRecordService.getCheckinCount(userId);
        if (existCheckin == 0) {
            checkinRecordService.createCheckinRecord(userId, "FRAME-202607-001",
                    "社区活动室打卡相框", "/static/frames/frame1.png",
                    "财厅前社区 | 社区活动室");
            createdCount++;
            checkinRecordService.createCheckinRecord(userId, "FRAME-202607-002",
                    "邻里服务站打卡相框", "/static/frames/frame2.png",
                    "财厅前社区 | 邻里服务站");
            createdCount++;
        }

        // 2. 创建测试紧急联系人
        List<?> existContacts = emergencyContactService.getContactList(userId);
        if (existContacts == null || existContacts.isEmpty()) {
            EmergencyContactVO contact1 = new EmergencyContactVO();
            contact1.setName("陈阿姨");
            contact1.setPhone("13800138000");
            contact1.setRelation("母亲");
            emergencyContactService.saveOrUpdateContact(userId, contact1);
            createdCount++;

            EmergencyContactVO contact2 = new EmergencyContactVO();
            contact2.setName("李先生");
            contact2.setPhone("13900139000");
            contact2.setRelation("邻居");
            emergencyContactService.saveOrUpdateContact(userId, contact2);
            createdCount++;
        }

        // 3. 创建测试需求记录
        DemandCreateVO demand1 = new DemandCreateVO();
        demand1.setTitle("上门除尘与衣物整理");
        demand1.setContent("需要志愿者帮忙上门做除尘和衣物整理工作");
        demand1.setLocation("越秀区青菜岗43号启东楼");
        demand1.setServiceTime(new Date(System.currentTimeMillis() + 2 * 86400000L));
        demand1.setRequirement("家政保洁");
        demand1.setCommunityId(communityId);
        demandRecordService.createDemand(userId, demand1);
        createdCount++;

        DemandCreateVO demand2 = new DemandCreateVO();
        demand2.setTitle("陪同医院做检查与取药");
        demand2.setContent("老人需要陪同前往医院做常规检查和取药");
        demand2.setLocation("广州市越秀区中医医院");
        demand2.setServiceTime(new Date(System.currentTimeMillis() + 3 * 86400000L));
        demand2.setRequirement("陪护");
        demand2.setCommunityId(communityId);
        demandRecordService.createDemand(userId, demand2);
        createdCount++;

        // 4. 插入已完成的帮忙记录（当前用户作为helperUserId），为排行榜提供数据
        // 正式流程需要其他用户发布需求→当前用户接单→完成→评价，demo直接构造已完成记录
        createdCount += createDemoCompletedDemand(userId, "帮王大爷代购生活物资",
                "代购米面油等生活物资送到老人家中", "越秀区中山一路",
                "代购", 5, "服务很贴心，送货上门很方便", communityId);
        createdCount += createDemoCompletedDemand(userId, "陪同李奶奶去医院取药",
                "陪同老人前往社区医院取药并安全送回家", "越秀区中医医院",
                "陪护", 5, "非常有耐心，老人很满意", communityId);
        createdCount += createDemoCompletedDemand(userId, "帮张阿姨修理水管",
                "厨房水管漏水，需要师傅上门维修", "越秀区青菜岗43号",
                "维修", 4, "维修及时，手艺不错", communityId);

        log.info("个人中心测试数据生成完成，userId={}，创建条目数={}", userId, createdCount);
        return Result.success("个人中心测试数据生成成功，共创建 " + createdCount + " 条数据（2打卡+2联系人+2待帮忙需求+3已完成帮忙记录）");
    }

    // ==================== 居民认证测试数据 ====================

    /**
     * 生成居民认证测试数据
     * 为当前用户提交一条待审核的居民认证记录。
     */
    @PostMapping("/initCertificationData")
    @Operation(summary = "生成居民认证测试数据（提交认证申请）",
            responses = {@ApiResponse(description = "成功信息", content = @Content(schema = @Schema(implementation = String.class)))})
    @NeedLogin
    public Result<String> initCertificationData() {
        checkEnv();
        Long userId = getCurrentUserId();

        ResidentCertificationVO vo = new ResidentCertificationVO();
        vo.setPhone("13800138000");
        vo.setCommunityName("中山一社区");
        vo.setRealName("张三");
        vo.setIdCard("440104199001011234");
        vo.setAddress("越秀区中山一路88号2栋301房");
        residentCertificationService.submitCertification(userId, vo);

        log.info("居民认证测试数据生成完成，userId={}", userId);
        return Result.success("居民认证测试数据生成成功，已提交1条待审核认证记录");
    }

    /**
     * 模拟审核通过当前用户的居民认证
     * 正式审核需要管理员调用 /api/mine/certification/audit，本地调试时用此接口快速模拟。
     */
    @PostMapping("/simulateAuditCertification")
    @Operation(summary = "模拟审核通过居民认证（本地调试用）",
            responses = {@ApiResponse(description = "成功信息", content = @Content(schema = @Schema(implementation = String.class)))})
    @NeedLogin
    public Result<String> simulateAuditCertification(@RequestBody SimulateAuditVO vo) {
        checkEnv();
        boolean approved = vo.getApproved() != null ? vo.getApproved() : true;
        Long userId = getCurrentUserId();
        // 查询当前用户的认证记录
        com.demo.weixin.entity.mine.ResidentCertification cert = residentCertificationService.getMyCertification(userId);
        if (cert == null) {
            return Result.failed("当前用户未提交认证，请先调用 /initCertificationData");
        }
        // 构造审核入参，调用正式审核接口
        CertificationAuditVO auditVO = new CertificationAuditVO();
        auditVO.setCertificationId(cert.getCertificationId());
        if (approved) {
            auditVO.setStatus(CertificationStatusEnum.APPROVED.getCode());
            auditVO.setAuditRemark("Demo模拟审核通过");
        } else {
            auditVO.setStatus(CertificationStatusEnum.REJECTED.getCode());
            auditVO.setAuditRemark("Demo模拟审核拒绝");
        }
        residentCertificationService.auditCertification(auditVO);
        log.info("模拟审核认证完成，userId={}，result={}", userId, approved ? "通过" : "拒绝");
        return Result.success(approved ? "认证审核通过模拟完成" : "认证审核拒绝模拟完成");
    }

    // ==================== 服务对象测试数据 ====================

    /**
     * 生成服务对象测试数据
     * 为当前用户创建2条服务对象记录（老人/小孩），用于发布需求时快速选择。
     */
    @PostMapping("/initServiceMemberData")
    @Operation(summary = "生成服务对象测试数据（被服务人信息）",
            responses = {@ApiResponse(description = "成功信息", content = @Content(schema = @Schema(implementation = String.class)))})
    @NeedLogin
    public Result<String> initServiceMemberData() {
        checkEnv();
        Long userId = getCurrentUserId();
        int createdCount = 0;

        // 检查是否已有服务对象，避免重复创建
        List<?> existMembers = serviceMemberService.getMemberList(userId);
        if (existMembers != null && !existMembers.isEmpty()) {
            return Result.success("服务对象已存在，跳过创建");
        }

        // 1. 服务对象-老人
        ServiceMemberVO member1 = new ServiceMemberVO();
        member1.setName("张奶奶");
        member1.setPhone("13800138001");
        member1.setAddress("越秀区中山一路88号");
        member1.setDetailAddress("2栋301房");
        member1.setRemark("独居老人，行动不便，需定期探访");
        serviceMemberService.saveOrUpdateMember(userId, member1);
        createdCount++;

        // 2. 服务对象-小孩
        ServiceMemberVO member2 = new ServiceMemberVO();
        member2.setName("小明");
        member2.setPhone("13800138002");
        member2.setAddress("越秀区中山一路88号");
        member2.setDetailAddress("2栋302房");
        member2.setRemark("留守儿童，放学后需陪伴看护");
        serviceMemberService.saveOrUpdateMember(userId, member2);
        createdCount++;

        log.info("服务对象测试数据生成完成，userId={}，创建条目数={}", userId, createdCount);
        return Result.success("服务对象测试数据生成成功，共创建 " + createdCount + " 条服务对象记录");
    }

    // ==================== 活动模板测试数据 ====================

    /**
     * 生成活动模板测试数据
     * 创建3个预设活动模板（社区活动、志愿服务、招募活动），供用户基于模板快速创建活动。
     * ActivityTemplateService无create方法，直接通过Dao插入。
     */
    @PostMapping("/initTemplateData")
    @Operation(summary = "生成活动模板测试数据（预设活动模板）",
            responses = {@ApiResponse(description = "成功信息", content = @Content(schema = @Schema(implementation = String.class)))})
    @NeedLogin
    public Result<String> initTemplateData() {
        checkEnv();
        int createdCount = 0;

        // 检查是否已有模板数据
        long existCount = activityTemplateDao.count(new org.springframework.data.mongodb.core.query.Criteria());
        if (existCount > 0) {
            return Result.success("活动模板已存在，跳过创建");
        }

        // 1. 社区活动模板（线下活动）
        ActivityTemplate tpl1 = new ActivityTemplate();
        tpl1.setTitle("社区邻里茶话会");
        tpl1.setContent("邀请社区居民参加茶话会，增进邻里感情，交流生活趣事。");
        tpl1.setCoverImage("https://cdn.uviewui.com/uview/album/5.jpg");
        tpl1.setImage("https://cdn.uviewui.com/uview/album/5.jpg");
        tpl1.setTag("社区活动");
        tpl1.setType(ActivityTypeEnum.OFFLINE.getCode());
        tpl1.setMaxLimit(50);
        tpl1.setParticipants("128");
        tpl1.setUsedCount(12);
        tpl1.setTotalJoined(128);
        tpl1.setCategory("社区活动");
        tpl1.setSort(100);
        activityTemplateDao.insertDocument(tpl1);
        createdCount++;

        // 2. 志愿服务模板（线下活动）
        ActivityTemplate tpl2 = new ActivityTemplate();
        tpl2.setTitle("社区环保清洁志愿行动");
        tpl2.setContent("组织志愿者对社区公共区域进行清洁，共建美好家园。");
        tpl2.setCoverImage("https://cdn.uviewui.com/uview/album/6.jpg");
        tpl2.setImage("https://cdn.uviewui.com/uview/album/6.jpg");
        tpl2.setTag("志愿服务");
        tpl2.setType(ActivityTypeEnum.OFFLINE.getCode());
        tpl2.setMaxLimit(30);
        tpl2.setParticipants("256");
        tpl2.setUsedCount(8);
        tpl2.setTotalJoined(256);
        tpl2.setCategory("志愿服务");
        tpl2.setSort(90);
        activityTemplateDao.insertDocument(tpl2);
        createdCount++;

        // 3. 招募活动模板（招募活动）
        ActivityTemplate tpl3 = new ActivityTemplate();
        tpl3.setTitle("长者陪伴志愿者招募");
        tpl3.setContent("招募志愿者为社区独居老人提供定期陪伴服务，每周探访2次。");
        tpl3.setCoverImage("https://cdn.uviewui.com/uview/album/9.jpg");
        tpl3.setImage("https://cdn.uviewui.com/uview/album/9.jpg");
        tpl3.setTag("志愿者招募");
        tpl3.setType(ActivityTypeEnum.RECRUITMENT.getCode());
        tpl3.setMaxLimit(20);
        tpl3.setParticipants("89");
        tpl3.setUsedCount(5);
        tpl3.setTotalJoined(89);
        tpl3.setCategory("招募活动");
        tpl3.setSort(80);
        activityTemplateDao.insertDocument(tpl3);
        createdCount++;

        log.info("活动模板测试数据生成完成，创建条目数={}", createdCount);
        return Result.success("活动模板测试数据生成成功，共创建 " + createdCount + " 个活动模板");
    }

    // ==================== 广告位测试数据 ====================

    /**
     * 生成广告位测试数据
     * 创建首页Banner广告和商城Banner广告。
     */
    @PostMapping("/initAdData")
    @Operation(summary = "生成广告位测试数据（首页Banner+商城Banner）",
            responses = {@ApiResponse(description = "成功信息", content = @Content(schema = @Schema(implementation = String.class)))})
    @NeedLogin
    public Result<String> initAdData() {
        checkEnv();
        int createdCount = 0;

        // 1. 首页Banner广告
        AdSaveVO homeAd = new AdSaveVO();
        homeAd.setPosition("home_banner");
        homeAd.setTitle("社区志愿者招募中");
        homeAd.setImageUrl("https://cdn.uviewui.com/uview/album/1.jpg");
        homeAd.setLinkType(1);
        homeAd.setLinkValue("1");
        homeAd.setSortNum(1);
        adService.saveOrUpdate(homeAd);
        createdCount++;

        AdSaveVO homeAd2 = new AdSaveVO();
        homeAd2.setPosition("home_banner");
        homeAd2.setTitle("爱心积分商城上线啦");
        homeAd2.setImageUrl("https://cdn.uviewui.com/uview/album/2.jpg");
        homeAd2.setLinkType(3);
        homeAd2.setLinkValue("1");
        homeAd2.setSortNum(2);
        adService.saveOrUpdate(homeAd2);
        createdCount++;

        // 2. 商城Banner广告
        AdSaveVO storeAd = new AdSaveVO();
        storeAd.setPosition("store_banner");
        storeAd.setTitle("新人专享积分兑换");
        storeAd.setImageUrl("https://cdn.uviewui.com/uview/album/3.jpg");
        storeAd.setLinkType(2);
        storeAd.setLinkValue("1");
        storeAd.setSortNum(1);
        adService.saveOrUpdate(storeAd);
        createdCount++;

        log.info("广告位测试数据生成完成，创建条目数={}", createdCount);
        return Result.success("广告位测试数据生成成功，共创建 " + createdCount + " 条广告数据（2首页Banner+1商城Banner）");
    }

    // ==================== 社区测试数据 ====================

    /**
     * [新增 2026-08-03 17:50] 生成社区测试数据
     * 创建3个社区（中山一社区、财厅前社区、东湖新村社区），供前端社区切换功能联调。
     */
    @PostMapping("/initCommunityData")
    @Operation(summary = "生成社区测试数据（3个社区）",
            responses = {@ApiResponse(description = "成功信息", content = @Content(schema = @Schema(implementation = String.class)))})
    @NeedLogin
    public Result<String> initCommunityData() {
        checkEnv();
        int createdCount = 0;

        // 检查是否已有社区数据，避免重复创建
        if (!communityService.getActiveCommunityList().isEmpty()) {
            return Result.success("社区数据已存在，跳过创建");
        }

        // 1. 中山一社区
        CommunitySaveVO community1 = new CommunitySaveVO();
        community1.setName("中山一社区");
        community1.setAddress("广州市越秀区中山一路88号");
        community1.setContactName("陈主任");
        community1.setContactPhone("020-87654321");
        community1.setDescription("中山一社区是越秀区中心城区社区，现有居民3000余户，社区活动丰富。");
        community1.setLogo("https://demo.example.com/community1.png");
        community1.setSort(1);
        communityService.createCommunity(community1);
        createdCount++;

        // 2. 财厅前社区
        CommunitySaveVO community2 = new CommunitySaveVO();
        community2.setName("财厅前社区");
        community2.setAddress("广州市越秀区北京路财厅前");
        community2.setContactName("李主任");
        community2.setContactPhone("020-87654322");
        community2.setDescription("财厅前社区位于北京路商圈核心区域，历史文化底蕴深厚。");
        community2.setLogo("https://demo.example.com/community2.png");
        community2.setSort(2);
        communityService.createCommunity(community2);
        createdCount++;

        // 3. 东湖新村社区
        CommunitySaveVO community3 = new CommunitySaveVO();
        community3.setName("东湖新村社区");
        community3.setAddress("广州市越秀区东湖路123号");
        community3.setContactName("王主任");
        community3.setContactPhone("020-87654323");
        community3.setDescription("东湖新村社区临近东山湖公园，环境优美，居民和谐友爱。");
        community3.setLogo("https://demo.example.com/community3.png");
        community3.setSort(3);
        communityService.createCommunity(community3);
        createdCount++;

        log.info("社区测试数据生成完成，创建条目数={}", createdCount);
        return Result.success("社区测试数据生成成功，共创建 " + createdCount + " 个社区（中山一社区、财厅前社区、东湖新村社区）");
    }

    // ==================== 通知模块测试数据 ====================

    /**
     * [新增 2026-08-03 19:30] 生成首页消息轮播通知测试数据
     * 创建多条不同类型的通知，覆盖系统公告、社区活动、捐赠播报、帮扶动态，
     * 以及不同状态（上架/下架）和投放时间（长期/限时），供前端首页轮播组件联调。
     */
    @PostMapping("/initNoticeData")
    @Operation(summary = "生成通知测试数据（首页消息轮播）",
            responses = {@ApiResponse(description = "成功信息", content = @Content(schema = @Schema(implementation = String.class)))})
    @NeedLogin
    public Result<String> initNoticeData() {
        checkEnv();
        int createdCount = 0;

        // 检查是否已有通知数据，避免重复创建
        com.demo.weixin.vo.notice.NoticeQueryVO queryVO = new com.demo.weixin.vo.notice.NoticeQueryVO();
        queryVO.setPageNumber(1);
        queryVO.setPageSize(1);
        if (!noticeService.queryNoticePage(queryVO).isEmpty()) {
            return Result.success("通知数据已存在，跳过创建");
        }

        // 查询社区ID用于数据隔离
        List<com.demo.weixin.entity.community.Community> communities = communityService.getActiveCommunityList();
        Long communityId = !communities.isEmpty() ? communities.get(0).getCommunityId() : null;

        // 1. 系统公告（全局通知，不关联社区，长期有效）
        NoticeSaveVO notice1 = new NoticeSaveVO();
        notice1.setTitle("社区积分商城上新，快来兑换好物！");
        notice1.setContent("社区积分商城已上新多款爱心商品，包括日用百货、生鲜食品等，参与志愿活动赚取积分即可兑换。");
        notice1.setType(1);  // 1=系统公告
        notice1.setLinkType(3);  // 3=跳转店铺
        notice1.setLinkValue("1");
        notice1.setSortNum(1);
        notice1.setStatus(1);  // 1=上架
        noticeService.createNotice(notice1);
        createdCount++;

        // 2. 社区活动通知（关联第一个社区，跳转活动详情）
        NoticeSaveVO notice2 = new NoticeSaveVO();
        notice2.setTitle("本周六社区邻里茶话会，欢迎报名参加！");
        notice2.setContent("中山一社区本周六下午举办邻里茶话会，现场提供茶点，欢迎居民携家带口参加，共叙邻里情。");
        notice2.setType(2);  // 2=社区活动
        notice2.setCommunityId(communityId);
        notice2.setLinkType(1);  // 1=跳转活动
        notice2.setLinkValue("1");
        notice2.setSortNum(2);
        notice2.setStatus(1);
        noticeService.createNotice(notice2);
        createdCount++;

        // 3. 捐赠播报通知（关联第一个社区，不跳转）
        NoticeSaveVO notice3 = new NoticeSaveVO();
        notice3.setTitle("爱心企业阳光食品捐赠5000元消费帮扶基金");
        notice3.setContent("感谢阳光食品有限公司定向捐赠消费帮扶基金5000元，用于社区困难家庭物资帮扶。");
        notice3.setType(3);  // 3=捐赠播报
        notice3.setCommunityId(communityId);
        notice3.setLinkType(0);  // 0=不跳转
        notice3.setSortNum(3);
        notice3.setStatus(1);
        noticeService.createNotice(notice3);
        createdCount++;

        // 4. 帮扶动态通知（全局通知，跳转外部链接）
        NoticeSaveVO notice4 = new NoticeSaveVO();
        notice4.setTitle("本月帮扶物资发放完成，共惠及32户家庭");
        notice4.setContent("本月消费帮扶物资已全部发放完毕，共惠及社区32户困难家庭，感谢所有爱心企业和个人的支持。");
        notice4.setType(4);  // 4=帮扶动态
        notice4.setLinkType(0);
        notice4.setSortNum(4);
        notice4.setStatus(1);
        noticeService.createNotice(notice4);
        createdCount++;

        // 5. 限时投放通知（投放时间范围：今天到7天后，测试时间过滤）
        NoticeSaveVO notice5 = new NoticeSaveVO();
        notice5.setTitle("【限时】志愿者招募中，报名即送积分");
        notice5.setContent("社区长者陪伴志愿者招募中，本周内报名参加可获额外积分奖励，名额有限，先到先得。");
        notice5.setType(2);
        notice5.setCommunityId(communityId);
        notice5.setLinkType(1);
        notice5.setLinkValue("1");
        notice5.setSortNum(5);
        notice5.setStatus(1);
        notice5.setStartTime(new Date());  // 立即生效
        notice5.setEndTime(new Date(System.currentTimeMillis() + 7 * 86400000L));  // 7天后结束
        noticeService.createNotice(notice5);
        createdCount++;

        // 6. 已下架通知（测试下架状态不展示）
        NoticeSaveVO notice6 = new NoticeSaveVO();
        notice6.setTitle("【已下架】旧版积分兑换规则说明（已失效）");
        notice6.setContent("此通知为测试下架状态，C端不应展示。");
        notice6.setType(1);
        notice6.setSortNum(99);
        notice6.setStatus(2);  // 2=下架
        noticeService.createNotice(notice6);
        createdCount++;

        log.info("通知测试数据生成完成，创建条目数={}", createdCount);
        return Result.success("通知测试数据生成成功，共创建 " + createdCount + " 条通知（4上架+1限时+1下架）");
    }

    // ==================== 志愿者测试数据 ====================

    /**
     * [新增 2026-08-03 21:00] 生成志愿者测试数据
     * 预录入3个志愿者（含志愿者ID、手机号、社区关联），供前端志愿者身份认证联调。
     * 流程：用户先通过 /api/mini/loginByCode 微信登录 → 再调用 /api/mini/auth 传 volunteerId 完成志愿者身份认证。
     * 也可通过手机号授权登录时自动匹配志愿者记录（如果手机号已预录入）。
     */
    @PostMapping("/initVolunteerData")
    @Operation(summary = "生成志愿者测试数据（预录入3个志愿者ID）",
            responses = {@ApiResponse(description = "成功信息", content = @Content(schema = @Schema(implementation = String.class)))})
    @NeedLogin
    public Result<String> initVolunteerData() {
        checkEnv();
        int createdCount = 0;

        // 查询社区ID用于数据隔离
        List<com.demo.weixin.entity.community.Community> communities = communityService.getActiveCommunityList();
        Long communityId = !communities.isEmpty() ? communities.get(0).getCommunityId() : null;
        String communityName = !communities.isEmpty() ? communities.get(0).getName() : "中山一社区";

        // 检查是否已有志愿者数据
        com.demo.weixin.vo.VolunteerQueryVO checkVO = new com.demo.weixin.vo.VolunteerQueryVO();
        checkVO.setPageNumber(1);
        checkVO.setPageSize(1);
        if (!userService.queryVolunteerPage(checkVO).isEmpty()) {
            return Result.success("志愿者数据已存在，跳过创建");
        }

        // 1. 志愿者1：有手机号，可通过ID或微信手机号授权登录
        VolunteerImportVO volunteer1 = new VolunteerImportVO();
        volunteer1.setVolunteerId("V20260001");
        volunteer1.setNickName("张志愿");
        volunteer1.setCellphone("13800138001");
        volunteer1.setCommunityId(communityId);
        volunteer1.setCommunityName(communityName);
        userService.importVolunteer(volunteer1);
        createdCount++;

        // 2. 志愿者2：有手机号，不同社区
        VolunteerImportVO volunteer2 = new VolunteerImportVO();
        volunteer2.setVolunteerId("V20260002");
        volunteer2.setNickName("李志愿");
        volunteer2.setCellphone("13800138002");
        volunteer2.setCommunityId(communityId);
        volunteer2.setCommunityName(communityName);
        userService.importVolunteer(volunteer2);
        createdCount++;

        // 3. 志愿者3：无手机号，只能通过志愿者ID登录
        VolunteerImportVO volunteer3 = new VolunteerImportVO();
        volunteer3.setVolunteerId("V20260003");
        volunteer3.setNickName("王志愿");
        volunteer3.setCommunityId(communityId);
        volunteer3.setCommunityName(communityName);
        userService.importVolunteer(volunteer3);
        createdCount++;

        log.info("志愿者测试数据生成完成，创建条目数={}", createdCount);
        return Result.success("志愿者测试数据生成成功，共创建 " + createdCount + " 个志愿者（V20260001~V20260003），前端登录后调用auth接口传volunteerId认证身份");
    }

    /**
     * [新增 2026-08-04 10:00] 生成社区特惠模块测试数据
     * 包含：5个一级分类+二级分类、7个特惠店铺（含分类/封面图/坐标/评分/起步价）、优惠券、评价
     * 前端联调路径：GET /api/special/categories → POST /api/special/shop/list → GET /api/special/shop/detail/{shopId}
     */
    @PostMapping("/initSpecialData")
    @Operation(summary = "生成社区特惠测试数据（分类+店铺+优惠券+评价）",
            responses = {@ApiResponse(description = "成功信息", content = @Content(schema = @Schema(implementation = String.class)))})
    @NeedLogin
    public Result<String> initSpecialData() {
        checkEnv();
        int createdCount = 0;

        // 查询社区信息用于数据隔离
        List<com.demo.weixin.entity.community.Community> communities = communityService.getActiveCommunityList();
        Long communityId = !communities.isEmpty() ? communities.get(0).getCommunityId() : null;
        String communityName = !communities.isEmpty() ? communities.get(0).getName() : "中山一社区";

        // ===== 1. 创建分类 =====
        // 检查是否已有分类数据
        List<com.demo.weixin.entity.special.SpecialCategory> existCategories = specialCategoryService.getList(communityId);
        if (!existCategories.isEmpty()) {
            return Result.success("社区特惠分类数据已存在，跳过创建");
        }

        // 一级分类数据（匹配前端mock）
        String[][] level1Data = {
                {"特惠好物", "https://images.unsplash.com/photo-1544816155-12df9643f363?auto=format&fit=crop&w=150&q=80"},
                {"教培托育", "https://images.unsplash.com/photo-1503676260728-1c00da094a0b?auto=format&fit=crop&w=150&q=80"},
                {"生活服务", "https://images.unsplash.com/photo-1581578731548-c64695cc6952?auto=format&fit=crop&w=150&q=80"},
                {"餐饮住宿", "https://images.unsplash.com/photo-1555396273-367ea4eb4db5?auto=format&fit=crop&w=150&q=80"},
                {"休闲娱乐", "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?auto=format&fit=crop&w=150&q=80"}
        };
        // 二级分类数据（匹配前端mock）
        String[][] level2Data = {
                {"数码家电", "居家日用", "服饰鞋帽", "粮油生鲜", "美妆个护"},
                {"早教托育", "少儿英语", "艺术兴趣", "学科辅导", "成人培训"},
                {"家政保洁", "洗衣洗鞋", "家电维修", "美容美甲", "美发养发", "汽车服务", "宠物服务"},
                {"美食快餐", "火锅烧烤", "甜品饮品", "特色民宿", "酒店住宿"},
                {"洗浴汗蒸", "足疗按摩", "KTV唱吧", "密室剧本杀", "运动健身"}
        };

        Long[] cat1Ids = new Long[5];
        for (int i = 0; i < 5; i++) {
            com.demo.weixin.entity.special.SpecialCategory cat1 = new com.demo.weixin.entity.special.SpecialCategory();
            cat1.setParentId(0L);
            cat1.setName(level1Data[i][0]);
            cat1.setIcon(level1Data[i][1]);
            cat1.setSort(i + 1);
            cat1.setStatus(com.demo.weixin.enums.special.SpecialCategoryStatusEnum.ENABLED.getCode());
            cat1.setCommunityId(0L); // 全社区通用
            cat1 = specialCategoryService.saveOrUpdate(cat1);
            cat1Ids[i] = cat1.getCategoryId();
            createdCount++;

            // 创建二级分类
            for (int j = 0; j < level2Data[i].length; j++) {
                com.demo.weixin.entity.special.SpecialCategory cat2 = new com.demo.weixin.entity.special.SpecialCategory();
                cat2.setParentId(cat1Ids[i]);
                cat2.setName(level2Data[i][j]);
                cat2.setSort(j + 1);
                cat2.setStatus(com.demo.weixin.enums.special.SpecialCategoryStatusEnum.ENABLED.getCode());
                cat2.setCommunityId(0L);
                specialCategoryService.saveOrUpdate(cat2);
                createdCount++;
            }
        }

        // ===== 2. 创建特惠店铺 =====
        // 检查是否已有特惠店铺（cat1Id不为空的店铺）
        Long existSpecialShops = storeShopDao.count(
                org.springframework.data.mongodb.core.query.Criteria.where("cat1Id").ne(null));
        if (existSpecialShops > 0) {
            return Result.success("社区特惠数据已存在，跳过创建");
        }

        // 店铺数据（匹配前端mock结构）
        Object[][] shopData = {
                // {cat1Index, cat2Index, name, price, sales, rating, image, address, lat, lng, phone}
                {0, 0, "社区特惠智能破壁机/养生壶专卖店", 199, 310, 4.9, "https://images.unsplash.com/photo-1544816155-12df9643f363?auto=format&fit=crop&w=400&q=80", "广州市越秀区中山一路88号", 23.12908, 113.26436, "13800138001"},
                {4, 4, "社区青年健身房与瑜伽生活馆", 88, 150, 4.6, "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?auto=format&fit=crop&w=400&q=80", "广州市越秀区东风东路100号", 23.13108, 113.26636, "13800138002"},
                {1, 0, "阳光少儿乐园与全日制托育中心", 299, 60, 5.0, "https://images.unsplash.com/photo-1503676260728-1c00da094a0b?auto=format&fit=crop&w=400&q=80", "广州市越秀区环市东路200号", 23.13508, 113.26836, "13800138003"},
                {3, 0, "老广记地道烧腊饭与爱心惠民食堂", 22, 880, 4.8, "https://images.unsplash.com/photo-1555396273-367ea4eb4db5?auto=format&fit=crop&w=400&q=80", "广州市越秀区北京路300号", 23.12708, 113.26236, "13800138004"},
                {2, 0, "厨卫下水道/马桶深度清洁惠民小铺", 98, 240, 4.9, "https://images.unsplash.com/photo-1584622650111-993a426fbf0a?auto=format&fit=crop&w=400&q=80", "广州市越秀区青菜岗43号启东楼", 23.12908, 113.26436, "13812345678"},
                {2, 0, "阿姨来了·全屋深度保洁与玻璃擦洗", 158, 180, 4.7, "https://images.unsplash.com/photo-1581578731548-c64695cc6952?auto=format&fit=crop&w=400&q=80", "广州市越秀区先烈中路50号", 23.13008, 113.26536, "13800138005"},
                {2, 2, "老张家电清洗拆修（空调/油烟机/洗衣机）", 120, 95, 4.8, "https://images.unsplash.com/photo-1581092160607-ee22621dd758?auto=format&fit=crop&w=400&q=80", "广州市越秀区越华路60号", 23.12808, 113.26336, "13800138006"}
        };

        Long[] shopIds = new Long[shopData.length];
        for (int i = 0; i < shopData.length; i++) {
            Object[] data = shopData[i];
            int cat1Idx = (Integer) data[0];
            int cat2Idx = (Integer) data[1];

            // 查找对应的二级分类ID
            List<com.demo.weixin.entity.special.SpecialCategory> subCats = specialCategoryService.getList(null);
            Long cat1Id = cat1Ids[cat1Idx];
            Long cat2Id = null;
            for (com.demo.weixin.entity.special.SpecialCategory sub : subCats) {
                if (sub.getParentId() != null && sub.getParentId().equals(cat1Id) && sub.getSort() != null && sub.getSort() == cat2Idx + 1) {
                    cat2Id = sub.getCategoryId();
                    break;
                }
            }

            StoreShop shop = new StoreShop();
            shop.setSellerUserId(getCurrentUserId());
            shop.setName((String) data[2]);
            shop.setLogo((String) data[6]);
            shop.setCoverImage((String) data[6]);
            shop.setPhone((String) data[10]);
            shop.setAddress((String) data[7]);
            shop.setDescription("社区特惠合作店铺");
            shop.setStatus(1); // 营业中
            shop.setMonthlySales((Integer) data[4]);
            shop.setRating((Double) data[5]);
            shop.setStartPrice(new BigDecimal(data[3].toString()));
            shop.setLatitude((Double) data[8]);
            shop.setLongitude((Double) data[9]);
            shop.setCat1Id(cat1Id);
            shop.setCat2Id(cat2Id);
            shop.setCommunityId(communityId);
            shop.setCommunityName(communityName);
            shop.setGoodsCount(0);
            shop.setFollowCount(0);
            shop.setFansCount(0);
            shop = storeShopDao.insertDocument(shop);
            shopIds[i] = shop.getShopId();
            createdCount++;
        }

        // ===== 3. 创建优惠券 =====
        // 为第一个店铺创建2张优惠券（匹配前端mock）
        com.demo.weixin.vo.special.ShopCouponCreateVO couponVO1 = new com.demo.weixin.vo.special.ShopCouponCreateVO();
        couponVO1.setShopId(shopIds[4]); // 厨卫清洁店铺
        couponVO1.setTitle("满100元可用");
        couponVO1.setMoney(new BigDecimal("10"));
        couponVO1.setMinSpend(new BigDecimal("100"));
        couponVO1.setTotal(100);
        couponVO1.setStartTime(new Date());
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.add(java.util.Calendar.DAY_OF_MONTH, 30);
        couponVO1.setEndTime(cal.getTime());
        shopCouponService.saveOrUpdate(couponVO1);
        createdCount++;

        com.demo.weixin.vo.special.ShopCouponCreateVO couponVO2 = new com.demo.weixin.vo.special.ShopCouponCreateVO();
        couponVO2.setShopId(shopIds[4]);
        couponVO2.setTitle("社区新人礼");
        couponVO2.setMoney(new BigDecimal("5"));
        couponVO2.setMinSpend(BigDecimal.ZERO);
        couponVO2.setTotal(50);
        couponVO2.setStartTime(new Date());
        couponVO2.setEndTime(cal.getTime());
        shopCouponService.saveOrUpdate(couponVO2);
        createdCount++;

        // ===== 4. 创建评价 =====
        // 为第一个店铺创建2条评价（匹配前端mock）
        com.demo.weixin.vo.special.ShopReviewCreateVO reviewVO1 = new com.demo.weixin.vo.special.ShopReviewCreateVO();
        reviewVO1.setShopId(shopIds[4]);
        reviewVO1.setRating(5);
        reviewVO1.setContent("师傅上门速度非常快，管道清洗得很干净，还帮忙把周边的卫生擦了，服务态度太棒了。");
        shopReviewService.createReview(getCurrentUserId(), "石头", "https://cdn.uviewui.com/uview/album/1.jpg", reviewVO1);
        createdCount++;

        com.demo.weixin.vo.special.ShopReviewCreateVO reviewVO2 = new com.demo.weixin.vo.special.ShopReviewCreateVO();
        reviewVO2.setShopId(shopIds[4]);
        reviewVO2.setRating(5);
        reviewVO2.setContent("价格真的很实惠，比外面随便找的便宜一大截，属于咱社区自己的实惠！");
        shopReviewService.createReview(getCurrentUserId(), "秉治", "https://cdn.uviewui.com/uview/album/2.jpg", reviewVO2);
        createdCount++;

        log.info("社区特惠测试数据生成完成，创建条目数={}", createdCount);
        return Result.success("社区特惠数据生成成功，共创建 " + createdCount + " 条数据（5个一级分类+25个二级分类+7个店铺+2张优惠券+2条评价）");
    }

    /**
     * 一键生成全部测试数据
     */
    @PostMapping("/initAll")
    @Operation(summary = "一键生成全部测试数据（社区+志愿者+商城+活动+个人中心+认证+服务对象+模板+广告+消费帮扶+通知+社区特惠）",
            responses = {@ApiResponse(description = "成功信息", content = @Content(schema = @Schema(implementation = String.class)))})
    @NeedLogin
    public Result<String> initAll() {
        checkEnv();
        StringBuilder sb = new StringBuilder();
        // [新增 2026-08-03 17:50] 先生成社区数据，其他模块的测试数据可关联社区
        try { sb.append(initCommunityData().getData()).append("；"); } catch (Exception e) { sb.append("社区数据已存在或失败；"); }
        // [新增 2026-08-03 21:00] 志愿者数据在社区之后、商城之前生成
        try { sb.append(initVolunteerData().getData()).append("；"); } catch (Exception e) { sb.append("志愿者数据已存在或失败；"); }
        try { sb.append(initData().getData()).append("；"); } catch (Exception e) { sb.append("商城数据已存在或失败；"); }
        try { sb.append(initActivityData().getData()).append("；"); } catch (Exception e) { sb.append("活动数据已存在或失败；"); }
        try { sb.append(initMineData().getData()).append("；"); } catch (Exception e) { sb.append("个人中心数据已存在或失败；"); }
        try { sb.append(initCertificationData().getData()).append("；"); } catch (Exception e) { sb.append("认证数据已存在或失败；"); }
        SimulateAuditVO auditVO = new SimulateAuditVO();
        auditVO.setApproved(true);
        try { sb.append(simulateAuditCertification(auditVO).getData()).append("；"); } catch (Exception e) { sb.append("认证审核模拟失败；"); }
        try { sb.append(initServiceMemberData().getData()).append("；"); } catch (Exception e) { sb.append("服务对象数据已存在或失败；"); }
        try { sb.append(initTemplateData().getData()).append("；"); } catch (Exception e) { sb.append("活动模板数据已存在或失败；"); }
        try { sb.append(initAdData().getData()).append("；"); } catch (Exception e) { sb.append("广告数据已存在或失败；"); }
        try { sb.append(initAssistanceData().getData()).append("；"); } catch (Exception e) { sb.append("消费帮扶数据已存在或失败；"); }
        // [新增 2026-08-03 19:30] 通知数据放在最后生成
        try { sb.append(initNoticeData().getData()).append("；"); } catch (Exception e) { sb.append("通知数据已存在或失败；"); }
        // [新增 2026-08-04 10:00] 社区特惠数据放在通知之后生成
        try { sb.append(initSpecialData().getData()).append("；"); } catch (Exception e) { sb.append("社区特惠数据已存在或失败；"); }
        log.info("一键生成全部测试数据完成，userId={}", getCurrentUserId());
        return Result.success(sb.toString());
    }
}
