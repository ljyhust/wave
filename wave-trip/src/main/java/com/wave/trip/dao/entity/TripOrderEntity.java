package com.wave.trip.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("t_trip_order")
public class TripOrderEntity implements Serializable{
    private static final long serialVersionUID = 604598617831760352L;

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Long userId;

    private Long driverId;

    private Double sourceLongit;

    private Double sourceLatit;

    private Double destLongit;

    private Double destLatit;

    private String tripStartTime;

    private String tripEndTime;

    private Integer tripState;

    private String tripInfo;

    private Short status;
}
