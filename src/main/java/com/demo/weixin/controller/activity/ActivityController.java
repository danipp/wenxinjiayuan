package com.demo.weixin.controller.activity;

import com.demo.common.core.result.Result;
import com.demo.weixin.annotation.DistributedIdempotent;
import com.demo.weixin.annotation.NeedLogin;
import com.demo.weixin.constant.Constants;
import com.demo.weixin.controller.BaseController;
import com.demo.weixin.entity.activity.Activity;
import com.demo.weixin.entity.activity.ActivityComment;
import com.demo.weixin.entity.activity.ActivityPhoto;
import com.demo.weixin.entity.activity.ActivitySignup;
import com.demo.weixin.service.activity.ActivityCommentService;
import com.demo.weixin.service.activity.ActivityPhotoService;
import com.demo.weixin.service.activity.ActivityService;
import com.demo.weixin.vo.activity.ActivityCommentVO;
import com.demo.weixin.vo.activity.ActivityCreateVO;
import com.demo.weixin.vo.activity.ActivityPhotoUploadVO;
import com.demo.weixin.vo.activity.ActivityQueryVO;
import com.demo.weixin.vo.activity.MyActivityQueryVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 活动控制器
 * <p>
 * 活动模块全功能接口：创建、详情、广场列表、报名、评价、照片、点赞。
 * 创建活动、报名、写评价、照片点赞使用 @DistributedIdempotent 防止并发重复操作。
 * </p>
 */
@RestController
@Tag(name = "活动")
@RequestMapping("/api/activity")
@Slf4j
public class ActivityController extends BaseController {

    @Autowired
    private ActivityService activityService;
    @Autowired
    private ActivityCommentService activityCommentService;
    @Autowired
    private ActivityPhotoService activityPhotoService;

    /**
     * 创建活动
     */
    @PostMapping("/create")
    @Operation(summary = "创建活动",
            responses = {@ApiResponse(description = "成功信息", content = @Content(schema = @Schema(implementation = Activity.class)))})
    @NeedLogin
    @DistributedIdempotent(prefix = Constants.LOCK_ACTIVITY_CREATE,
            message = "创建活动请求正在处理中，请不要高频连击")
    public Result<Activity> create(@Valid @RequestBody ActivityCreateVO vo) {
        Activity activity = activityService.createActivity(getCurrentUserId(), vo);
        return Result.success(activity);
    }

    /**
     * 活动详情（含当前用户报名状态）
     */
    @GetMapping("/detail/{activityId}")
    @Operation(summary = "活动详情",
            responses = {@ApiResponse(description = "成功信息", content = @Content(schema = @Schema(implementation = Activity.class)))})
    @NeedLogin
    public Result<Activity> detail(@PathVariable Long activityId) {
        Activity activity = activityService.getActivityDetail(activityId, getCurrentUserId());
        return Result.success(activity);
    }

    /**
     * 活动广场列表（分页，带筛选）
     */
    @PostMapping("/square")
    @Operation(summary = "活动广场列表（分页）",
            responses = {@ApiResponse(description = "成功信息", content = @Content(schema = @Schema(implementation = Activity.class)))})
    public Result<Page<Activity>> square(@Valid @RequestBody ActivityQueryVO queryVO) {
        queryVO.setPageNumber(queryVO.getPageNumber() - 1);
        if (queryVO.getPageNumber() < 0) {
            queryVO.setPageNumber(0);
        }
        Page<Activity> page = activityService.getSquareList(queryVO);
        return Result.success(page, page.getTotalElements());
    }

    /**
     * 报名活动
     */
    @PostMapping("/signup/{activityId}")
    @Operation(summary = "报名活动",
            responses = {@ApiResponse(description = "成功信息", content = @Content(schema = @Schema(implementation = ActivitySignup.class)))})
    @NeedLogin
    @DistributedIdempotent(prefix = Constants.LOCK_ACTIVITY_SIGNUP,
            key = "#activityId",
            message = "报名请求正在处理中，请不要高频连击")
    public Result<ActivitySignup> signup(@PathVariable Long activityId) {
        ActivitySignup signup = activityService.signup(activityId, getCurrentUserId());
        return Result.success(signup);
    }

    /**
     * 获取已加入的邻居列表
     */
    @GetMapping("/joined/{activityId}")
    @Operation(summary = "已加入的邻居列表",
            responses = {@ApiResponse(description = "成功信息", content = @Content(schema = @Schema(implementation = ActivitySignup.class)))})
    @NeedLogin
    public Result<List<ActivitySignup>> joined(@PathVariable Long activityId) {
        List<ActivitySignup> list = activityService.getJoinedNeighbors(activityId);
        return Result.success(list);
    }

