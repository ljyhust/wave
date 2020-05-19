package com.wave.base.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("dict")
public class DictEntity implements Serializable{
    private static final long serialVersionUID = -3271817901703012317L;

    @TableId(type = IdType.AUTO)
    private Integer id;

    private String dictCode;

    private String dictName;

    private String creatorId;

    private String operatorId;

    private Date createTs;

    private Date updateTs;

    private Short status;
}
