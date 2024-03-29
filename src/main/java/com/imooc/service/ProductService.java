package com.imooc.service;

import com.imooc.dataobject.ProductInfo;
import com.imooc.dto.CartDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


import java.util.List;

/**
 * 商品的服务层（service）
 * Created by Liqiankun on 2019/6/12
 * param:
 */
public interface ProductService {

    /**根据商品Id号查找商品*/
    ProductInfo findOne(String productId);

    /**查询所有在架商品列表*/
    List<ProductInfo> findUpAll();

    /**
     * 管理段按分页查询
     * */
    Page<ProductInfo> findAll(Pageable pageable);

    ProductInfo save(ProductInfo productInfo);

    //加库存
    void increaseStock(List<CartDTO> cartDTOList);

    //减库存
    void decreaseStock(List<CartDTO> cartDTOList);
}
