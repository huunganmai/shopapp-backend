package com.huungan.shopapp.services;

import com.huungan.shopapp.dtos.OrderDTO;
import com.huungan.shopapp.models.Order;
import com.huungan.shopapp.responses.orders.OrderResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IOrderService {
    Order createOrder(OrderDTO orderDTO) throws Exception;

    Order getOrder(Long id) throws Exception;

    Order updateOrder(Long id, OrderDTO orderDTO) throws Exception;

    void deleteOrder(Long id);

    List<Order> findByUserId(Long userId);

    Page<Order> getOrderByKeyword(String keyword, Pageable pageable);
}
