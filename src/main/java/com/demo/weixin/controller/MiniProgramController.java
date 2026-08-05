package com.demo.weixin.controller;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.binarywang.wx.miniapp.bean.WxMaPhoneNumberInfo;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.aliyun.oss.ServiceException;
import com.demo.common.core.result.Result;
import com.demo.common.core.util.SpringBeanContextHolder;
import com.demo.weixin.annotation.NeedLogin;
import com.demo.weixin.constant.Constants;
import com.demo.weixin.dao.EventDao;
import com.demo.weixin.entity.Event;
import com.demo.weixin.entity.User;
import com.demo.weixin.enums.PlatformTypeEnum;
import com.demo.weixin.service.UserService;
import com.demo.weixin.service.WxPaySwitchService;
import com.demo.weixin.service.store.PaymentService;
import com.demo.weixin.vo.LoginVo;
import com.demo.weixin.vo.UserProfileVO;
import com.demo.weixin.vo.UserPublicInfoVO;
import com.demo.weixin.vo.WeChatComplementVo;
import java.math.BigDecimal;
import com.github.binarywang.wxpay.bean.notify.WxPayNotifyV3Result;
import com.github.binarywang.wxpay.bean.notify.WxPayRefundNotifyV3Result;
import com.github.binarywang.wxpay.bean.transfer.TransferBillsNotifyResult;
import com.github.binarywang.wxpay.service.TransferService;
import com.github.binarywang.wxpay.service.WxPayService;
import com.mongodb.BasicDBObject;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * wx
 * alipay
 */
@RestController
@Tag(name = "小程序操作")
@RequestMapping(value = "/api/mini")
@Slf4j
public class MiniProgramController extends BaseController {

    @Autowired
    private WxMaService wxMaService;
    @Autowired
    private WxPaySwitchService wxPaySwitchService;
    @Autowired
    private UserService userService;


    @Autowired
    private EventDao eventDao;
    @Autowired
    private RedisTemplate<String, String> stringRedisTemplate;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Value("${spring.profiles.active}")
    private String applicationEnv;

    private static Map<String, String> getHeadersInfo(HttpServletRequest request) {

        Map<String, String> map = new HashMap<String, String>();

        Enumeration headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String key = (String) headerNames.nextElement();
            String value = request.getHeader(key);
            map.put(key, value);
        }

