package com.imooc.dto;

import lombok.Data;

/**
 * 购物车
 * Created by Liqiankun on 2019/6/16
 * param:
 */
@Data
public class CartDTO {
    /**商品Id*/
    private String productId;


    /**
     * 商品数量
     */
    private Integer productQuantity;


    public CartDTO(String productId, Integer productQuantity) {
        this.productId = productId;
        this.productQuantity = productQuantity;
    }
}
