package com.demo.weixin.controller.assistance;

import com.demo.common.core.result.Result;
import com.demo.weixin.annotation.AdminNeedLogin;
import com.demo.weixin.annotation.DistributedIdempotent;
import com.demo.weixin.annotation.ManageAuditLog;
import com.demo.weixin.annotation.NeedLogin;
import com.demo.weixin.constant.Constants;
import com.demo.weixin.controller.BaseController;
import com.demo.weixin.entity.assistance.AssistanceApply;
import com.demo.weixin.service.assistance.AssistanceApplyService;
import com.demo.weixin.vo.assistance.AssistanceApplyVO;
import com.demo.weixin.vo.assistance.AssistanceAuditVO;
import com.demo.weixin.vo.assistance.AssistanceQueryVO;
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

/**
 * 消费帮扶-帮扶申请控制器
 * 提供帮扶申请提交、列表查询、管理员审核接口。
 * 提交帮扶申请使用分布式幂等锁防重入，审核操作记录审计日志。
 */
@RestController
@Tag(name = "消费帮扶-帮扶申请")
@RequestMapping("/api/assistance/apply")
@Slf4j
public class AssistanceApplyController extends BaseController {

    @Autowired
    private AssistanceApplyService assistanceApplyService;

    @PostMapping("/submit")
    @Operation(summary = "提交帮扶申请",
            description = "demo: POST /api/assistance/apply/submit，body传AssistanceApplyVO",
            responses = {@ApiResponse(description = "成功返回帮扶申请记录", content = @Content(schema = @Schema(implementation = AssistanceApply.class)))})
    @NeedLogin
    @DistributedIdempotent(prefix = Constants.LOCK_ASSISTANCE_APPLY, message = "帮扶申请正在处理中，请不要高频连击")
    public Result<AssistanceApply> submit(@Valid @RequestBody AssistanceApplyVO vo) {
        return Result.success(assistanceApplyService.submitApply(getCurrentUserId(), vo));
    }

    @PostMapping("/page")
    @Operation(summary = "查询帮扶申请列表",
            description = "demo: POST /api/assistance/apply/page，body传AssistanceQueryVO",
            responses = {@ApiResponse(description = "成功返回帮扶申请分页", content = @Content(schema = @Schema(implementation = AssistanceApply.class)))})
    @NeedLogin
    public Result<Page<AssistanceApply>> page(@RequestBody @Valid AssistanceQueryVO queryVO) {
        queryVO.setPageNumber(queryVO.getPageNumber() - 1);

        if (queryVO.getPageNumber() < 0) {

            queryVO.setPageNumber(0);

        }
        Page<AssistanceApply> page = assistanceApplyService.queryApplyPage(getCurrentUserId(), queryVO);
        return Result.success(page, page.getTotalElements());
    }

    @PostMapping("/audit")
    @Operation(summary = "审核帮扶申请（管理员）",
            description = "demo: POST /api/assistance/apply/audit，body传AssistanceAuditVO",
            responses = {@ApiResponse(description = "成功信息")})
    @AdminNeedLogin
    @ManageAuditLog(module = "消费帮扶-帮扶申请", action = "审核帮扶申请")
    public Result<Void> audit(@Valid @RequestBody AssistanceAuditVO vo) {
        assistanceApplyService.auditApply(vo);
        return Result.success();
    }
}