    /**
     * 我的活动列表（分页，支持发布/参与筛选）
     */
    @PostMapping("/myActivities")
    @Operation(summary = "我的活动列表（分页）",
            responses = {@ApiResponse(description = "成功信息", content = @Content(schema = @Schema(implementation = Activity.class)))})
    @NeedLogin
    public Result<Page<Activity>> myActivities(@Valid @RequestBody MyActivityQueryVO queryVO) {
        queryVO.setPageNumber(queryVO.getPageNumber() - 1);
        if (queryVO.getPageNumber() < 0) {
            queryVO.setPageNumber(0);
        }
        Page<Activity> page = activityService.getMyActivities(getCurrentUserId(), queryVO);
        return Result.success(page, page.getTotalElements());
    }

    /**
     * 写活动评价
     */
    @PostMapping("/comment")
    @Operation(summary = "写活动评价",
            responses = {@ApiResponse(description = "成功信息", content = @Content(schema = @Schema(implementation = ActivityComment.class)))})
    @NeedLogin
    @DistributedIdempotent(prefix = Constants.LOCK_ACTIVITY_COMMENT,
            message = "评价请求正在处理中，请不要高频连击")
    public Result<ActivityComment> comment(@Valid @RequestBody ActivityCommentVO vo) {
        ActivityComment comment = activityCommentService.createComment(getCurrentUserId(), vo);
        return Result.success(comment);
    }

    /**
     * 活动评价列表（分页）
     */
    @GetMapping("/comments/{activityId}")
    @Operation(summary = "活动评价列表（分页）",
            responses = {@ApiResponse(description = "成功信息", content = @Content(schema = @Schema(implementation = ActivityComment.class)))})
    public Result<Page<ActivityComment>> comments(
            @PathVariable Long activityId,
            @RequestParam(value = "pageNumber", defaultValue = "1") Integer pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "20") Integer pageSize) {
        // 页码从1转为0
        int pageIdx = pageNumber - 1;
        if (pageIdx < 0) {
            pageIdx = 0;
        }
        Pageable pageable = PageRequest.of(pageIdx, pageSize, Sort.by(Sort.Order.desc("createTime")));
        Page<ActivityComment> page = activityCommentService.getCommentPage(activityId, pageable);
        return Result.success(page, page.getTotalElements());
    }

    /**
     * 获取活动平均评分
     */
    @GetMapping("/score/{activityId}")
    @Operation(summary = "获取活动平均评分",
            responses = {@ApiResponse(description = "成功信息", content = @Content(schema = @Schema(implementation = Double.class)))})
    public Result<Double> averageScore(@PathVariable Long activityId) {
        return Result.success(activityCommentService.getAverageScore(activityId));
    }

    /**
     * 上传活动照片
     * 图片已通过OSS上传，本接口仅记录照片信息。
     */
    @PostMapping("/photo/upload")
    @Operation(summary = "上传活动照片",
            responses = {@ApiResponse(description = "成功信息", content = @Content(schema = @Schema(implementation = ActivityPhoto.class)))})
    @NeedLogin
    @DistributedIdempotent(prefix = Constants.LOCK_ACTIVITY_PHOTO_UPLOAD,
            message = "照片上传请求正在处理中，请不要高频连击")
    public Result<ActivityPhoto> uploadPhoto(@Valid @RequestBody ActivityPhotoUploadVO vo) {
        ActivityPhoto photo = activityPhotoService.uploadPhoto(getCurrentUserId(), vo.getActivityId(), vo.getImageUrl());
        return Result.success(photo);
    }

    /**
     * 活动照片列表
     */
    @GetMapping("/photos/{activityId}")
    @Operation(summary = "活动照片列表",
            responses = {@ApiResponse(description = "成功信息", content = @Content(schema = @Schema(implementation = ActivityPhoto.class)))})
    public Result<List<ActivityPhoto>> photos(@PathVariable Long activityId) {
        // 未登录时 currentUserId 为 null，isLiked 全部为 false
        List<ActivityPhoto> list = activityPhotoService.getPhotoList(activityId, getCurrentUserId());
        return Result.success(list);
    }

    /**
     * 照片点赞/取消点赞
     */
    @PostMapping("/photo/like/{photoId}")
    @Operation(summary = "照片点赞/取消点赞",
            responses = {@ApiResponse(description = "成功信息", content = @Content(schema = @Schema(implementation = Boolean.class)))})
    @NeedLogin
    @DistributedIdempotent(prefix = Constants.LOCK_ACTIVITY_PHOTO_LIKE,
            key = "#photoId",
            message = "点赞请求正在处理中，请不要高频连击")
    public Result<Boolean> toggleLike(@PathVariable Long photoId) {
        Boolean liked = activityPhotoService.toggleLike(photoId, getCurrentUserId());
        return Result.success(liked);
    }
}
