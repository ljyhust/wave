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

    private String userName;

    /** 手机号 **/
    private String mobileNo;

    private String imageUrl;

    private Short status;
}
