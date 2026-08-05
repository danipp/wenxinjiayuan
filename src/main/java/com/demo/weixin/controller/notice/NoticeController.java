package com.demo.weixin.controller.notice;

import com.demo.common.core.result.Result;
import com.demo.weixin.annotation.AdminNeedLogin;
import com.demo.weixin.annotation.DistributedIdempotent;
import com.demo.weixin.annotation.ManageAuditLog;
import com.demo.weixin.annotation.NeedLogin;
import com.demo.weixin.constant.Constants;
import com.demo.weixin.controller.BaseController;
import com.demo.weixin.entity.notice.Notice;
import com.demo.weixin.service.notice.NoticeService;
import com.demo.weixin.vo.notice.NoticeQueryVO;
import com.demo.weixin.vo.notice.NoticeSaveVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 消息通知控制器
 * [新增 2026-08-03 19:30]
 * <p>
 * 管理端接口：新增/编辑/删除/上下架/分页查询
 * C端接口：获取首页轮播通知列表
 * </p>
 */
@RestController
@Tag(name = "消息通知")
@RequestMapping("/api/notice")
@Slf4j
public class NoticeController extends BaseController {

    @Autowired
    private NoticeService noticeService;

    /**
     * 新增通知（管理员）
     * demo: POST /api/notice/create
     */
    @PostMapping("/create")
    @Operation(summary = "新增通知",
            description = "demo: POST /api/notice/create，body传NoticeSaveVO",
            responses = {@ApiResponse(description = "成功返回通知信息", content = @Content(schema = @Schema(implementation = Notice.class)))})
    @AdminNeedLogin
    @ManageAuditLog(module = "消息通知管理", action = "新增通知")
    @DistributedIdempotent(prefix = Constants.LOCK_NOTICE_SAVE, message = "通知保存请求正在处理中，请不要高频连击")
    public Result<Notice> create(@Valid @RequestBody NoticeSaveVO vo) {
        return Result.success(noticeService.createNotice(vo));
    }

    /**
     * 编辑通知（管理员）
     * demo: POST /api/notice/update
     */
    @PostMapping("/update")
    @Operation(summary = "编辑通知",
            description = "demo: POST /api/notice/update，body传NoticeSaveVO（noticeId必传）",
            responses = {@ApiResponse(description = "成功返回通知信息", content = @Content(schema = @Schema(implementation = Notice.class)))})
    @AdminNeedLogin
    @ManageAuditLog(module = "消息通知管理", action = "编辑通知")
    @DistributedIdempotent(prefix = Constants.LOCK_NOTICE_SAVE, message = "通知保存请求正在处理中，请不要高频连击")
    public Result<Notice> update(@Valid @RequestBody NoticeSaveVO vo) {
        return Result.success(noticeService.updateNotice(vo));
    }

    /**
     * 删除通知（管理员，逻辑删除）
     * demo: POST /api/notice/delete/123456
     */
    @PostMapping("/delete/{noticeId}")
    @Operation(summary = "删除通知",
            description = "demo: POST /api/notice/delete/123456",
            responses = {@ApiResponse(description = "成功信息")})
    @AdminNeedLogin
    @ManageAuditLog(module = "消息通知管理", action = "删除通知")
    public Result<Void> delete(@PathVariable Long noticeId) {
        noticeService.deleteNotice(noticeId);
        return Result.success();
    }

    /**
     * 切换通知状态（管理员，上架/下架）
     * demo: POST /api/notice/toggleStatus/123456
     */
    @PostMapping("/toggleStatus/{noticeId}")
    @Operation(summary = "切换通知状态",
            description = "demo: POST /api/notice/toggleStatus/123456",
            responses = {@ApiResponse(description = "成功信息")})
    @AdminNeedLogin
    @ManageAuditLog(module = "消息通知管理", action = "切换通知状态")
    public Result<Void> toggleStatus(@PathVariable Long noticeId) {
        noticeService.toggleStatus(noticeId);
        return Result.success();
    }

    /**
     * 通知详情
     * demo: GET /api/notice/detail/123456
     */
    @GetMapping("/detail/{noticeId}")
    @Operation(summary = "通知详情",
            description = "demo: GET /api/notice/detail/123456",
            responses = {@ApiResponse(description = "成功返回通知信息", content = @Content(schema = @Schema(implementation = Notice.class)))})
    @NeedLogin
    public Result<Notice> detail(@PathVariable Long noticeId) {
        return Result.success(noticeService.getNoticeDetail(noticeId));
    }

    /**
     * 通知分页列表（管理员）
     * demo: POST /api/notice/page
     */
    @PostMapping("/page")
    @Operation(summary = "通知分页列表（管理员）",
            description = "demo: POST /api/notice/page，body传NoticeQueryVO",
            responses = {@ApiResponse(description = "成功返回通知分页", content = @Content(schema = @Schema(implementation = Notice.class)))})
    @AdminNeedLogin
    public Result<Page<Notice>> page(@RequestBody @Valid NoticeQueryVO queryVO) {
        queryVO.setPageNumber(queryVO.getPageNumber() - 1);

        if (queryVO.getPageNumber() < 0) {

            queryVO.setPageNumber(0);

        }
        Page<Notice> page = noticeService.queryNoticePage(queryVO);
        return Result.success(page, page.getTotalElements());
    }

    /**
     * C端-获取首页轮播通知列表
     * [新增 2026-08-03 19:30] 查询当前有效的上架通知，按社区隔离过滤
     * 前端首页消息轮播组件调用此接口，获取文字滚动通知列表。
     * demo: GET /api/notice/active?communityId=123456
     */
    @GetMapping("/active")
    @Operation(summary = "C端-获取首页轮播通知列表",
            description = "demo: GET /api/notice/active?communityId=123456，返回当前有效的上架通知，按排序号和创建时间排列",
            responses = {@ApiResponse(description = "成功返回通知列表", content = @Content(schema = @Schema(implementation = Notice.class)))})
    @NeedLogin
    public Result<List<Notice>> active(@RequestParam(required = false) Long communityId) {
        return Result.success(noticeService.getActiveNoticeList(communityId));
    }
}
