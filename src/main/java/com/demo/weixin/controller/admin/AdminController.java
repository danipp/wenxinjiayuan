package com.demo.weixin.controller.admin;

import cn.hutool.core.lang.UUID;
import com.demo.common.core.result.Result;
import com.demo.common.core.result.ResultCode;
import com.demo.common.core.util.*;
import com.demo.weixin.annotation.AdminNeedLogin;
import com.demo.weixin.annotation.DistributedIdempotent;
import com.demo.weixin.annotation.ManageAuditLog;
import com.demo.weixin.constant.Constants;
import com.demo.weixin.entity.Admin;
import com.demo.weixin.enums.LimitCountTypeEnum;
import com.demo.weixin.service.AdminService;
import com.demo.weixin.service.ILimitCountService;
import com.demo.weixin.vo.AdminLoginVo;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;


/**
 * Zane
 */
@RestController
@Slf4j
@RequestMapping("/manage/api/admin")
@Tag(name = "后台用户")
public class AdminController extends BaseAdminController {

    @Autowired
    private AdminService adminService;
    @Autowired
    private ILimitCountService limitCountService;

    @GetMapping("/basicInfo")
    @Operation(summary = "账户基本信息",
            responses = {
                    @ApiResponse(description = "成功信息", content = @Content(schema = @Schema(implementation = Admin.class)))
            })
    @AdminNeedLogin
    public Result<Admin> basicInfo() {
        Admin admin = adminService.getAdminById(getCurrentAdminId());
        admin.setPassWord(null);
        return Result.success(admin);
    }


    @PostMapping("/login")
    @Operation(summary = "账号密码登录",
            responses = {@ApiResponse(description = "成功信息", content = @Content(schema = @Schema(implementation = String.class)))})
    public Result<String> login(@RequestBody AdminLoginVo adminLoginVo) {
        try {
            BeanValueTrimUtil.beanValueTrim(adminLoginVo);
            ValidatorUtils.validateEntity(adminLoginVo);
            String ip = IpUtil.getRequestIp(request);
            int limit = limitCountService.getLimitCount(ip + ":admin:" + adminLoginVo.getCellphone(), LimitCountTypeEnum.LOGIN.getKey());
            if (limit >= Constants.ERROR_LIMIT_COUNT) {
                return Result.failed("操作频繁，请" + Constants.LIMIT_FREQUENT_HOUR_INSTANCE + "小时后再试");
            }
            String decrypt = RSAUtil.decrypt(adminLoginVo.getPassWord());
            if (StringUtils.isEmpty(decrypt)) {
                return Result.failed("密码有误");
            }
            adminLoginVo.setPassWord(decrypt);
            Criteria criteria = new Criteria();
            criteria.and("cellphone").is(adminLoginVo.getCellphone());
            //先查出用户存不存在
            Admin admin = adminService.findOne(criteria);
            if (Objects.isNull(admin)) {
                return Result.failed(ResultCode.USER_ERROR);
            } else {
                if (!admin.getPassWord().equals(AesUtil.encrypt(adminLoginVo.getPassWord()))) {
                    limitCountService.updateLimitCount(ip + ":admin:" + adminLoginVo.getCellphone(), LimitCountTypeEnum.LOGIN.getKey(), ++limit);
                    return Result.failed("登录账号或密码错误，剩余尝试次数为：" + (Constants.ERROR_LIMIT_COUNT - limit));
                }
            }
            limitCountService.deleteCountLimit(ip + ":admin:" + adminLoginVo.getCellphone(), LimitCountTypeEnum.LOGIN.getKey());
            return loginSuccess(admin);
        } catch (Exception e) {
            errLog("admin login errMsg:{}", e);
        }
        return Result.failed();
    }

    /**
     * 登录成功返回token
     *
     * @param admin
     * @return
     */
    private Result<String> loginSuccess(Admin admin) {
        String oldToken = stringRedisTemplate.opsForValue().get(Constants.REDIS_SESSION_ADMIN + ":" + admin.getAdminId());
        if (StringUtils.hasText(oldToken)) {
            stringRedisTemplate.delete(Constants.REDIS_SESSION_ADMIN + ":" + oldToken);
        }
        String token = UUID.fastUUID().toString();
        adminService.setAdminSession(token, admin);
        return Result.success(token);
    }


    private void errLog(String s, Exception e) {
        log.error(s, e.getLocalizedMessage());
        log.error(e.getLocalizedMessage(), e);
    }
}
