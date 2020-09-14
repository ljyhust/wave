package com.wave.user.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("t_user_fancy")
public class UserFancyEntity {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Long userId;

    /**
     * 我的粉丝
     */
    private Long fancyUserId;

    private Short status;
}
