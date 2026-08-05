package com.demo.weixin.enums;

import lombok.Getter;
import org.springframework.util.StringUtils;

@Getter
public enum PlatformTypeEnum {

    //
    WX(1, "wx", "wxClaimRecords"),
    ALIPAY(2, "alipay", "alipayClaimRecords"),
    ;

    private final int key;

    private final String desc;
    private final String claimRecordCollectionName;

    PlatformTypeEnum(int key, String desc, String claimRecordCollectionName) {
        this.key = key;
        this.desc = desc;
        this.claimRecordCollectionName = claimRecordCollectionName;
    }

    public static int getKeyByDesc(String desc) {
        if(!StringUtils.hasText(desc)){
            return -1;
        }
        for (PlatformTypeEnum typeEnum : PlatformTypeEnum.values()) {
            if(typeEnum.desc.equals(desc)){
                return typeEnum.key;
            }
        }
        return -1;
    }

    public static PlatformTypeEnum getEnumByDesc(String desc) {
        if(!StringUtils.hasText(desc)){
            return null;
        }
        for (PlatformTypeEnum typeEnum : PlatformTypeEnum.values()) {
            if(typeEnum.desc.equals(desc)){
                return typeEnum;
            }
        }
        return null;
    }

}
