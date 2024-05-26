package com.huungan.shopapp.controller;

import com.huungan.shopapp.components.LocalizationUtils;
import com.huungan.shopapp.dtos.OrderDTO;
import com.huungan.shopapp.exceptions.DataNotFoundException;
import com.huungan.shopapp.models.Order;
import com.huungan.shopapp.responses.ResponseObject;
import com.huungan.shopapp.responses.orders.OrderListResponse;
import com.huungan.shopapp.responses.orders.OrderResponse;
import com.huungan.shopapp.services.IOrderService;
import com.huungan.shopapp.utils.MessageKeys;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("${api.prefix}/orders")
@RequiredArgsConstructor
public class OrderController {
    private final IOrderService orderService;
    private final LocalizationUtils localizationUtils;

    @PostMapping("")
    // POST http://localhost:8088/api/v1/orders
    public ResponseEntity<ResponseObject> createOrder(
            @Valid @RequestBody OrderDTO orderDTO,
            BindingResult result
    ) throws Exception {
        if(result.hasErrors()) {
            List<String> errorMessages = result.getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .toList();
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .message(String.join(";", errorMessages))
                    .build());
        }

        Order order = orderService.createOrder(orderDTO);
        OrderResponse orderResponse = OrderResponse.fromOrder(order);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .status(HttpStatus.OK)
                .message(localizationUtils.getLocalizedMessage(MessageKeys.CREATE_ORDER_SUCCESSFULLY))
                .data(orderResponse)
                .build());
    }

    @GetMapping("/user/{user_id}")
    // GET http://localhost:8088/api/v1/orders/{user_id}
    public ResponseEntity<ResponseObject> getOrders(@Valid @PathVariable("user_id") Long userId) {
        List<Order> orders = orderService.findByUserId(userId);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .status(HttpStatus.OK)
                .message(localizationUtils.getLocalizedMessage(MessageKeys.FIND_ORDER_BY_USER_ID_SUCCESSFULLY))
                .data(orders)
                .build());
//        try {
//            List<Order> orders = orderService.findByUserId(userId);
//            List<OrderResponse> orderResponses = orders.stream()
//                    .map(OrderResponse::fromOrder)
//                    .collect(Collectors.toList());
//            return ResponseEntity.ok(orderResponses);
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().body(e.getMessage());
//        }
    }

    @GetMapping("/{id}")
    // GET http://localhost:8088/api/v1/orders/{id}
    public ResponseEntity<ResponseObject> getOrder(@Valid @PathVariable("id") Long id) throws Exception {
        Order existingOrder = orderService.getOrder(id);
        OrderResponse orderResponse = OrderResponse.fromOrder(existingOrder);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .status(HttpStatus.OK)
                .message(localizationUtils.getLocalizedMessage(MessageKeys.FIND_ORDER_BY_ID_SUCCESSFULLY))
                .build());

//        try {
//            Order existingOrder = orderService.getOrder(id);
//            OrderResponse orderResponse = OrderResponse.fromOrder(existingOrder);
//            return ResponseEntity.ok(orderResponse);
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().body(e.getMessage());
//        }
    }

    @GetMapping("/get-orders-by-keyword")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ResponseObject> getOrdersByKeyword(
            @RequestParam(defaultValue = "",required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit
    ) {
        PageRequest pageRequest = PageRequest.of(page, limit, Sort.by("id").ascending());
        Page<OrderResponse> orderPage = orderService.getOrderByKeyword(keyword, pageRequest)
                .map(OrderResponse::fromOrder);
        List<OrderResponse> orderResponses = orderPage.getContent();
        return ResponseEntity.ok().body(ResponseObject.builder()
                .status(HttpStatus.OK)
                .data(orderResponses)
                .build());
    }

    @PutMapping("/{id}")
    // PUT http://localhost:8088/api/v1/orders/{id}
    // This method is intended for admin use only
    public ResponseEntity<ResponseObject> updateOrder(
            @Valid @PathVariable Long id,
            @Valid @RequestBody OrderDTO orderDTO
    ) throws Exception {
        Order order = orderService.updateOrder(id, orderDTO);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .status(HttpStatus.OK)
                .data(order)
                .build());
//        try {
//            Order order = orderService.updateOrder(id, orderDTO);
//            return ResponseEntity.ok(order);
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().body(e.getMessage());
//        }
    }

    @DeleteMapping("/{id}")
    // DELETE http://localhost:8088/api/v1/orders/{id}
    public ResponseEntity<ResponseObject> deleteOrder(@Valid @PathVariable Long id) {
        // soft delete => update field active = false
        orderService.deleteOrder(id);
        return ResponseEntity.ok(ResponseObject.builder()
                .message(String.format("Deleted order with id: %d", id))
                .build());
    }
}
