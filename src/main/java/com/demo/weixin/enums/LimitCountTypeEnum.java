package com.demo.weixin.enums;

import lombok.Getter;

@Getter
public enum LimitCountTypeEnum {

    //
    CAPTCHA_VALIDATE(0, "验证码验证次数"),
    CAPTCHA_SEND(1, "验证码发送次数"),
    LOGIN(2, "登录密码校验次数"),
    REG(3, "注册次数"),
    LOOK(4, "查询账号次数"),
    ;

    private final int key;

    private final String desc;

    LimitCountTypeEnum(int key) {
        this.key = key;
        this.desc = "";
    }

    LimitCountTypeEnum(int key, String desc) {
        this.key = key;
        this.desc = desc;
    }

}
