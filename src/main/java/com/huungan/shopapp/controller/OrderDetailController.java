package com.huungan.shopapp.controller;

import com.huungan.shopapp.components.LocalizationUtils;
import com.huungan.shopapp.dtos.OrderDetailDTO;
import com.huungan.shopapp.models.OrderDetail;
import com.huungan.shopapp.responses.ResponseObject;
import com.huungan.shopapp.responses.orders.OrderDetailResponse;
import com.huungan.shopapp.services.IOrderDetailService;
import com.huungan.shopapp.utils.MessageKeys;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/order_details")
@RequiredArgsConstructor
public class OrderDetailController {
    private final IOrderDetailService orderDetailService;
    private final LocalizationUtils localizationUtils;

    @PostMapping("")
    public ResponseEntity<ResponseObject> createOrderDetail(
            @Valid @RequestBody OrderDetailDTO orderDetailDTO
    ) throws Exception {
        OrderDetail orderDetail = orderDetailService.createOrderDetail(orderDetailDTO);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .status(HttpStatus.OK)
                .data(OrderDetailResponse.fromOrderDetail(orderDetail)).build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseObject> getOrderDetail(
            @Valid @PathVariable("id") Long id
    ) throws Exception {
        OrderDetail orderDetail = orderDetailService.getOrderDetail(id);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .status(HttpStatus.OK)
                .data(OrderDetailResponse.fromOrderDetail(orderDetail))
                .build());
//        try {
//            OrderDetail orderDetail = orderDetailService.getOrderDetail(id);
//            return ResponseEntity.ok(OrderDetailResponse.fromOrderDetail(orderDetail));
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().body(e.getMessage());
//        }
    }

    @GetMapping("/order/{order_id}")
    // Get OrderDetail list of certain Order
    public ResponseEntity<ResponseObject> getOrderDetails(
            @Valid @PathVariable("order_id") Long orderId
    ) {
        List<OrderDetail> orderDetail = orderDetailService.findByOrderId(orderId);
        List<OrderDetailResponse> orderDetailResponses = orderDetail
                .stream()
                .map(OrderDetailResponse::fromOrderDetail)
                .toList();
        return ResponseEntity.ok().body(ResponseObject.builder()
                .status(HttpStatus.OK)
                .data(orderDetailResponses)
                .build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseObject> updateOrderDetail(
            @Valid @PathVariable("id") Long id,
            @RequestBody OrderDetailDTO OrderDetailDTO
    ) throws Exception {
        OrderDetail orderDetail = orderDetailService.updateOrderDetail(id, OrderDetailDTO);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .status(HttpStatus.OK)
                .data(orderDetail)
                .build());
//        try {
//            OrderDetail orderDetail = orderDetailService.updateOrderDetail(id, OrderDetailDTO);
//            return ResponseEntity.ok(orderDetail);
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().body(e.getMessage());
//        }
    }

    @DeleteMapping("/{id}")
        public ResponseEntity<ResponseObject> deleteOrderDetail(
            @Valid @PathVariable("id") Long id
    ) {
        orderDetailService.deleteById(id);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .status(HttpStatus.OK)
                .message(localizationUtils.getLocalizedMessage(MessageKeys.DELETE_ORDER_DETAIL_SUCCESSFULLY))
                .build());
//        try {
//            orderDetailService.deleteById(id);
//            return ResponseEntity.ok("Delete order with id = "+ id + " successfully");
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().body(e.getMessage());
//        }


    }
}
