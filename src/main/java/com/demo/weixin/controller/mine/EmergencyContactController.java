package com.demo.weixin.controller.mine;

import com.demo.common.core.result.Result;
import com.demo.weixin.annotation.DistributedIdempotent;
import com.demo.weixin.annotation.ManageAuditLog;
import com.demo.weixin.annotation.NeedLogin;
import com.demo.weixin.constant.Constants;
import com.demo.weixin.controller.BaseController;
import com.demo.weixin.entity.mine.EmergencyContact;
import com.demo.weixin.service.mine.EmergencyContactService;
import com.demo.weixin.vo.mine.EmergencyContactVO;
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
 * 个人中心-紧急联系人控制器
 * 提供联系人列表查询、添加/编辑、删除接口。
 * 删除操作记录审计日志，保存操作使用分布式幂等锁防重入。
 */
@RestController
@Tag(name = "个人中心-紧急联系人")
@RequestMapping("/api/mine/contact")
@Slf4j
public class EmergencyContactController extends BaseController {

    @Autowired
    private EmergencyContactService emergencyContactService;

    @GetMapping("/list")
    @Operation(summary = "紧急联系人列表",
            responses = {@ApiResponse(description = "成功信息", content = @Content(schema = @Schema(implementation = EmergencyContact.class)))})
    @NeedLogin
    public Result<List<EmergencyContact>> list() {
        return Result.success(emergencyContactService.getContactList(getCurrentUserId()));
    }

    @PostMapping("/save")
    @Operation(summary = "添加/编辑联系人",
            responses = {@ApiResponse(description = "成功信息", content = @Content(schema = @Schema(implementation = EmergencyContact.class)))})
    @NeedLogin
    @DistributedIdempotent(prefix = Constants.LOCK_MINE_CONTACT_SAVE, message = "保存请求正在处理中，请不要高频连击")
    public Result<EmergencyContact> save(@Valid @RequestBody EmergencyContactVO vo) {
        return Result.success(emergencyContactService.saveOrUpdateContact(getCurrentUserId(), vo));
    }

    @DeleteMapping("/delete/{contactId}")
    @Operation(summary = "删除联系人",
            responses = {@ApiResponse(description = "成功信息")})
    @NeedLogin
    @ManageAuditLog(module = "个人中心-紧急联系人", action = "删除联系人")
    public Result<Void> delete(@PathVariable Long contactId) {
        emergencyContactService.deleteContact(getCurrentUserId(), contactId);
        return Result.success();
    }
}
