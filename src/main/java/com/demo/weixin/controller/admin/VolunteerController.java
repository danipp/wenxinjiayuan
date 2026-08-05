package com.demo.weixin.controller.admin;

import com.demo.common.core.result.Result;
import com.demo.weixin.annotation.AdminNeedLogin;
import com.demo.weixin.annotation.DistributedIdempotent;
import com.demo.weixin.annotation.ManageAuditLog;
import com.demo.weixin.constant.Constants;
import com.demo.weixin.entity.User;
import com.demo.weixin.service.UserService;
import com.demo.weixin.vo.VolunteerImportVO;
import com.demo.weixin.vo.VolunteerQueryVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

/**
 * 志愿者管理控制器（管理员后台）
 * [新增 2026-08-03 21:00] 管理员录入、编辑、查询、启停志愿者
 * 志愿者ID由管理员手动录入或第三方平台返回，志愿者可通过ID或手机号授权登录
 */
@RestController
@Slf4j
@RequestMapping("/manage/api/volunteer")
@Tag(name = "后台-志愿者管理")
public class VolunteerController extends BaseAdminController {

    @Autowired
    private UserService userService;

    /**
     * 录入志愿者
     * demo: POST /manage/api/volunteer/import
     */
    @PostMapping("/import")
    @Operation(summary = "录入志愿者",
            description = "demo: POST /manage/api/volunteer/import，入参{\"volunteerId\":\"V20260001\",\"nickName\":\"张三\",\"cellphone\":\"13800138000\"}",
            responses = {@ApiResponse(description = "成功信息", content = @Content(schema = @Schema(implementation = User.class)))})
    @AdminNeedLogin
    @ManageAuditLog(module = "志愿者管理", action = "录入志愿者")
    @DistributedIdempotent(prefix = Constants.LOCK_VOLUNTEER_IMPORT, message = "请勿重复提交")
    public Result<User> importVolunteer(@RequestBody VolunteerImportVO vo) {
        User user = userService.importVolunteer(vo);
        return Result.success(user);
    }

    /**
     * 编辑志愿者
     * demo: POST /manage/api/volunteer/update
     */
    @PostMapping("/update")
    @Operation(summary = "编辑志愿者",
            description = "demo: POST /manage/api/volunteer/update，入参{\"userId\":123,\"volunteerId\":\"V20260001\",\"nickName\":\"张三\"}",
            responses = {@ApiResponse(description = "成功信息", content = @Content(schema = @Schema(implementation = User.class)))})
    @AdminNeedLogin
    @ManageAuditLog(module = "志愿者管理", action = "编辑志愿者")
    @DistributedIdempotent(prefix = Constants.LOCK_VOLUNTEER_UPDATE, message = "请勿重复提交")
    public Result<User> updateVolunteer(@RequestBody VolunteerImportVO vo) {
        User user = userService.updateVolunteer(vo);
        return Result.success(user);
    }

    /**
     * 分页查询志愿者列表
     * demo: POST /manage/api/volunteer/page
     */
    @PostMapping("/page")
    @Operation(summary = "分页查询志愿者列表",
            description = "demo: POST /manage/api/volunteer/page，入参{\"pageNumber\":1,\"pageSize\":20,\"keyword\":\"V2026\"}",
            responses = {@ApiResponse(description = "成功信息", content = @Content(schema = @Schema(implementation = Page.class)))})
    @AdminNeedLogin
    public Result<Page<User>> page(@RequestBody VolunteerQueryVO vo) {
        return Result.success(userService.queryVolunteerPage(vo));
    }

    /**
     * 切换志愿者状态（启用/停用）
     * demo: POST /manage/api/volunteer/toggleStatus?userId=123
     */
    @PostMapping("/toggleStatus")
    @Operation(summary = "切换志愿者状态（启用/停用）",
            description = "demo: POST /manage/api/volunteer/toggleStatus?userId=123",
            responses = {@ApiResponse(description = "成功信息", content = @Content(schema = @Schema(implementation = String.class)))})
    @AdminNeedLogin
    @ManageAuditLog(module = "志愿者管理", action = "切换志愿者状态")
    public Result<String> toggleStatus(@RequestParam Long userId) {
        userService.toggleVolunteerStatus(userId);
        return Result.success("操作成功");
    }

    /**
     * 获取志愿者详情
     * demo: GET /manage/api/volunteer/detail?userId=123
     */
    @GetMapping("/detail")
    @Operation(summary = "获取志愿者详情",
            description = "demo: GET /manage/api/volunteer/detail?userId=123",
            responses = {@ApiResponse(description = "成功信息", content = @Content(schema = @Schema(implementation = User.class)))})
    @AdminNeedLogin
    public Result<User> detail(@RequestParam Long userId) {
        User user = userService.getUser(userId);
        if (user == null) {
            return Result.failed("用户不存在");
        }
        // 脱敏：不返回密码
        user.setPassWord(null);
        return Result.success(user);
    }
}
