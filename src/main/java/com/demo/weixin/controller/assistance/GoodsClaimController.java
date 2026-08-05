package com.demo.weixin.controller.assistance;

import com.demo.common.core.result.Result;
import com.demo.weixin.annotation.AdminNeedLogin;
import com.demo.weixin.annotation.DistributedIdempotent;
import com.demo.weixin.annotation.ManageAuditLog;
import com.demo.weixin.annotation.NeedLogin;
import com.demo.weixin.constant.Constants;
import com.demo.weixin.controller.BaseController;
import com.demo.weixin.entity.assistance.GoodsClaim;
import com.demo.weixin.service.assistance.GoodsClaimService;
import com.demo.weixin.vo.assistance.ClaimAuditVO;
import com.demo.weixin.vo.assistance.ClaimQueryVO;
import com.demo.weixin.vo.assistance.GoodsClaimVO;
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
 * 消费帮扶-物资申领控制器
 * 提供物资申领提交、列表查询、管理员审核、物资发放接口。
 * 提交物资申领使用分布式幂等锁防重入，审核与发放操作记录审计日志。
 */
@RestController
@Tag(name = "消费帮扶-物资申领")
@RequestMapping("/api/assistance/claim")
@Slf4j
public class GoodsClaimController extends BaseController {

    @Autowired
    private GoodsClaimService goodsClaimService;

    @PostMapping("/submit")
    @Operation(summary = "提交物资申领",
            description = "demo: POST /api/assistance/claim/submit，body传GoodsClaimVO",
            responses = {@ApiResponse(description = "成功返回物资申领记录", content = @Content(schema = @Schema(implementation = GoodsClaim.class)))})
    @NeedLogin
    @DistributedIdempotent(prefix = Constants.LOCK_ASSISTANCE_CLAIM, message = "物资申领正在处理中，请不要高频连击")
    public Result<GoodsClaim> submit(@Valid @RequestBody GoodsClaimVO vo) {
        return Result.success(goodsClaimService.submitClaim(getCurrentUserId(), vo));
    }

    @PostMapping("/page")
    @Operation(summary = "查询物资申领列表",
            description = "demo: POST /api/assistance/claim/page，body传ClaimQueryVO",
            responses = {@ApiResponse(description = "成功返回物资申领分页", content = @Content(schema = @Schema(implementation = GoodsClaim.class)))})
    @NeedLogin
    public Result<Page<GoodsClaim>> page(@RequestBody @Valid ClaimQueryVO queryVO) {
        queryVO.setPageNumber(queryVO.getPageNumber() - 1);

        if (queryVO.getPageNumber() < 0) {

            queryVO.setPageNumber(0);

        }
        Page<GoodsClaim> page = goodsClaimService.queryClaimPage(getCurrentUserId(), queryVO);
        return Result.success(page, page.getTotalElements());
    }

    @PostMapping("/audit")
    @Operation(summary = "审核物资申领（管理员）",
            description = "demo: POST /api/assistance/claim/audit，body传ClaimAuditVO",
            responses = {@ApiResponse(description = "成功信息")})
    @AdminNeedLogin
    @ManageAuditLog(module = "消费帮扶-物资申领", action = "审核物资申领")
    public Result<Void> audit(@Valid @RequestBody ClaimAuditVO vo) {
        goodsClaimService.auditClaim(vo);
        return Result.success();
    }

    @PostMapping("/distribute/{claimId}")
    @Operation(summary = "发放物资（管理员）",
            description = "demo: POST /api/assistance/claim/distribute/123",
            responses = {@ApiResponse(description = "成功信息")})
    @AdminNeedLogin
    @ManageAuditLog(module = "消费帮扶-物资申领", action = "发放物资")
    public Result<Void> distribute(@PathVariable Long claimId) {
        goodsClaimService.distributeClaim(claimId);
        return Result.success();
    }
}
