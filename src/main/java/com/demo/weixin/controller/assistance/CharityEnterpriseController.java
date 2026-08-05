package com.demo.weixin.controller.assistance;

import com.demo.common.core.result.Result;
import com.demo.weixin.annotation.AdminNeedLogin;
import com.demo.weixin.annotation.ManageAuditLog;
import com.demo.weixin.annotation.NeedLogin;
import com.demo.weixin.controller.BaseController;
import com.demo.weixin.entity.assistance.CharityEnterprise;
import com.demo.weixin.service.assistance.CharityEnterpriseService;
import com.demo.weixin.vo.assistance.EnterpriseQueryVO;
import com.demo.weixin.vo.assistance.EnterpriseSaveVO;
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
 * 消费帮扶-爱心企业控制器
 * 提供爱心企业保存/编辑、列表查询、上下架切换、删除接口。
 * 保存、上下架、删除均为管理员操作并记录审计日志。
 */
@RestController
@Tag(name = "消费帮扶-爱心企业")
@RequestMapping("/api/assistance/enterprise")
@Slf4j
public class CharityEnterpriseController extends BaseController {

    @Autowired
    private CharityEnterpriseService charityEnterpriseService;

    @PostMapping("/save")
    @Operation(summary = "保存/编辑爱心企业（管理员）",
            description = "demo: POST /api/assistance/enterprise/save，body传EnterpriseSaveVO",
            responses = {@ApiResponse(description = "成功返回企业记录", content = @Content(schema = @Schema(implementation = CharityEnterprise.class)))})
    @AdminNeedLogin
    @ManageAuditLog(module = "消费帮扶-爱心企业", action = "保存/编辑企业")
    public Result<CharityEnterprise> save(@Valid @RequestBody EnterpriseSaveVO vo) {
        return Result.success(charityEnterpriseService.saveOrUpdateEnterprise(vo));
    }

    @PostMapping("/page")
    @Operation(summary = "查询爱心企业列表",
            description = "demo: POST /api/assistance/enterprise/page，body传EnterpriseQueryVO",
            responses = {@ApiResponse(description = "成功返回企业分页", content = @Content(schema = @Schema(implementation = CharityEnterprise.class)))})
    @NeedLogin
    public Result<Page<CharityEnterprise>> page(@RequestBody @Valid EnterpriseQueryVO queryVO) {
        queryVO.setPageNumber(queryVO.getPageNumber() - 1);

        if (queryVO.getPageNumber() < 0) {

            queryVO.setPageNumber(0);

        }
        Page<CharityEnterprise> page = charityEnterpriseService.queryEnterprisePage(queryVO);
        return Result.success(page, page.getTotalElements());
    }

    @PostMapping("/toggleStatus/{enterpriseId}")
    @Operation(summary = "爱心企业上下架切换（管理员）",
            description = "demo: POST /api/assistance/enterprise/toggleStatus/123",
            responses = {@ApiResponse(description = "成功信息")})
    @AdminNeedLogin
    @ManageAuditLog(module = "消费帮扶-爱心企业", action = "上下架切换")
    public Result<Void> toggleStatus(@PathVariable Long enterpriseId) {
        charityEnterpriseService.toggleStatus(enterpriseId);
        return Result.success();
    }

    @PostMapping("/delete/{enterpriseId}")
    @Operation(summary = "删除爱心企业（管理员）",
            description = "demo: POST /api/assistance/enterprise/delete/123",
            responses = {@ApiResponse(description = "成功信息")})
    @AdminNeedLogin
    @ManageAuditLog(module = "消费帮扶-爱心企业", action = "删除企业")
    public Result<Void> delete(@PathVariable Long enterpriseId) {
        charityEnterpriseService.deleteEnterprise(enterpriseId);
        return Result.success();
    }
}
