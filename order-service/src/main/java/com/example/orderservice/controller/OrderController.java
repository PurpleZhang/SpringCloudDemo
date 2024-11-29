package com.example.orderservice.controller;

import com.example.orderservice.dto.ErrorResponse;
import com.example.orderservice.exception.SubServiceException;
import com.example.orderservice.model.Order;
import com.example.orderservice.service.OrderService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody Order order) {
        try {
            Order createdOrder = orderService.createOrder(order);
            return ResponseEntity.ok(createdOrder);
        } catch (SubServiceException ex) {
            return handleException(ex);
        }
    }

    public String fallbackCreateOrder(String id, Throwable t) {
        return "Fallback order for ID: " + id + ". Error: " + t.getMessage();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderById(@PathVariable Long id) {
        try {
            Order order = orderService.getOrderById(id);
            return ResponseEntity.ok(order);
        } catch (SubServiceException ex) {
            return handleException(ex);
        }
    }

    @GetMapping("/check-product")
    public boolean checkProductExists(@RequestParam("productId") Long productId) {
        return orderService.checkProductExists(productId);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateOrderStatus(@PathVariable Long id, @RequestBody String status) {
        try {
            Order updatedOrder = orderService.updateOrderStatus(id, status);
            return ResponseEntity.ok(updatedOrder);
        } catch (SubServiceException ex) {
            return handleException(ex);
        }
    }

    @GetMapping("/")
    public List<Order> getAllOrders() {
        return orderService.getAllOrders();
    }

    private ResponseEntity<ErrorResponse> handleException(SubServiceException ex) {
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), ex.getStatus().value());
        return new ResponseEntity<>(errorResponse, ex.getStatus());
    }
} 