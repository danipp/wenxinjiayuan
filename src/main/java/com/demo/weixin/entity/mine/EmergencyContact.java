package com.demo.weixin.entity.mine;

import com.demo.weixin.entity.Base;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * 紧急联系人实体
 * 记录用户设置的紧急联系人信息，用于个人中心紧急联系人管理。
 */
@Data
@NoArgsConstructor
@Document(collection = "emergencyContact")
@Schema(description = "紧急联系人")
public class EmergencyContact extends Base {

    /** 联系人业务主键 */
    @Field
    private Long contactId;

    /** 用户ID（所属用户） */
    @Field
    @Indexed
    @Schema(description = "用户ID")
    private Long userId;

    /** 联系人姓名 */
    @Field
    @Schema(description = "联系人姓名")
    private String name;

    /** 联系人电话 */
    @Field
    @Schema(description = "联系人电话")
    private String phone;

    /** 与用户的关系（如：父母、子女、配偶、朋友等） */
    @Field
    @Schema(description = "关系")
    private String relation;

    @Override
    public Long getID() {
        return contactId;
    }

    @Override
    public void setID(Long id) {
        this.contactId = id;
    }
}
