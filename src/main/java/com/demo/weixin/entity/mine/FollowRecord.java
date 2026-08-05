package com.demo.weixin.entity.mine;

import com.demo.weixin.entity.Base;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * 关注记录实体
 * 记录用户间的关注关系，关注成功后给被关注者增加积分。
 */
@Data
@NoArgsConstructor
@Document(collection = "followRecord")
// 唯一复合索引：targetUserId + followerUserId + del_flag，防止重复关注（并发由唯一索引兜底）
@CompoundIndex(name = "idx_follow_user_target", def = "{'targetUserId': 1, 'followerUserId': 1, 'del_flag': 1}", unique = true)
@Schema(description = "关注记录")
public class FollowRecord extends Base {

    /** 关注记录业务主键 */
    @Field
    private Long followId;

    /** 被关注者用户ID（被关注的人） */
    @Field
    @Schema(description = "被关注者用户ID")
    private Long targetUserId;

    /** 关注者用户ID（执行关注操作的人） */
    @Field
    @Schema(description = "关注者用户ID")
    private Long followerUserId;

    /** 关注者姓名（冗余，避免用户信息变更影响历史记录） */
    @Field
    @Schema(description = "关注者姓名")
    private String followerName;

    /** 关注者电话（冗余） */
    @Field
    @Schema(description = "关注者电话")
    private String followerPhone;

    /** 关注者头像URL（冗余） */
    @Field
    @Schema(description = "关注者头像")
    private String followerAvatar;

    @Override
    public Long getID() {
        return followId;
    }

    @Override
    public void setID(Long id) {
        this.followId = id;
    }
}
