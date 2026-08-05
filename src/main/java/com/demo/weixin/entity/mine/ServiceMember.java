package com.demo.weixin.entity.mine;

import com.demo.weixin.entity.Base;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * 服务对象实体
 * 记录用户需要服务/看护的联系人信息，用于服务发布时快速选择。
 * 与紧急联系人（EmergencyContact）分离，因为服务对象需要地址信息且用途不同。
 */
@Data
@NoArgsConstructor
@Document(collection = "serviceMember")
@Schema(description = "服务对象")
public class ServiceMember extends Base {

    /** 服务对象业务主键 */
    @Field
    private Long memberId;

    /** 用户ID（所属用户） */
    @Indexed
    @Field
    @Schema(description = "用户ID")
    private Long userId;

    /** 姓名 */
    @Field
    @Schema(description = "姓名")
    private String name;

    /** 手机号 */
    @Field
    @Schema(description = "手机号")
    private String phone;

    /** 地址（地图选择的地址） */
    @Field
    @Schema(description = "地址")
    private String address;

    /** 详细门牌号 */
    @Field
    @Schema(description = "详细门牌号")
    private String detailAddress;

    /** 备注 */
    @Field
    @Schema(description = "备注")
    private String remark;

    @Override
    public Long getID() {
        return memberId;
    }

    @Override
    public void setID(Long id) {
        this.memberId = id;
    }
}
