package com.demo.weixin.controller.mine;

import com.demo.common.core.result.Result;
import com.demo.weixin.annotation.AdminNeedLogin;
import com.demo.weixin.annotation.DistributedIdempotent;
import com.demo.weixin.annotation.ManageAuditLog;
import com.demo.weixin.annotation.NeedLogin;
import com.demo.weixin.constant.Constants;
import com.demo.weixin.controller.BaseController;
import com.demo.weixin.entity.mine.ResidentCertification;
import com.demo.weixin.service.mine.ResidentCertificationService;
import com.demo.weixin.vo.mine.CertificationAuditVO;
import com.demo.weixin.vo.mine.ResidentCertificationVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 个人中心-居民认证控制器
 * 提供认证提交、认证状态查询、管理员审核接口。
 * 提交认证使用分布式幂等锁防重入，审核操作记录审计日志。
 */
@RestController
@Tag(name = "个人中心-居民认证")
@RequestMapping("/api/mine/certification")
@Slf4j
public class ResidentCertificationController extends BaseController {

    @Autowired
    private ResidentCertificationService residentCertificationService;

    @PostMapping("/submit")
    @Operation(summary = "提交居民认证",
            description = "demo: POST /api/mine/certification/submit，body传ResidentCertificationVO",
            responses = {@ApiResponse(description = "成功返回认证记录", content = @Content(schema = @Schema(implementation = ResidentCertification.class)))})
    @NeedLogin
    @DistributedIdempotent(prefix = Constants.LOCK_MINE_CERTIFICATION_SUBMIT, message = "认证请求正在处理中，请不要高频连击")
    public Result<ResidentCertification> submit(@Valid @RequestBody ResidentCertificationVO vo) {
        return Result.success(residentCertificationService.submitCertification(getCurrentUserId(), vo));
    }

    @GetMapping("/my")
    @Operation(summary = "查询我的认证状态",
            description = "demo: GET /api/mine/certification/my",
            responses = {@ApiResponse(description = "成功返回认证记录", content = @Content(schema = @Schema(implementation = ResidentCertification.class)))})
    @NeedLogin
    public Result<ResidentCertification> my() {
        return Result.success(residentCertificationService.getMyCertification(getCurrentUserId()));
    }

    @PostMapping("/audit")
    @Operation(summary = "审核居民认证（管理员）",
            description = "demo: POST /api/mine/certification/audit，body传CertificationAuditVO",
            responses = {@ApiResponse(description = "成功信息")})
    @AdminNeedLogin
    @ManageAuditLog(module = "个人中心-居民认证", action = "审核居民认证")
    public Result<Void> audit(@Valid @RequestBody CertificationAuditVO vo) {
        residentCertificationService.auditCertification(vo);
        return Result.success();
    }
}
