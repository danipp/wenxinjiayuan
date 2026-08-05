package com.demo.weixin.service.assistance;

import cn.hutool.core.util.StrUtil;
import com.demo.common.exception.BizException;
import com.demo.weixin.dao.assistance.AssistanceStatDao;
import com.demo.weixin.dao.assistance.CharityEnterpriseDao;
import com.demo.weixin.dao.assistance.DonationApplyDao;
import com.demo.weixin.dao.store.StoreGoodsDao;
import com.demo.weixin.dao.store.StoreShopDao;
import com.demo.weixin.entity.assistance.AssistanceStat;
import com.demo.weixin.enums.assistance.EnterpriseStatusEnum;
import com.demo.weixin.enums.store.GoodsSceneEnum;
import com.demo.weixin.vo.assistance.StatConfigVO;
import com.demo.weixin.vo.assistance.StatQueryVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 消费帮扶统计服务
 * 提供统计列表查询（实时聚合/管理员配置/全部）、统计配置增删改、默认统计项初始化。
 * 实时聚合模式从各业务表实时计算统计数据，自定义模式读取管理员配置的固定值。
 */
@Service
@Slf4j
public class AssistanceStatService {

    /** 实时聚合模式 */
    private static final String MODE_REAL = "real";
    /** 自定义配置模式 */
    private static final String MODE_CUSTOM = "custom";
    /** 全部模式（自定义优先，补充实时数据） */
    private static final String MODE_ALL = "all";

    @Autowired
    private AssistanceStatDao assistanceStatDao;
    @Autowired
    private StoreShopDao storeShopDao;
    @Autowired
    private StoreGoodsDao storeGoodsDao;
    @Autowired
    private CharityEnterpriseDao charityEnterpriseDao;
    @Autowired
    private DonationApplyDao donationApplyDao;

    /**
     * 获取统计列表
     * - mode=real：实时聚合商家数、商品数、企业数、物资总数
     * - mode=custom：查询管理员配置的自定义统计项（isCustom=true）
     * - mode=all：先查自定义记录，再补充实时数据（同名key优先custom）
     *
     * @param vo 查询条件（mode指定查询模式）
     * @return 统计项列表
     */
    public List<AssistanceStat> getStatList(StatQueryVO vo) {
        String mode = StrUtil.isNotBlank(vo.getMode()) ? vo.getMode() : MODE_ALL;
        // [新增 2026-08-03 17:30] 社区数据隔离
        Long communityId = vo.getCommunityId();
        switch (mode) {
            case MODE_REAL:
                return buildRealTimeStats(communityId);
            case MODE_CUSTOM:
                // [新增 2026-08-03 17:30] 自定义配置也按社区过滤
                Criteria customCriteria = Criteria.where("isCustom").is(true);
                if (communityId != null) {
                    customCriteria.and("communityId").is(communityId);
                }
                return assistanceStatDao.findDocumentList(customCriteria);
            case MODE_ALL:
            default:
                return buildAllStats(communityId);
        }
    }

    /**
     * 保存统计配置
     * statId为空时新增，有值时编辑。均设置isCustom=true。
     *
     * @param vo 统计配置入参
     * @return 保存后的统计配置记录
     */
    public AssistanceStat saveStatConfig(StatConfigVO vo) {
        if (vo.getStatId() == null) {
            // 新增统计配置
            AssistanceStat stat = new AssistanceStat();
            stat.setStatKey(vo.getStatKey());
            stat.setStatLabel(vo.getStatLabel());
            stat.setStatValue(vo.getStatValue() != null ? vo.getStatValue() : 0L);
            stat.setIsCustom(true);
            stat.setDisplayOrder(vo.getDisplayOrder());
            assistanceStatDao.insertDocument(stat);
            log.info("新增统计配置，statId={}，statKey={}", stat.getStatId(), vo.getStatKey());
            return stat;
        }
        // 编辑统计配置
        AssistanceStat existing = assistanceStatDao.findById(vo.getStatId());
        if (existing == null) {
            throw new BizException("统计配置不存在");
        }
        assistanceStatDao.updateOneDocument(
                Criteria.where("statId").is(vo.getStatId()),
                new Update()
                        .set("statKey", vo.getStatKey())
                        .set("statLabel", vo.getStatLabel())
                        .set("statValue", vo.getStatValue() != null ? vo.getStatValue() : 0L)
                        .set("isCustom", true)
                        .set("displayOrder", vo.getDisplayOrder()));
        log.info("编辑统计配置，statId={}，statKey={}", vo.getStatId(), vo.getStatKey());
        return assistanceStatDao.findById(vo.getStatId());
    }

    /**
     * 删除统计配置
     *
     * @param statId 统计项ID
     */
    public void deleteStatConfig(Long statId) {
        AssistanceStat existing = assistanceStatDao.findById(statId);
        if (existing == null) {
            throw new BizException("统计配置不存在");
        }
        assistanceStatDao.deleteDocument(statId);
        log.info("删除统计配置，statId={}", statId);
    }

