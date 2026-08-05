package com.demo.weixin.entity.mine;

import com.demo.weixin.entity.Base;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

/**
 * 打卡记录实体
 * 记录用户在各相框点位的打卡信息，用于个人中心打卡记录展示。
 */
@Data
@NoArgsConstructor
@Document(collection = "checkinRecord")
@Schema(description = "打卡记录")
public class CheckinRecord extends Base {

    /** 打卡记录业务主键 */
    @Field
    private Long recordId;

    /** 用户ID（打卡人） */
    @Field
    @Indexed
    @Schema(description = "用户ID")
    private Long userId;

    /** 相框编号 */
    @Field
    @Schema(description = "相框编号")
    private String frameNo;

    /** 相框名称 */
    @Field
    @Schema(description = "相框名称")
    private String frameName;

    /** 相框图片URL */
    @Field
    @Schema(description = "相框图片")
    private String frameImage;

    /** 打卡位置（地点描述） */
    @Field
    @Schema(description = "打卡位置")
    private String location;

    /** 打卡时间 */
    @Field
    @Schema(description = "打卡时间")
    private Date checkinTime;

    /** 打卡状态：1成功（预留扩展） */
    @Field
    @Schema(description = "打卡状态：1成功")
    private Integer status;

    @Override
    public Long getID() {
        return recordId;
    }

    @Override
    public void setID(Long id) {
        this.recordId = id;
    }
}
