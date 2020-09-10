package com.wave.user.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("t_user_info")
public class UserInfoEntity {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private String nickName;

    /** 手机号 **/
    private String mobile;

    private String imageUrl;

    private Short status;

    private String email;
}
