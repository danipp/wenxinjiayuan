package com.demo.weixin.controller.ad;

import com.demo.common.core.result.Result;
import com.demo.weixin.annotation.AdminNeedLogin;
import com.demo.weixin.annotation.ManageAuditLog;
import com.demo.weixin.annotation.NeedLogin;
import com.demo.weixin.controller.BaseController;
import com.demo.weixin.entity.ad.Ad;
import com.demo.weixin.service.ad.AdService;
import com.demo.weixin.vo.ad.AdQueryVO;
import com.demo.weixin.vo.ad.AdSaveVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 广告位控制器
 * C端接口：按位置查询有效广告
 * 管理端接口：增删改查、上下架
 */
@RestController
@Tag(name = "广告位")
@RequestMapping("/api/ad")
@Slf4j
public class AdController extends BaseController {

    @Autowired
    private AdService adService;

    /**
     * C端-查询指定位置的有效广告列表
     * 无需登录，广告为公开内容。
     */
    @GetMapping("/list/{position}")
    @Operation(summary = "C端-查询指定位置的有效广告列表",
            responses = {@ApiResponse(description = "成功信息", content = @Content(schema = @Schema(implementation = Ad.class)))})
    public Result<List<Ad>> getActiveAds(@PathVariable String position) {
        return Result.success(adService.getActiveAdsByPosition(position));
    }

    /**
     * 管理端-查询广告列表
     */
    @PostMapping("/manage/list")
    @Operation(summary = "管理端-查询广告列表",
            responses = {@ApiResponse(description = "成功信息", content = @Content(schema = @Schema(implementation = Ad.class)))})
    @AdminNeedLogin
    public Result<List<Ad>> getAdList(@RequestBody AdQueryVO vo) {
        return Result.success(adService.getAdList(vo.getPosition()));
    }

    /**
     * 管理端-获取广告详情
     */
    @GetMapping("/manage/detail/{adId}")
    @Operation(summary = "管理端-获取广告详情",
            responses = {@ApiResponse(description = "成功信息", content = @Content(schema = @Schema(implementation = Ad.class)))})
    @AdminNeedLogin
    public Result<Ad> getAdDetail(@PathVariable Long adId) {
        return Result.success(adService.getAdDetail(adId));
    }

    /**
     * 管理端-新增/编辑广告
     */
    @PostMapping("/manage/save")
    @Operation(summary = "管理端-新增/编辑广告",
            responses = {@ApiResponse(description = "成功信息", content = @Content(schema = @Schema(implementation = Ad.class)))})
    @AdminNeedLogin
    @ManageAuditLog(module = "广告位管理", action = "新增/编辑广告")
    public Result<Ad> save(@RequestBody AdSaveVO vo) {
        return Result.success(adService.saveOrUpdate(vo));
    }

    /**
     * 管理端-广告上下架切换
     */
    @PostMapping("/manage/toggle/{adId}")
    @Operation(summary = "管理端-广告上下架切换",
            responses = {@ApiResponse(description = "成功信息", content = @Content(schema = @Schema(implementation = String.class)))})
    @AdminNeedLogin
    @ManageAuditLog(module = "广告位管理", action = "广告上下架切换")
    public Result<String> toggleStatus(@PathVariable Long adId) {
        adService.toggleStatus(adId);
        return Result.success("操作成功");
    }

    /**
     * 管理端-删除广告
     */
    @DeleteMapping("/manage/delete/{adId}")
    @Operation(summary = "管理端-删除广告",
            responses = {@ApiResponse(description = "成功信息", content = @Content(schema = @Schema(implementation = String.class)))})
    @AdminNeedLogin
    @ManageAuditLog(module = "广告位管理", action = "删除广告")
    public Result<String> deleteAd(@PathVariable Long adId) {
        adService.deleteAd(adId);
        return Result.success("删除成功");
    }
}