    /**
     * 初始化默认统计项
     * 如果AssistanceStat表为空，插入4条默认记录：
     * merchantCount(爱心联盟商家)、shopCount(社区慈善超市)、
     * enterpriseCount(爱心帮扶企业)、goodsCount(爱心物资总数)，
     * 均设isCustom=true，statValue=0。
     */
    public void initDefaultStats() {
        // 表为空时才初始化，避免重复插入
        long count = assistanceStatDao.count(new Criteria());
        if (count > 0) {
            log.info("统计配置表已有{}条记录，跳过默认初始化", count);
            return;
        }
        // 默认4条统计项
        String[][] defaults = {
                {"merchantCount", "爱心联盟商家"},
                {"shopCount", "社区慈善超市"},
                {"enterpriseCount", "爱心帮扶企业"},
                {"goodsCount", "爱心物资总数"}
        };
        for (int i = 0; i < defaults.length; i++) {
            AssistanceStat stat = new AssistanceStat();
            stat.setStatKey(defaults[i][0]);
            stat.setStatLabel(defaults[i][1]);
            stat.setStatValue(0L);
            stat.setIsCustom(true);
            stat.setDisplayOrder(i + 1);
            assistanceStatDao.insertDocument(stat);
        }
        log.info("初始化默认统计项完成，共{}条", defaults.length);
    }

    // ==================== 内部方法 ====================

    /**
     * 构建实时聚合统计列表
     * 商家数=StoreShop总数，商品数=StoreGoods(scene=assistance)总数，
     * 企业数=CharityEnterprise(status=active)总数，物资总数=StoreGoods(scene=assistance)库存求和。
     *
     * @return 实时统计项列表
     */
    // [变更 2026-08-03 17:30] 增加communityId参数用于社区数据隔离
    private List<AssistanceStat> buildRealTimeStats(Long communityId) {
        List<AssistanceStat> list = new ArrayList<>();

        // [新增 2026-08-03 17:30] 社区数据隔离条件
        Criteria communityCriteria = new Criteria();
        if (communityId != null) {
            communityCriteria.and("communityId").is(communityId);
        }

        // 商家数 = StoreShop 总数（按社区过滤）
        long merchantCount = storeShopDao.count(communityCriteria);
        list.add(buildStat("merchantCount", "爱心联盟商家", merchantCount, 1));

        // 商品数 = StoreGoods(scene=assistance) 总数（按社区过滤）
        Criteria assistanceScene = Criteria.where("scene").is(GoodsSceneEnum.ASSISTANCE.getCode());
        if (communityId != null) {
            assistanceScene.and("communityId").is(communityId);
        }
        long shopCount = storeGoodsDao.count(assistanceScene);
        list.add(buildStat("shopCount", "社区慈善超市", shopCount, 2));

        // 企业数 = CharityEnterprise(status=active) 总数（按社区过滤）
        Criteria enterpriseCriteria = Criteria.where("status").is(EnterpriseStatusEnum.ACTIVE.getCode());
        if (communityId != null) {
            enterpriseCriteria.and("communityId").is(communityId);
        }
        long enterpriseCount = charityEnterpriseDao.count(enterpriseCriteria);
        list.add(buildStat("enterpriseCount", "爱心帮扶企业", enterpriseCount, 3));

        // 物资总数 = StoreGoods(scene=assistance) stock 求和（按社区过滤）
        Object stockSum = storeGoodsDao.sumByField(assistanceScene, "stock");
        list.add(buildStat("goodsCount", "爱心物资总数", toLong(stockSum), 4));

        return list;
    }

    /**
     * 构建全部统计列表
     * 先查自定义记录，再补充实时数据，同名key优先custom。
     *
     * @return 合并后的统计项列表
     */
    // [变更 2026-08-03 17:30] 增加communityId参数用于社区数据隔离
    private List<AssistanceStat> buildAllStats(Long communityId) {
        // 先查自定义配置记录（按社区过滤）
        Criteria customCriteria = Criteria.where("isCustom").is(true);
        if (communityId != null) {
            customCriteria.and("communityId").is(communityId);
        }
        List<AssistanceStat> customList = assistanceStatDao.findDocumentList(customCriteria);
        // 收集自定义记录已有的key，实时数据中同名的跳过
        Set<String> customKeys = new HashSet<>();
        for (AssistanceStat stat : customList) {
            if (StrUtil.isNotBlank(stat.getStatKey())) {
                customKeys.add(stat.getStatKey());
            }
        }
        // 补充实时数据（同名key优先custom，不覆盖）
        List<AssistanceStat> realList = buildRealTimeStats(communityId);
        for (AssistanceStat realStat : realList) {
            if (!customKeys.contains(realStat.getStatKey())) {
                customList.add(realStat);
            }
        }
        return customList;
    }

    /**
     * 构建单个统计项（非持久化，仅用于返回）
     *
     * @param key          统计项标识
     * @param label        统计项标签
     * @param value        统计值
     * @param displayOrder 展示顺序
     * @return 统计项对象
     */
    private AssistanceStat buildStat(String key, String label, long value, int displayOrder) {
        AssistanceStat stat = new AssistanceStat();
        stat.setStatKey(key);
        stat.setStatLabel(label);
        stat.setStatValue(value);
        stat.setIsCustom(false);
        stat.setDisplayOrder(displayOrder);
        return stat;
    }

    /**
     * 将聚合求和结果安全转换为Long
     * MongoDB聚合$sum可能返回Integer、Long或Double，统一转为Long。
     *
     * @param value 聚合结果
     * @return Long值，null返回0
     */
    private Long toLong(Object value) {
        if (value == null) {
            return 0L;
        }
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        return 0L;
    }
}
