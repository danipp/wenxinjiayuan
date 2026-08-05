package com.demo.weixin.controller.mine;

import com.demo.common.core.result.Result;
import com.demo.weixin.annotation.DistributedIdempotent;
import com.demo.weixin.annotation.ManageAuditLog;
import com.demo.weixin.annotation.NeedLogin;
import com.demo.weixin.constant.Constants;
import com.demo.weixin.controller.BaseController;
import com.demo.weixin.entity.mine.ServiceMember;
import com.demo.weixin.service.mine.ServiceMemberService;
import com.demo.weixin.vo.mine.ServiceMemberVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 个人中心-服务对象管理控制器
 * 提供服务对象列表查询、添加/编辑、删除接口。
 * 服务对象用于发布服务需求时快速选择被服务人信息。
 * 保存操作使用分布式幂等锁防重入，删除操作记录审计日志。
 */
@RestController
@Tag(name = "个人中心-服务对象")
@RequestMapping("/api/mine/serviceMember")
@Slf4j
public class ServiceMemberController extends BaseController {

    @Autowired
    private ServiceMemberService serviceMemberService;

    @GetMapping("/list")
    @Operation(summary = "服务对象列表",
            description = "demo: GET /api/mine/serviceMember/list",
            responses = {@ApiResponse(description = "成功返回服务对象列表", content = @Content(schema = @Schema(implementation = ServiceMember.class)))})
    @NeedLogin
    public Result<List<ServiceMember>> list() {
        return Result.success(serviceMemberService.getMemberList(getCurrentUserId()));
    }

    @PostMapping("/save")
    @Operation(summary = "添加/编辑服务对象",
            description = "demo: POST /api/mine/serviceMember/save，body传ServiceMemberVO",
            responses = {@ApiResponse(description = "成功返回服务对象", content = @Content(schema = @Schema(implementation = ServiceMember.class)))})
    @NeedLogin
    @DistributedIdempotent(prefix = Constants.LOCK_MINE_SERVICE_MEMBER_SAVE, message = "保存请求正在处理中，请不要高频连击")
    public Result<ServiceMember> save(@Valid @RequestBody ServiceMemberVO vo) {
        return Result.success(serviceMemberService.saveOrUpdateMember(getCurrentUserId(), vo));
    }

    @DeleteMapping("/delete/{memberId}")
    @Operation(summary = "删除服务对象",
            description = "demo: DELETE /api/mine/serviceMember/delete/123",
            responses = {@ApiResponse(description = "成功信息")})
    @NeedLogin
    @ManageAuditLog(module = "个人中心-服务对象", action = "删除服务对象")
    public Result<Void> delete(@PathVariable Long memberId) {
        serviceMemberService.deleteMember(getCurrentUserId(), memberId);
        return Result.success();
    }
}
