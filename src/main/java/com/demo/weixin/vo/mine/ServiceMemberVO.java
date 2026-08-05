package com.demo.weixin.vo.mine;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 服务对象添加/编辑入参
 * memberId 为空时新增，非空时更新。
 */
@Data
@Schema(description = "服务对象添加/编辑入参")
public class ServiceMemberVO {

    @Schema(description = "服务对象ID（为空时新增，非空时编辑）")
    private Long memberId;

    @Schema(description = "姓名")
    @NotBlank(message = "姓名不能为空")
    private String name;

    @Schema(description = "手机号")
    @NotBlank(message = "手机号不能为空")
    private String phone;

    @Schema(description = "地址")
    private String address;

    @Schema(description = "详细门牌号")
    private String detailAddress;

    @Schema(description = "备注")
    private String remark;
}
