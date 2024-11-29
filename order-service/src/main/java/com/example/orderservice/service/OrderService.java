package com.example.orderservice.service;

import com.example.orderservice.model.Order;
import com.example.orderservice.model.OrderStatus;
import com.example.orderservice.exception.SubServiceException;
import com.example.orderservice.repository.OrderRepository;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private RestTemplate restTemplate;

    @CircuitBreaker(name = "userService", fallbackMethod = "fallbackValidateUser")
    private void validateUser( Long userId ){
        restTemplate.getForEntity("http://user-service/api/user/{userId}",
                    String.class, userId);
    }

    public void fallbackValidateUser( Long userId, Throwable t) {
        throw new RuntimeException("Fallback user validation for user ID: " + userId + ". Error: " + t.getMessage());
    }

    @CircuitBreaker(name = "productService", fallbackMethod = "fallbackValidateProduct")
    private void validateProduct( Long productId ){
        restTemplate
                    .getForEntity("http://product-service/api/product/{productId}", String.class, productId);
    }

    public void fallbackValidateProduct( Long productId, Throwable t) {
        throw new RuntimeException("Fallback product validation for product ID: " + productId + ". Error: " + t.getMessage());
    }

    @Transactional
    public Order createOrder(Order order) {
        try {
            // Validate user ID
            validateUser( order.getUserId());
        } catch (HttpClientErrorException ex) {
            handleNotFoundException(ex, "User", order.getUserId());
        }
        try {
            // Validate product ID
            validateProduct( order.getProductId());
        } catch (HttpClientErrorException ex) {
            System.out.println( "Reduce Inventory httpclienterrorexception:" + ex );
            handleNotFoundException(ex, "Product", order.getProductId());
        }
        try {
            reduceInventory(order);
        } catch (HttpClientErrorException ex) {
            throw new SubServiceException( "Insufficient inventory for product ID: " + order.getProductId(), ex.getStatusCode());
        }
        
        order.setStatus(OrderStatus.PENDING);
        return orderRepository.save(order);
    }

    @CircuitBreaker(name = "productService", fallbackMethod = "fallbackReduceInventory")
    private void reduceInventory(Order order) {
        ResponseEntity<String> response = restTemplate.postForEntity(
                "http://product-service/api/product/{productId}/reduceInventory",
                order.getQuantity(),
                String.class,
                order.getProductId()
        );
    }

    public Order fallbackReduceInventory(Order order, Throwable t) {
       throw new RuntimeException("Fallback reduce inventory for product ID: " + order.getProductId() + ". Error: " + t.getMessage());
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(
                        () -> new SubServiceException("Order not found with id " + id, HttpStatus.NOT_FOUND));
    }

    @Transactional
    public Order updateOrderStatus(Long orderId, String newStatus) {
        Order order = getOrderById(orderId);
        OrderStatus status;
        try{
            status = OrderStatus.valueOf(newStatus);
        }
        catch( IllegalArgumentException e ){
            e.printStackTrace();
            throw new SubServiceException("Invalid order status: " + newStatus, HttpStatus.BAD_REQUEST);
        }

        if( order.getStatus()!=OrderStatus.CANCELLED && status == OrderStatus.CANCELLED ){
            increaseInventory(order);
        }

        order.setStatus(status);
        return orderRepository.save(order);
    }

    @CircuitBreaker(name = "productService", fallbackMethod = "fallbackIncreaseInventory")
    private void increaseInventory(Order order) {
        ResponseEntity<String> response = restTemplate.postForEntity(
                "http://product-service/api/product/{productId}/increaseInventory",
                order.getQuantity(),
                String.class,
                order.getProductId()
        );
    }

    public Order fallbackIncreaseInventory(Order order, Throwable t) {
        throw new RuntimeException("Fallback increase inventory for product ID: " + order.getProductId() + ". Error: " + t.getMessage());
     }

    private void handleNotFoundException( HttpClientErrorException ex, String objectName, Long id ){
        if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
            throw new SubServiceException( objectName + " not found with id " + id, ex.getStatusCode());
        } else {
            throw new SubServiceException("Unexpected client error: " + ex.getResponseBodyAsString(),
                    ex.getStatusCode());
        }
    }
    
    public boolean checkProductExists(Long productId) {
        return orderRepository.existsByProductId(productId);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }
}