package com.wave.trip.service;

/**
 * 订单状态枚举类
 */
public enum TripOrderState {

    CREATE_JUST(0, "创建"),

    DISPATCH_OK(10, "匹配成功"),

    DISPATCH_ING(11, "匹配中"),

    TRIP_ING(12, "行程中"),

    TRIP_OVER(13, "行程结束"),

    PAY_ING(21, "支付中"),

    PAY_ERROR(22, "支付失败"),

    PAY_OK(20, "支付成功"),

    ORDER_OK(30, "订单结束"),

    DISCARD(50, "取消");

    /**
     * 状态 0- 创建， 10- 匹配中， 12-进行中，20-支付成功，13-结束并待支付，50-取消，9x-其它
     */
    private int state;

    /**
     * 描述
     */
    private String desc;

    private TripOrderState(int state, String desc) {
        this.state = state;
        this.desc = desc;
    }

    public int getState() {
        return state;
    }

    public String getDesc() {
        return desc;
    }
}
