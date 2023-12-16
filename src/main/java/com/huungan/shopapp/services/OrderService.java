package com.huungan.shopapp.services;

import com.huungan.shopapp.dtos.OrderDTO;
import com.huungan.shopapp.exceptions.DataNotFoundException;
import com.huungan.shopapp.models.Order;
import com.huungan.shopapp.models.OrderStatus;
import com.huungan.shopapp.models.User;
import com.huungan.shopapp.repositories.OrderRepository;
import com.huungan.shopapp.repositories.UserRepository;
import com.huungan.shopapp.responses.orders.OrderResponse;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService implements IOrderService{
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public OrderResponse createOrder(OrderDTO orderDTO) throws Exception {
        User user = userRepository.findById(orderDTO.getUserId())
                .orElseThrow(() -> new DataNotFoundException(
                        "Cannot find the user with id: " + orderDTO.getUserId()));
        modelMapper.typeMap(OrderDTO.class, Order.class)
                .addMappings(mapper -> mapper.skip(Order::setId));
        Order order = new Order();
        modelMapper.map(orderDTO, order);
        order.setUser(user);
        order.setOrderDate(new Date());
        order.setStatus(OrderStatus.PENDING);
        LocalDate shippingDate = orderDTO.getShippingDate() == null ? LocalDate.now() : orderDTO.getShippingDate();
        if(shippingDate.isBefore(LocalDate.now())) {
            throw new DataNotFoundException("Shipping date must be after order date");
        }
        order.setActive(true);
        order.setShippingDate(shippingDate);
        orderRepository.save(order);
        modelMapper.typeMap(Order.class, OrderResponse.class);
        OrderResponse orderResponse = new OrderResponse();
        modelMapper.map(order, orderResponse);
        return orderResponse;
    }

    @Override
    public Order getOrder(Long id) throws Exception {
        return orderRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Cannot find the orders with id = " + id));
    }

    @Override
    @Transactional
    public Order updateOrder(Long id, OrderDTO orderDTO) throws Exception {
        Order existingOrder = orderRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Cannot find the order with id: " + id));
        User exitsingUser = userRepository.findById(orderDTO.getUserId())
                .orElseThrow(() -> new DataNotFoundException("Cannot find user with id: " + orderDTO.getUserId()));
        modelMapper.typeMap(OrderDTO.class, Order.class)
                .addMappings(mapper -> mapper.skip(Order::setId));
        modelMapper.map(orderDTO, existingOrder);
        existingOrder.setUser(exitsingUser);
        orderRepository.save(existingOrder);
        return existingOrder;
    }

    @Override
    @Transactional
    public void deleteOrder(Long id) {
        Order existingOrder = orderRepository.findById(id).orElse(null);
        if(existingOrder != null) {
            existingOrder.setActive(false);
            orderRepository.save(existingOrder);
        }
    }

    @Override
    public List<Order> findByUserId(Long userId) {
        return orderRepository.findByUserId(userId);
    }
}
