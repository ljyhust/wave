package com.wave.user.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("t_account")
public class AccountEntity {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private String account;

    private Short type;

    private Long userId;

    private Short status;
}
