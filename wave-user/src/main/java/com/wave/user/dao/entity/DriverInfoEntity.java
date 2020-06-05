package com.wave.user.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("t_driver_info")
public class DriverInfoEntity implements Serializable{
    private static final long serialVersionUID = -5580416249153578111L;

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private String userName;

    private String mobileNo;

    private String carNo;

    private String idNum;

    private String imageUrl;

    private String status;
}
