package com.demo.weixin.controller.community;

import com.demo.common.core.result.Result;
import com.demo.weixin.annotation.AdminNeedLogin;
import com.demo.weixin.annotation.DistributedIdempotent;
import com.demo.weixin.annotation.ManageAuditLog;
import com.demo.weixin.annotation.NeedLogin;
import com.demo.weixin.constant.Constants;
import com.demo.weixin.controller.BaseController;
import com.demo.weixin.entity.User;
import com.demo.weixin.entity.community.Community;
import com.demo.weixin.enums.community.CommunityStatusEnum;
import com.demo.weixin.service.UserService;
import com.demo.weixin.service.community.CommunityService;
import com.demo.weixin.vo.community.CommunityQueryVO;
import com.demo.weixin.vo.community.CommunitySaveVO;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 社区控制器
 * <p>
 * 提供社区管理接口（管理员）和社区列表接口（C端用户）。
 * 管理员操作（新增/编辑/删除/切换状态）需登录后台并记录审计日志。
 * C端用户通过列表接口获取可选社区，选择后 communityId 用于各业务模块的数据隔离。
 * </p>
 */
@RestController
@Tag(name = "社区管理")
@RequestMapping("/api/community")
@Slf4j
public class CommunityController extends BaseController {

    @Autowired
    private CommunityService communityService;
    // [新增 2026-08-03 18:10] 用户服务，用于切换社区时更新用户信息
    @Autowired
    private UserService userService;

    /**
     * 新增社区（管理员）
     */
    @PostMapping("/create")
    @Operation(summary = "新增社区",
            description = "demo: POST /api/community/create，body传CommunitySaveVO",
            responses = {@ApiResponse(description = "成功返回社区信息", content = @Content(schema = @Schema(implementation = Community.class)))})
    @AdminNeedLogin
    @ManageAuditLog(module = "社区管理", action = "新增社区")
    @DistributedIdempotent(prefix = Constants.LOCK_COMMUNITY_SAVE, message = "社区保存请求正在处理中，请不要高频连击")
    public Result<Community> create(@Valid @RequestBody CommunitySaveVO vo) {
        return Result.success(communityService.createCommunity(vo));
    }

    /**
     * 编辑社区（管理员）
     */
    @PostMapping("/update")
    @Operation(summary = "编辑社区",
            description = "demo: POST /api/community/update，body传CommunitySaveVO（communityId必传）",
            responses = {@ApiResponse(description = "成功返回社区信息", content = @Content(schema = @Schema(implementation = Community.class)))})
    @AdminNeedLogin
    @ManageAuditLog(module = "社区管理", action = "编辑社区")
    @DistributedIdempotent(prefix = Constants.LOCK_COMMUNITY_SAVE, message = "社区保存请求正在处理中，请不要高频连击")
    public Result<Community> update(@Valid @RequestBody CommunitySaveVO vo) {
        return Result.success(communityService.updateCommunity(vo));
    }

    /**
     * 删除社区（管理员，逻辑删除）
     */
    @PostMapping("/delete/{communityId}")
    @Operation(summary = "删除社区",
            description = "demo: POST /api/community/delete/123456",
            responses = {@ApiResponse(description = "成功信息")})
    @AdminNeedLogin
    @ManageAuditLog(module = "社区管理", action = "删除社区")
    public Result<Void> delete(@PathVariable Long communityId) {
        communityService.deleteCommunity(communityId);
        return Result.success();
    }

    /**
     * 切换社区状态（管理员，启用/禁用）
     */
    @PostMapping("/toggleStatus/{communityId}")
    @Operation(summary = "切换社区状态",
            description = "demo: POST /api/community/toggleStatus/123456",
            responses = {@ApiResponse(description = "成功信息")})
    @AdminNeedLogin
    @ManageAuditLog(module = "社区管理", action = "切换社区状态")
    public Result<Void> toggleStatus(@PathVariable Long communityId) {
        communityService.toggleStatus(communityId);
        return Result.success();
    }

    /**
     * 社区详情
     */
    @GetMapping("/detail/{communityId}")
    @Operation(summary = "社区详情",
            description = "demo: GET /api/community/detail/123456",
            responses = {@ApiResponse(description = "成功返回社区信息", content = @Content(schema = @Schema(implementation = Community.class)))})
    public Result<Community> detail(@PathVariable Long communityId) {
        return Result.success(communityService.getCommunityDetail(communityId));
    }

    /**
     * 社区分页列表（管理员，可查全部状态）
     */
    @PostMapping("/page")
    @Operation(summary = "社区分页列表（管理员）",
            description = "demo: POST /api/community/page，body传CommunityQueryVO",
            responses = {@ApiResponse(description = "成功返回社区分页", content = @Content(schema = @Schema(implementation = Community.class)))})
    @AdminNeedLogin
    public Result<Page<Community>> page(@RequestBody @Valid CommunityQueryVO queryVO) {
        queryVO.setPageNumber(queryVO.getPageNumber() - 1);

        if (queryVO.getPageNumber() < 0) {

            queryVO.setPageNumber(0);

        }
        Page<Community> page = communityService.queryCommunityPage(queryVO);
        return Result.success(page, page.getTotalElements());
    }

    /**
     * 获取启用的社区列表（C端用户选择社区用）
     * 前端首页左上角切换社区时调用此接口。
     */
    @GetMapping("/list")
    @Operation(summary = "获取启用的社区列表",
            description = "demo: GET /api/community/list，C端用户切换社区时调用",
            responses = {@ApiResponse(description = "成功返回社区列表", content = @Content(schema = @Schema(implementation = Community.class)))})
    public Result<List<Community>> list() {
        return Result.success(communityService.getActiveCommunityList());
    }

    /**
     * [新增 2026-08-03 18:10] C端用户切换社区
     * 用户在首页左上角选择社区后调用此接口，后端更新用户的 communityId 和 communityName（冗余字段）。
     * 切换后各业务模块通过 communityId 实现数据隔离。
     */
    @PostMapping("/switch/{communityId}")
    @Operation(summary = "切换社区（C端用户）",
            description = "demo: POST /api/community/switch/123456，用户选择社区后调用，更新当前用户的社区归属",
            responses = {@ApiResponse(description = "成功返回更新后的用户信息", content = @Content(schema = @Schema(implementation = User.class)))})
    @NeedLogin
    public Result<User> switchCommunity(@PathVariable Long communityId) {
        // 校验社区存在且启用
        Community community = communityService.getCommunityDetail(communityId);
        if (!CommunityStatusEnum.ACTIVE.getCode().equals(community.getStatus())) {
            return Result.failed("该社区未启用，不可选择");
        }
        // 更新用户的 communityId 和 communityName
        Long userId = getCurrentUserId();
        Map<String, Object> updateMap = new HashMap<>();
        updateMap.put("userId", userId);
        updateMap.put("communityId", communityId);
        updateMap.put("communityName", community.getName());
        User user = userService.updateMap(updateMap);
        // 刷新会话中的用户信息
        userService.resetUserSession(user);
        log.info("用户切换社区，userId={}，communityId={}，communityName={}", userId, communityId, community.getName());
        return Result.success(user);
    }
}
