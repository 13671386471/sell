package com.imooc.service.impl;

import com.imooc.Enums.OrderStatusEnum;
import com.imooc.Enums.PayStatuEnum;
import com.imooc.Enums.ResultEnum;
import com.imooc.converter.OrderMaster2OrderTOConverter;
import com.imooc.dataobject.OrderDetail;
import com.imooc.dataobject.OrderMaster;
import com.imooc.dataobject.ProductInfo;
import com.imooc.dto.CartDTO;
import com.imooc.dto.OrderDTO;
import com.imooc.exception.SellException;
import com.imooc.repository.OrderDetailRepository;
import com.imooc.repository.OrderMasterRepository;
import com.imooc.service.OrderService;
import com.imooc.service.ProductService;
import com.imooc.utils.KeyUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Liqiankun on 2019/6/14
 * param:
 */
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private ProductService productService;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private OrderMasterRepository orderMasterRepository;


    @Override
    @Transactional
    public OrderDTO create(OrderDTO orderDTO) {

        String orderId = KeyUtil.genUniqueKey();
        BigDecimal orderAmount = new BigDecimal(BigInteger.ONE.ZERO);
        System.out.println("orderAmount:::"+orderAmount);
        //前置步骤：
        //1、查询商品的数量和单价（数量用于判断库存还够不够，单价用于准确计算）
        for(OrderDetail orderDetail : orderDTO.getOrderDetailList()){

            //从数据库里查处商品的信息，商品的信息肯定是卖家录入进去的信息
            ProductInfo productInfo = productService.findOne(orderDetail.getProductId());
            if(productInfo == null){
                throw new SellException(ResultEnum.PRODUCT_NOT_EXIST);
            }

            //2、计算订单总价 商品的价格应该从商品表里的数据中获取（因为这里的数据是商家定义写入的）
            orderAmount = productInfo.getProductPrice()
                    .multiply(new BigDecimal(orderDetail.getPriductQuantity()))//一件商品的总价
                    .add(orderAmount);

            //订单详情入库
            orderDetail.setDetailId(KeyUtil.genUniqueKey());
            orderDetail.setOrderId(orderId);
            BeanUtils.copyProperties(productInfo, orderDetail);//把productInfo的属性拷贝到orderDetail
            orderDetailRepository.save(orderDetail);
        }

        //3、写入订单数据库（OrderMaster和OrderDetail）
        OrderMaster orderMaster = new OrderMaster();
        //上移快捷键：shift+option+上下键
        BeanUtils.copyProperties(orderDTO, orderMaster);//把orderDTO的属性拷贝到orderMaster；先拷贝，再进行某个字段设值
        System.out.println("orderMaster:"+ orderMaster);
        orderMaster.setOrderId(orderId);
        orderMaster.setOrderAmount(orderAmount);
        orderMaster.setOrderStatus(OrderStatusEnum.NEW.getCode());
        orderMaster.setPayStatu(PayStatuEnum.WAIT.getCode());
        orderMasterRepository.save(orderMaster);
        //4、下单成功后扣库存
        List<CartDTO> cartDTOList = orderDTO.getOrderDetailList().stream().map(e ->
            new CartDTO(e.getProductId(), e.getPriductQuantity())
        ).collect(Collectors.toList());
        productService.decreaseStock(cartDTOList);

        return orderDTO;
    }

    @Override
    public OrderDTO findOne(String orderId) {
        OrderMaster orderMaster = orderMasterRepository.findById(orderId).get();
        if(orderMaster == null){
            throw new SellException(ResultEnum.ORDER_NOT_EXIST);
        }
        List<OrderDetail> orderDetailList = orderDetailRepository.findByOrderId(orderId);
        if(CollectionUtils.isEmpty(orderDetailList)){
            throw new SellException(ResultEnum.ORDERDETAIL_NOT_EXIST);
        }
        OrderDTO orderDTO = new OrderDTO();
        BeanUtils.copyProperties(orderMaster, orderDTO);//数据需要转一下
        orderDTO.setOrderDetailList(orderDetailList);
        return orderDTO;
    }

    @Override
    public Page<OrderDTO> findList(String buyerOpenid, Pageable pageables) {
        //查订单列表；
        Page<OrderMaster> orderMasterPage = orderMasterRepository.findByBuyerOpenid(buyerOpenid, pageables);
        List<OrderDTO> orderDTOList = OrderMaster2OrderTOConverter.convert(orderMasterPage.getContent());
//        Page<OrderDTO> orderDTOPage = new PageImpl<OrderDTO>(orderDTOList, pageables, orderMasterPage.getTotalElements());
        return new PageImpl<OrderDTO>(orderDTOList, pageables, orderMasterPage.getTotalElements());
    }

    @Override
    public OrderDTO cancel(OrderDTO orderDTO) {
        return null;
    }

    @Override
    public OrderDTO finish(OrderDTO orderDTO) {
        return null;
    }

    @Override
    public OrderDTO paied(OrderDTO orderDTO) {
        return null;
    }
}
