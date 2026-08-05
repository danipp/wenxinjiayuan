package com.demo.weixin.controller;

import com.demo.common.core.result.Result;
import com.demo.weixin.annotation.NeedLogin;
import com.demo.weixin.service.OssService;
import com.demo.weixin.vo.OssPolicyVo;
import com.demo.weixin.vo.OssUploadPolicyVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
@RestController
@Tag(name = "小程序端-oss配置获取")
@RequestMapping(value = "/api/oss")
@Slf4j
public class OssController extends BaseController {

    @Autowired
    private OssService ossService;

    @PostMapping("/policyToken")
    @CrossOrigin
    @NeedLogin
    @Operation(summary = "前端直传-获取oss凭证",
            responses = {@ApiResponse(description = "成功信息", content = @Content(schema = @Schema(implementation = OssUploadPolicyVO.class)))})
    public Result<OssUploadPolicyVO> policyToken(@RequestBody OssPolicyVo ossPolicyVo) {
        return ossService.policyToken(ossPolicyVo, "WARM_HOME/"+new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + "/");
    }
}