        return map;
    }

    private static String getBody(HttpServletRequest request) throws IOException {
        String body;
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader = null;
        try {
            InputStream inputStream = request.getInputStream();
            if (inputStream != null) {
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                char[] charBuffer = new char[128];
                int bytesRead = -1;
                while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
                    stringBuilder.append(charBuffer, 0, bytesRead);
                }
            } else {
                stringBuilder.append("");
            }
        } catch (IOException ex) {
            throw ex;
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException ex) {
                    throw ex;
                }
            }
        }

        body = stringBuilder.toString();
        return body;
    }

    /**
     * 微信小程序登陆接口
     *
     * @param code 动态code
     */
    @GetMapping("/loginByCode/{code}")
    @Operation(summary = "微信小程序登陆接口-根据code", 
            responses = {@ApiResponse(description = "成功信息", content = @Content(schema = @Schema(implementation = LoginVo.class)))})
    public Result<LoginVo> loginByCode(@PathVariable String code) {
        try {
            //根据appid和code获取session信息 如果正常执行肯定不为空 为空就报错了
            WxMaJscode2SessionResult session = getSession(code);
            String unionId = session.getUnionid();
            String openId = session.getOpenid();
            User user = userService.findOne(Criteria.where("openId").is(openId));
            if (ObjectUtil.isNull(user)) {
                //数据库无数据，说明是首次登录小程序 保存记录到数据库
                User insertUser = new User();
                insertUser.setOpenId(openId);
                insertUser.setUnionId(unionId);
                user = userService.insertUser(insertUser);
            }
            // TODO 首次登录后再次登录时未更新 unionId，需确认业务需求后补充更新逻辑
            return loginSuccess(user);
        } catch (Exception e) {
            log.error("登录失败, code={}", code, e);
            return Result.failed("授权失败");
        }
    }

    /**
     * {
     * "id": "EV-2018022511223320873",
     * "create_time": "2015-05-20T13:29:35+08:00",
     * "resource_type": "encrypt-resource",
     * "event_type": "TRANSACTION.SUCCESS",
     * "summary": "支付成功",
     * "resource": {
     * "original_type": "transaction",
     * "algorithm": "AEAD_AES_256_GCM",
     * "ciphertext": "",
     * "associated_data": "",
     * "nonce": ""
     * }
     * }
     *
     * @param params
     * @return
     */
    // webhook回调接口，返回格式与标准Result不同
    @PostMapping("/wx/pay/webhook")
    @Operation(summary = "微信支付回调通知处理",
            responses = {@ApiResponse(description = "微信支付回调处理结果")})
    public Map<String, String> payWebhook(@RequestBody Map params) {
        Map<String, String> result = new HashMap<>();
        try {
            Map<String, String> headersInfos = getHeadersInfo(request);
            String paramsJson = JSONUtil.toJsonStr(params);
            log.info("payWebhook header:\n" + JSONUtil.toJsonStr(headersInfos));
            log.info("payWebhook body:\n" + paramsJson);
            com.github.binarywang.wxpay.bean.notify.SignatureHeader header = new com.github.binarywang.wxpay.bean.notify.SignatureHeader();
            header.setTimeStamp(headersInfos.get("wechatpay-timestamp"));
            header.setNonce(headersInfos.get("wechatpay-nonce"));
            header.setSignature(headersInfos.get("wechatpay-signature"));
            header.setSerial(headersInfos.get("wechatpay-serial"));
            WxPayService wxPayService = wxPaySwitchService.merchantPayService();
            WxPayNotifyV3Result wxPayNotifyV3Result = wxPayService.parseOrderNotifyV3Result(paramsJson, header);
            log.info("payWebhook wxPayNotifyV3Result:\n" + JSONUtil.toJsonStr(wxPayNotifyV3Result));
            WxPayNotifyV3Result.DecryptNotifyResult notifyResult = wxPayNotifyV3Result.getResult();
            String outTradeNo = notifyResult.getOutTradeNo(); // 从 V3 解密对象中获取商户单号
            String transactionId = notifyResult.getTransactionId(); // 获取微信系统流水单号
            HashMap dbLog = mongoTemplate.findOne(new Query(Criteria.where("result.transactionId").is(notifyResult.getTransactionId())), HashMap.class, "wxPayWebhookRecords");
            if (Objects.isNull(dbLog)) {
                params.put("currentTime", DateUtil.current());
                params.put("header", header);
                params.put("wxPayNotifyResult", wxPayNotifyV3Result);
                BasicDBObject document = new BasicDBObject(params);
                mongoTemplate.insert(document, "wxPayWebhookRecords");
            }

            // 商城订单支付回调处理：outTradeNo 以 "S" 开头的是商城订单
            if (outTradeNo != null && outTradeNo.startsWith("S")) {
                try {
                    PaymentService paymentService = SpringBeanContextHolder.getBean(PaymentService.class);
                    // 传入微信支付流水号，便于后续对账追溯
                    paymentService.handlePayCallback(outTradeNo, true, transactionId);
                    log.info("商城订单支付回调处理完成，outTradeNo={}，transactionId={}", outTradeNo, transactionId);
                } catch (Exception ex) {
                    log.error("商城订单支付回调处理失败，outTradeNo={}", outTradeNo, ex);
                }
            }

        } catch (Exception e) {
            response.setStatus(500);
            result.put("code", "FAIL");
            result.put("message", "失败");
            log.error(e.getLocalizedMessage());
        }
        return result;
    }

    // webhook回调接口，返回格式与标准Result不同
    @PostMapping("/wx/refund/webhook")
    @Operation(summary = "微信退款回调通知处理",
            responses = {@ApiResponse(description = "微信退款回调处理结果")})
    public Map<String, String> refundWebhook(@RequestBody Map params) {
        Map<String, String> result = new HashMap<>();
        try {
            Map<String, String> headersInfos = getHeadersInfo(request);
            String paramsJson = JSONUtil.toJsonStr(params);
            log.info("refundWebhook header:\n" + JSONUtil.toJsonStr(headersInfos));
            log.info("refundWebhook body:\n" + paramsJson);
            com.github.binarywang.wxpay.bean.notify.SignatureHeader header = new com.github.binarywang.wxpay.bean.notify.SignatureHeader();
            header.setTimeStamp(headersInfos.get("wechatpay-timestamp"));
            header.setNonce(headersInfos.get("wechatpay-nonce"));
            header.setSignature(headersInfos.get("wechatpay-signature"));
            header.setSerial(headersInfos.get("wechatpay-serial"));
            WxPayService wxPayService = wxPaySwitchService.merchantPayService();
            WxPayRefundNotifyV3Result wxPayRefundNotifyV3Result = wxPayService.parseRefundNotifyV3Result(paramsJson, header);
            log.info("refundWebhook wxPayRefundNotifyV3Result:\n" + JSONUtil.toJsonStr(wxPayRefundNotifyV3Result));
            WxPayRefundNotifyV3Result.DecryptNotifyResult notifyResult = wxPayRefundNotifyV3Result.getResult();
            String outRefundNo = notifyResult.getOutRefundNo(); // 微信退款回调里获取的商户退款单号，如 R123456
            HashMap dbLog = mongoTemplate.findOne(new Query(Criteria.where("result.transactionId").is(notifyResult.getTransactionId())), HashMap.class, "wxRefundWebhookRecords");
            if (Objects.isNull(dbLog)) {
                params.put("currentTime", DateUtil.current());
                params.put("header", header);
                params.put("wxRefundNotifyResult", wxPayRefundNotifyV3Result);
                BasicDBObject document = new BasicDBObject(params);
                mongoTemplate.insert(document, "wxRefundWebhookRecords");
            }

            // 商城订单退款回调处理：outRefundNo 以 "RS" 开头的是商城订单退款
            if (outRefundNo != null && outRefundNo.startsWith("RS")) {
                try {
                    // 提取退款状态和微信退款单号
                    String refundStatus = notifyResult.getRefundStatus();
                    String refundId = notifyResult.getRefundId();
                    boolean success = "SUCCESS".equals(refundStatus);
                    PaymentService paymentService = SpringBeanContextHolder.getBean(PaymentService.class);
                    paymentService.handleRefundCallback(outRefundNo, success, refundId);
                    log.info("商城订单退款回调处理完成，outRefundNo={}，refundStatus={}，refundId={}",
                            outRefundNo, refundStatus, refundId);
                } catch (Exception ex) {
                    log.error("商城订单退款回调处理失败，outRefundNo={}", outRefundNo, ex);
                }
            }

        } catch (Exception e) {
            response.setStatus(500);
            result.put("code", "FAIL");
            result.put("message", "失败");
            log.error(e.getLocalizedMessage());
        }
        return result;
    }

    @PostMapping("/actionEvent")
    @Operation(summary = "埋点收录",
            responses = {@ApiResponse(description = "成功信息", content = @Content(schema = @Schema(implementation = String.class)))})
    public Result<String> sign(@RequestBody Event event) {
        event.setUserId(getCurrentUserId());
        event.setSessionId(request.getSession().getId());
        if (PlatformTypeEnum.ALIPAY.getKey() == getPlatform()) {
            event.setType(2);
        }
        eventDao.insertDocument(event);
        return Result.success();
    }


    /**
     * 二次授权
     * [变更 2026-08-03 21:30] 支持两种身份认证方式（二选一）：
     * 1. 手机号授权（decodeTelCode）：居民身份，获取微信手机号绑定到当前用户
     * 2. 志愿者ID（volunteerId）：志愿者身份，校验预录入的志愿者ID后绑定到当前用户
     * 所有用户必须先通过 loginByCode 建立会话，再通过此接口完成身份认证。
     *
     * @param vo 微信用户信息补充入参
     */
    @PostMapping("/auth")
    @Operation(summary = "二次授权（手机号授权或志愿者ID认证，二选一）",
            description = "demo: POST /api/mini/auth，居民传{\"decodeTelCode\":\"xxx\"}，志愿者传{\"volunteerId\":\"V20260001\"}",
            responses = {@ApiResponse(description = "成功信息", content = @Content(schema = @Schema(implementation = String.class)))})
    @NeedLogin
    public Result<String> auth(@RequestBody WeChatComplementVo vo) {
        //查询用户信息
        User user = userService.getUser(getCurrentUserId());
        if (ObjectUtil.isNull(user)) {
            return Result.failed("授权有误，请重进小程序！");
        }

        // ===== 志愿者ID认证 =====
        if (StringUtils.hasText(vo.getVolunteerId())) {
            try {
                userService.bindVolunteerIdentity(user.getUserId(), vo.getVolunteerId());
                // 刷新会话中的用户信息
                User updated = userService.getUser(user.getUserId());
                userService.resetUserSession(updated);
                log.info("志愿者身份认证成功，userId={}，volunteerId={}", user.getUserId(), vo.getVolunteerId());
                return Result.success("志愿者身份认证成功");
            } catch (com.demo.common.exception.BizException e) {
                return Result.failed(e.getMessage());
            } catch (Exception e) {
                log.error("志愿者身份认证异常，userId={}，volunteerId={}", user.getUserId(), vo.getVolunteerId(), e);
                return Result.failed("认证失败，请重试");
            }
        }

        // ===== 手机号授权认证（居民身份） =====
        if (StringUtils.hasText(vo.getDecodeTelCode())) {
            try {
                WxMaPhoneNumberInfo phoneNoInfo = wxMaService.getUserService().getPhoneNumber(vo.getDecodeTelCode());
                log.info(JSONUtil.toJsonStr(phoneNoInfo));
                String phoneNumber = phoneNoInfo.getPhoneNumber();
                Map<String, Object> updateMap = new HashMap<>();
                updateMap.put("userId", user.getUserId());
                updateMap.put("cellphone", phoneNumber);
                user = userService.updateMap(updateMap);

                // [新增 2026-08-03 21:30] 检查该手机号是否已预录入志愿者记录
                // 如果匹配，将当前用户切换为志愿者身份（合并账号，避免双账号）
                User volunteer = userService.findVolunteerByPhone(phoneNumber);
                if (volunteer != null && !volunteer.getUserId().equals(user.getUserId())) {
                    // 将志愿者身份信息合并到当前用户（当前用户已有openId和会话）
                    userService.mergeVolunteerToCurrentUser(user.getUserId(), volunteer.getUserId());
                    User updated = userService.getUser(user.getUserId());
                    userService.resetUserSession(updated);
                    log.info("手机号匹配志愿者，身份切换成功，phone={}，userId={}，volunteerId={}",
                            phoneNumber, user.getUserId(), updated.getVolunteerId());
                    return Result.success(phoneNumber);
                }

                // 普通居民，刷新会话
                userService.resetUserSession(user);
                return Result.success(phoneNumber);
            } catch (WxErrorException e) {
                log.error(e.getMessage(), e);
                return Result.failed("授权有误，请重进小程序！");
            }
        }

        return Result.failed("请选择手机号授权或输入志愿者ID");
    }

    /**
     * 更新用户资料（头像、昵称、描述）
     * 图片已通过OSS上传，本接口仅更新用户信息。
     */
    @PostMapping("/updateProfile")
    @Operation(summary = "更新用户资料（头像/昵称/描述）",
            responses = {@ApiResponse(description = "成功信息", content = @Content(schema = @Schema(implementation = User.class)))})
    @NeedLogin
    public Result<User> updateProfile(@RequestBody UserProfileVO vo) {
        Long userId = getCurrentUserId();
        User user = userService.getUser(userId);
        if (user == null) {
            return Result.failed("用户不存在");
        }
        // 更新资料
        Map<String, Object> updateMap = new HashMap<>();
        updateMap.put("userId", userId);
        if (StringUtils.hasText(vo.getNickName())) {
            updateMap.put("nickName", vo.getNickName());
        }
        if (StringUtils.hasText(vo.getAvatar())) {
            updateMap.put("avatar", vo.getAvatar());
        }
        if (vo.getDescription() != null) {
            updateMap.put("description", vo.getDescription());
        }
        user = userService.updateMap(updateMap);
        // 刷新会话中的用户信息
        userService.resetUserSession(user);
        return Result.success(user);
    }

    /**
     * 查询用户公开信息
     * [新增 2026-08-03 19:00] 查看其他用户的公开资料（昵称、头像、简介等），不暴露敏感信息
     * demo: GET /api/mini/user/publicInfo/123456
     */
    @GetMapping("/user/publicInfo/{userId}")
    @Operation(summary = "查询用户公开信息",
            description = "demo: GET /api/mini/user/publicInfo/123456，返回昵称、头像、简介等公开字段",
            responses = {@ApiResponse(description = "成功信息", content = @Content(schema = @Schema(implementation = UserPublicInfoVO.class)))})
    @NeedLogin
    public Result<UserPublicInfoVO> getPublicInfo(@PathVariable Long userId) {
        return Result.success(userService.getPublicInfo(userId));
    }

    private Result<LoginVo> loginSuccess(User user) {
        String oldToken = stringRedisTemplate.opsForValue().get(Constants.REDIS_SESSION_USER + ":" + user.getUserId());
        if (StringUtils.hasText(oldToken)) {
            stringRedisTemplate.delete(Constants.REDIS_SESSION_USER + ":" + oldToken);
        }
        String token = UUID.fastUUID().toString();
        userService.setUserSession(token, user);
        // [变更 2026-08-03 21:00] LoginVo增加role和volunteerId字段，返回用户角色信息
        Integer role = user.getRole() != null ? user.getRole() : 1;
        return Result.success(new LoginVo(token, user.getOpenId(), role, user.getVolunteerId()));
    }
    

    /**
     * 退出登录，清除Redis中的会话信息。
     */
    @PostMapping("/logout")
    @Operation(summary = "退出登录",
            responses = {
                    @ApiResponse(description = "成功信息", content = @Content(schema = @Schema(implementation = String.class)))
            })
    @NeedLogin
    public Result<String> logout() {
        Long userId = getCurrentUserId();
        String token = stringRedisTemplate.opsForValue().get(Constants.REDIS_SESSION_USER + ":" + userId);
        if (StringUtils.hasText(token)) {
            stringRedisTemplate.delete(Constants.REDIS_SESSION_USER + ":" + token);
            stringRedisTemplate.delete(Constants.REDIS_SESSION_USER + ":" + userId);
        }
        log.info("用户退出登录，userId={}", userId);
        return Result.success("已退出登录");
    }





    /**
     * 手机号脱敏处理，如 13800138000 → 138****8000。
     */
    private String maskPhone(String phone) {
        if (phone == null || phone.length() < 7) {
            return phone;
        }
        return phone.substring(0, 3) + "****" + phone.substring(phone.length() - 4);
    }

    private WxMaJscode2SessionResult getSession(String code) {
        try {
            //根据code获取session 里面包含openid和unionid等
            return wxMaService.getUserService().getSessionInfo(code);
        } catch (WxErrorException e) {
            throw new ServiceException("微信服务异常");
        }
    }

    // webhook回调接口，返回格式与标准Result不同
    @PostMapping("/wx/transfer/webhook")
    @Operation(summary = "转账回调",
            responses = {@ApiResponse(description = "微信转账回调处理结果")})
    public Map<String, String> wxTransferWebhook(@RequestBody Map params) {
        Map<String, String> result = new HashMap<>();
        try {
            Map<String, String> headersInfos = getHeadersInfo(request);
            String paramsJson = JSONUtil.toJsonStr(params);
            log.info("couponWebhook header:\n" + JSONUtil.toJsonStr(headersInfos));
            log.info("couponWebhook body:\n" + paramsJson);
            com.github.binarywang.wxpay.bean.notify.SignatureHeader header = new com.github.binarywang.wxpay.bean.notify.SignatureHeader();
            header.setTimeStamp(headersInfos.get("wechatpay-timestamp"));
            header.setNonce(headersInfos.get("wechatpay-nonce"));
            header.setSignature(headersInfos.get("wechatpay-signature"));
            header.setSerial(headersInfos.get("wechatpay-serial"));
            WxPayService wxPayService;
            if ("wzy".equals(applicationEnv)) {
                wxPayService = wxPaySwitchService.merchantPayService();
            } else {
                wxPayService = wxPaySwitchService.servicePayService();
            }
            TransferService transferService = wxPayService.getTransferService();
            TransferBillsNotifyResult transferBillsNotifyResult = transferService.parseTransferBillsNotifyResult(paramsJson, header);
            log.info("transferWebhook transferBillsNotifyResult:\n" + JSONUtil.toJsonStr(transferBillsNotifyResult));
            String outBillNo = transferBillsNotifyResult.getResult().getOutBillNo(); // 微信回调单号
            HashMap dbLog = mongoTemplate.findOne(new Query(Criteria.where("transferBillsNotifyResult.transferBillNo").is(transferBillsNotifyResult.getResult().getTransferBillNo())), HashMap.class, "wxTransferWebhookRecords");
            if (Objects.isNull(dbLog)) {
                params.put("currentTime", DateUtil.current());
                params.put("header", header);
                TransferBillsNotifyResult.DecryptNotifyResult notifyResult = transferBillsNotifyResult.getResult();
                params.put("transferBillsNotifyResult", notifyResult);
                BasicDBObject document = new BasicDBObject(params);
                mongoTemplate.insert(document, "wxTransferWebhookRecords");
            }

        } catch (Exception e) {
            response.setStatus(500);
            result.put("code", "FAIL");
            result.put("message", "失败");
            log.error(e.getLocalizedMessage());
        }
        return result;
    }
}
