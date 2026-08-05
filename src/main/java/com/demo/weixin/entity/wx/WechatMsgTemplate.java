package com.demo.weixin.entity.wx;

import com.demo.weixin.entity.Base;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

/**
 * 微信小程序服务通知通用订阅消息配置模板表
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Document(collection = "wechatMsgTemplate")
@Schema(description = "微信小程序服务通知通用订阅消息配置模板表")
public class WechatMsgTemplate extends Base {

    private static final long serialVersionUID = 1L;

    @Field
    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "微信小程序AppID")
    private String appId;

    @Indexed(unique = true)
    @Schema(description = "通知类型唯一业务标识 (如：QUEUE_CALLED (叫号提醒), BOOKING_RESULT (预约审核结果))")
    private String type;

    @Schema(description = "通知类型")
    private String typeName;

    @Schema(description = "微信官方审核通过的模板ID (Template_ID) ")
    private String modelId;

    @Schema(description = "默认跳转小程序的页面路径 (如: /opages/wifi/queue?merchantId=xxx)")
    private String jumpPage;

    @Schema(description = "模板内嵌动态字段配置列表，支持任意个(1~N)参数动态追加 ")
    private List<TemplateField> fields;

    @Schema(description = "启用状态")
    private Boolean enabled = true;

    @Schema(description = "备注")
    private String remark;

    @Data
    @NoArgsConstructor
    @Schema(description = "微信官方订阅消息单项字段定义描述")
    public static class TemplateField {

        @Schema(description = "微信官方指定的参数标识键，例如：character_string1, thing2")
        private String key;

        @Schema(description = "该参数在餐饮/等位系统中对应的展示标签名，例如：排号, 商家名")
        private String title;

        @Schema(description = "该参数值的默认填充/占位字符 (选填)")
        private String defaultValue;
    }

    @Override
    public Long getID() {
        return id;
    }

    @Override
    public void setID(Long id) {
        this.id = id;
    }
}