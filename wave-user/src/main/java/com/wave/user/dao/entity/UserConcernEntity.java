package com.wave.user.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("t_user_concern")
public class UserConcernEntity {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Long userId;

    /**
     * 我的关注
     */
    private Long concernUserId;

    private Short status;
}
