package com.demo.weixin.controller.activity;

import com.demo.common.core.result.Result;
import com.demo.weixin.annotation.NeedLogin;
import com.demo.weixin.controller.BaseController;
import com.demo.weixin.entity.activity.ActivityTemplate;
import com.demo.weixin.service.activity.ActivityTemplateService;
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
 * 活动模板控制器
 * 提供模板列表、模板详情、使用模板创建活动接口。
 * 前端"活动广场"页面的模板详情页"制作同款"按钮调用 useTemplate 接口。
 */
@RestController
@Tag(name = "活动模板")
@RequestMapping("/api/activity/template")
@Slf4j
public class ActivityTemplateController extends BaseController {

    @Autowired
    private ActivityTemplateService activityTemplateService;

    @GetMapping("/list")
    @Operation(summary = "活动模板列表（分页）",
            description = "demo: GET /api/activity/template/list?pageNumber=1&pageSize=10&category=志愿服务",
            responses = {@ApiResponse(description = "成功返回分页模板列表", content = @Content(schema = @Schema(implementation = ActivityTemplate.class)))})
    @NeedLogin
    public Result<Page<ActivityTemplate>> list(
            @RequestParam(defaultValue = "1") Integer pageNumber,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String category) {
        // 与 ActivityController 保持一致，页码从1开始，转换为0开始
        pageNumber = pageNumber - 1;
        if (pageNumber < 0) {
            pageNumber = 0;
        }
        return Result.success(activityTemplateService.getTemplateList(pageNumber, pageSize, category));
    }

    @GetMapping("/detail/{templateId}")
    @Operation(summary = "活动模板详情",
            description = "demo: GET /api/activity/template/detail/101",
            responses = {@ApiResponse(description = "成功返回模板详情", content = @Content(schema = @Schema(implementation = ActivityTemplate.class)))})
    @NeedLogin
    public Result<ActivityTemplate> detail(@PathVariable Long templateId) {
        return Result.success(activityTemplateService.getTemplateDetail(templateId));
    }

    @PostMapping("/use/{templateId}")
    @Operation(summary = "使用模板创建活动（递增使用次数并返回模板数据）",
            description = "demo: POST /api/activity/template/use/101",
            responses = {@ApiResponse(description = "成功返回模板数据用于填充创建表单", content = @Content(schema = @Schema(implementation = ActivityTemplate.class)))})
    @NeedLogin
    public Result<ActivityTemplate> use(@PathVariable Long templateId) {
        return Result.success(activityTemplateService.useTemplate(templateId));
    }
}
