package com.demo.common.core.result;

import java.io.Serializable;

public enum ResultCode implements IResultCode, Serializable {
    SUCCESS("00000", "ok"),
    USER_ERROR("A0001", "用户信息为空"),
    PARAM_IS_NULL("A0410", "请求必填参数为空"),
    // [新增 2026-07-31 16:19] 参数校验失败错误码，用于 @Valid 校验不通过时的返回
    PARAM_VALIDATE_FAILED("A0420", "参数校验失败"),
    AUTH_FAILED("A0401", "未登录"),

    AUTH_FORBIDDEN("A0403", "账号冻结"),
    INSUFFICIENT_BALANCE("A0501", "余额不足，请充值"),
    SYSTEM_EXECUTION_ERROR("B0001", "系统执行出错");

    private String code;
    private String msg;

    public String getCode() {
        return this.code;
    }

    public String getMsg() {
        return this.msg;
    }

    public String toString() {
        return "{\"code\":\"" + this.code + '"' + ", \"msg\":\"" + this.msg + '"' + '}';
    }

    public static ResultCode getValue(String code) {
        ResultCode[] var1 = values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            ResultCode value = var1[var3];
            if (value.getCode().equals(code)) {
                return value;
            }
        }

        return SYSTEM_EXECUTION_ERROR;
    }

    private ResultCode(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    private ResultCode() {
    }
}
