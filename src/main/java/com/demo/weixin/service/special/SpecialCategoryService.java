package com.demo.weixin.service.special;

import com.demo.weixin.dao.special.SpecialCategoryDao;
import com.demo.weixin.entity.special.SpecialCategory;
import com.demo.weixin.enums.special.SpecialCategoryStatusEnum;
import com.demo.weixin.vo.special.SpecialCategoryVO;
import com.demo.common.exception.BizException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 社区特惠分类服务
 * 管理两级分类树：一级分类（parentId=0）和二级分类（parentId=一级分类ID）
 */
@Service
@Slf4j
public class SpecialCategoryService {

    @Autowired
    private SpecialCategoryDao specialCategoryDao;

    /**
     * 获取分类树（C端）
     * 只返回启用状态的分类，按sort升序排列
     *
     * @param communityId 社区ID（可选，为null时返回全社区通用的分类）
     * @return 分类树结构
     */
    public List<SpecialCategoryVO> getCategoryTree(Long communityId) {
        // 查询所有启用状态的分类
        Criteria criteria = new Criteria();
        criteria.and("status").is(SpecialCategoryStatusEnum.ENABLED.getCode());
        if (communityId != null) {
            // 同时匹配指定社区和全社区通用（communityId=0）的分类
            criteria.and("communityId").in(communityId, 0L);
        }
        List<SpecialCategory> allCategories = specialCategoryDao.findDocumentList(criteria,
                Sort.Order.asc("sort"), Sort.Order.asc("createTime"));

        // 构建树结构
        return buildTree(allCategories);
    }

    /**
     * 构建分类树
     */
    private List<SpecialCategoryVO> buildTree(List<SpecialCategory> allCategories) {
        // 先找出一级分类
        List<SpecialCategoryVO> tree = new ArrayList<>();
        for (SpecialCategory cat : allCategories) {
            if (cat.getParentId() != null && cat.getParentId() == 0L) {
                SpecialCategoryVO vo = convertToVO(cat);
                // 查找子分类
                List<SpecialCategoryVO> children = allCategories.stream()
                        .filter(c -> cat.getCategoryId().equals(c.getParentId()))
                        .map(this::convertToVO)
                        .collect(Collectors.toList());
                vo.setChildren(children);
                tree.add(vo);
            }
        }
        return tree;
    }

    /**
     * 实体转VO
     */
    private SpecialCategoryVO convertToVO(SpecialCategory cat) {
        SpecialCategoryVO vo = new SpecialCategoryVO();
        vo.setCategoryId(cat.getCategoryId());
        vo.setParentId(cat.getParentId());
        vo.setName(cat.getName());
        vo.setIcon(cat.getIcon());
        vo.setSort(cat.getSort());
        return vo;
    }

    /**
     * 管理端：创建或编辑分类
     *
     * @param category 分类信息（categoryId为空时创建，非空时编辑）
     * @return 保存后的分类
     */
    public SpecialCategory saveOrUpdate(SpecialCategory category) {
        if (category.getName() == null || category.getName().trim().isEmpty()) {
            throw new BizException("分类名称不能为空");
        }
        if (category.getParentId() == null) {
            category.setParentId(0L);
        }
        if (category.getStatus() == null) {
            category.setStatus(SpecialCategoryStatusEnum.ENABLED.getCode());
        }
        if (category.getSort() == null) {
            category.setSort(0);
        }
        // 编辑时校验存在性
        if (category.getCategoryId() != null) {
            SpecialCategory exist = specialCategoryDao.findById(category.getCategoryId());
            if (exist == null) {
                throw new BizException("分类不存在");
            }
        }
        // 如果是二级分类，校验父分类存在性
        if (category.getParentId() != null && category.getParentId() > 0) {
            SpecialCategory parent = specialCategoryDao.findById(category.getParentId());
            if (parent == null) {
                throw new BizException("父分类不存在");
            }
        }
        return specialCategoryDao.saveOrUpdate(category);
    }

    /**
     * 管理端：查询分类列表（扁平结构，管理端用）
     *
     * @param communityId 社区ID（可选）
     * @return 分类列表
     */
    public List<SpecialCategory> getList(Long communityId) {
        Criteria criteria = new Criteria();
        if (communityId != null) {
            criteria.and("communityId").in(communityId, 0L);
        }
        return specialCategoryDao.findDocumentList(criteria,
                Sort.Order.asc("parentId"), Sort.Order.asc("sort"));
    }

    /**
     * 管理端：删除分类
     * 如果是一级分类，需检查是否有子分类
     *
     * @param categoryId 分类ID
     */
    public void delete(Long categoryId) {
        SpecialCategory category = specialCategoryDao.findById(categoryId);
        if (category == null) {
            throw new BizException("分类不存在");
        }
        // 如果是一级分类，检查是否有子分类
        if (category.getParentId() != null && category.getParentId() == 0L) {
            List<SpecialCategory> children = specialCategoryDao.findDocumentList(
                    Criteria.where("parentId").is(categoryId));
            if (!children.isEmpty()) {
                throw new BizException("该分类下有子分类，无法删除");
            }
        }
        specialCategoryDao.deleteDocument(categoryId);
        log.info("删除社区特惠分类，categoryId={}，name={}", categoryId, category.getName());
    }
}
