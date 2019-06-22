package com.imooc.Enums;

import lombok.Getter;

/**
 * Created by Liqiankun on 2019/6/15
 * param:
 */
@Getter
public enum ResultEnum {
    //command+shift+u：小写变大写
    PRODUCT_NOT_EXIST(10, "商品不存在"),
    PRODUCT_NOT_ENOUGH(11, "商品库存不正确"),
    ORDER_NOT_EXIST(12, "订单不存在"),
    ORDERDETAIL_NOT_EXIST(13, "订单详情不存在"),
    ;

    private Integer code;
    private String msg;

    ResultEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
