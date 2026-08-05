package com.demo.weixin.controller.assistance;

import com.demo.common.core.result.Result;
import com.demo.weixin.annotation.AdminNeedLogin;
import com.demo.weixin.annotation.DistributedIdempotent;
import com.demo.weixin.annotation.ManageAuditLog;
import com.demo.weixin.annotation.NeedLogin;
import com.demo.weixin.constant.Constants;
import com.demo.weixin.controller.BaseController;
import com.demo.weixin.entity.assistance.DonationApply;
import com.demo.weixin.service.assistance.DonationService;
import com.demo.weixin.vo.assistance.DonationApplyVO;
import com.demo.weixin.vo.assistance.DonationAuditVO;
import com.demo.weixin.vo.assistance.DonationQueryVO;
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
 * 消费帮扶-捐赠申请控制器
 * 提供捐赠申请提交、列表查询、管理员审核、捐赠排行接口。
 * 提交捐赠申请使用分布式幂等锁防重入，审核操作记录审计日志。
 */
@RestController
@Tag(name = "消费帮扶-捐赠申请")
@RequestMapping("/api/assistance/donation")
@Slf4j
public class DonationController extends BaseController {

    @Autowired
    private DonationService donationService;

    @PostMapping("/submit")
    @Operation(summary = "提交捐赠申请",
            description = "demo: POST /api/assistance/donation/submit，body传DonationApplyVO",
            responses = {@ApiResponse(description = "成功返回捐赠申请记录", content = @Content(schema = @Schema(implementation = DonationApply.class)))})
    @NeedLogin
    @DistributedIdempotent(prefix = Constants.LOCK_ASSISTANCE_DONATION, message = "捐赠申请正在处理中，请不要高频连击")
    public Result<DonationApply> submit(@Valid @RequestBody DonationApplyVO vo) {
        return Result.success(donationService.submitDonation(getCurrentUserId(), vo));
    }

    @PostMapping("/page")
    @Operation(summary = "查询捐赠申请列表",
            description = "demo: POST /api/assistance/donation/page，body传DonationQueryVO",
            responses = {@ApiResponse(description = "成功返回捐赠申请分页", content = @Content(schema = @Schema(implementation = DonationApply.class)))})
    @NeedLogin
    public Result<Page<DonationApply>> page(@RequestBody @Valid DonationQueryVO queryVO) {
        queryVO.setPageNumber(queryVO.getPageNumber() - 1);

        if (queryVO.getPageNumber() < 0) {

            queryVO.setPageNumber(0);

        }
        Page<DonationApply> page = donationService.queryDonationPage(getCurrentUserId(), queryVO);
        return Result.success(page, page.getTotalElements());
    }

    @PostMapping("/audit")
    @Operation(summary = "审核捐赠申请（管理员）",
            description = "demo: POST /api/assistance/donation/audit，body传DonationAuditVO",
            responses = {@ApiResponse(description = "成功信息")})
    @AdminNeedLogin
    @ManageAuditLog(module = "消费帮扶-捐赠申请", action = "审核捐赠申请")
    public Result<Void> audit(@Valid @RequestBody DonationAuditVO vo) {
        donationService.auditDonation(vo);
        return Result.success();
    }

    // [变更 2026-08-03 18:00] 增加communityId参数用于社区数据隔离
    @GetMapping("/ranking")
    @Operation(summary = "捐赠排行",
            description = "demo: GET /api/assistance/donation/ranking?limit=10&communityId=123456",
            responses = {@ApiResponse(description = "成功返回捐赠排行列表", content = @Content(schema = @Schema(implementation = DonationApply.class)))})
    @NeedLogin
    public Result<List<DonationApply>> ranking(@RequestParam(defaultValue = "10") int limit,
                                                @RequestParam(required = false) Long communityId) {
        return Result.success(donationService.getDonationRanking(limit, communityId));
    }
}
