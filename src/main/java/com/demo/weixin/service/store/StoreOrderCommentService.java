package com.demo.weixin.service.store;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.demo.common.exception.BizException;
import com.demo.weixin.dao.UserDao;
import com.demo.weixin.dao.store.StoreOrderCommentDao;
import com.demo.weixin.dao.store.StoreOrderDao;
import com.demo.weixin.entity.User;
import com.demo.weixin.entity.store.StoreOrder;
import com.demo.weixin.entity.store.StoreOrderComment;
import com.demo.weixin.enums.store.OrderStatusEnum;
import com.demo.weixin.vo.store.CommentCreateVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 商城订单评价服务
 * 买家在订单核销完成后对商品进行评价，一个订单只能评价一次。
 */
@Service
@Slf4j
public class StoreOrderCommentService {

    @Autowired
    private StoreOrderCommentDao storeOrderCommentDao;
    @Autowired
    private StoreOrderDao storeOrderDao;
    @Autowired
    private UserDao userDao;

    /**
     * 创建订单评价
     * 校验订单状态为已完成且未评价，创建评价并回写订单 commentId。
     *
     * @param userId 评价用户ID（必须是订单买家）
     * @param vo     评价入参
     */
    public StoreOrderComment createComment(Long userId, CommentCreateVO vo) {
        // 1. 校验订单
        StoreOrder order = storeOrderDao.findById(vo.getOrderId());
        if (order == null) {
            throw new BizException("订单不存在");
        }
        if (!order.getBuyerUserId().equals(userId)) {
            throw new BizException("无权评价他人订单");
        }
        // M8: 使用Integer.valueOf避免拆箱NPE
        if (!Integer.valueOf(OrderStatusEnum.COMPLETED.getCode()).equals(order.getStatus())) {
            throw new BizException("订单未完成，无法评价");
        }
        if (order.getCommentId() != null) {
            throw new BizException("订单已评价，不能重复评价");
        }
        // 2. 校验评价内容
        if (vo.getRating() == null || vo.getRating() < 1 || vo.getRating() > 5) {
            throw new BizException("评分必须在1-5之间");
        }
        if (StrUtil.isBlank(vo.getContent())) {
            throw new BizException("评价内容不能为空");
        }
        // 3. 获取用户信息（冗余到评价记录）
        User user = userDao.findById(userId);
        // 4. 创建评价
        StoreOrderComment comment = new StoreOrderComment();
        comment.setOrderId(vo.getOrderId());
        comment.setGoodsId(order.getGoodsId());
        comment.setUserId(userId);
        comment.setUserName(user != null ? user.getNickName() : "匿名用户");
        comment.setUserAvatar(user != null ? user.getAvatar() : null);
        comment.setRating(vo.getRating());
        comment.setContent(vo.getContent());
        comment.setImages(vo.getImages());
        storeOrderCommentDao.insertDocument(comment);
        // 5. 回写订单评价ID
        storeOrderDao.updateOneDocument(
                Criteria.where("orderId").is(vo.getOrderId()),
                new Update().set("commentId", comment.getCommentId()));
        log.info("创建订单评价，commentId={}，orderId={}，rating={}", comment.getCommentId(), vo.getOrderId(), vo.getRating());
        return comment;
    }

    /**
     * 查询商品评价列表（分页）
     */
    public Page<StoreOrderComment> queryGoodsComments(Long goodsId, int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Order.desc("createTime")));
        Criteria criteria = Criteria.where("goodsId").is(goodsId);
        return storeOrderCommentDao.findDocumentPage(criteria, pageable);
    }

    /**
     * 查询订单评价（单条）
     */
    public StoreOrderComment getByOrderId(Long orderId) {
        return storeOrderCommentDao.findOne(Criteria.where("orderId").is(orderId));
    }
}
