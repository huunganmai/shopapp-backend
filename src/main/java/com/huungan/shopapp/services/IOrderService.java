package com.huungan.shopapp.services;

import com.huungan.shopapp.dtos.OrderDTO;
import com.huungan.shopapp.models.Order;
import com.huungan.shopapp.responses.orders.OrderResponse;

import java.util.List;

public interface IOrderService {
    Order createOrder(OrderDTO orderDTO) throws Exception;

    Order getOrder(Long id) throws Exception;

    Order updateOrder(Long id, OrderDTO orderDTO) throws Exception;

    void deleteOrder(Long id);

    List<Order> findByUserId(Long userId);
}
