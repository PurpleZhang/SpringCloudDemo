package com.example.orderservice.service;

import com.example.orderservice.exception.SubServiceException;
import com.example.orderservice.model.Order;
import com.example.orderservice.model.OrderStatus;
import com.example.orderservice.repository.OrderRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private OrderService orderService;

    private Order order1;
    private Order order2;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        order1 = new Order();
        order1.setId(1L);
        order1.setUserId(101L);
        order1.setProductId(201L);
        order1.setQuantity(5);
        order1.setStatus(OrderStatus.PENDING);

        order2 = new Order();
        order2.setId(2L);
        order2.setUserId(102L);
        order2.setProductId(202L);
        order2.setQuantity(3);
        order2.setStatus(OrderStatus.COMPLETED);
    }

    @Test
    public void testCreateOrderSuccess() throws Exception {
        when(restTemplate.getForEntity(eq("http://user-service/api/user/{userId}"), eq(String.class), eq(101L)))
                .thenReturn(ResponseEntity.ok("User exists"));
        when(restTemplate.getForEntity(eq("http://product-service/api/product/{productId}"), eq(String.class), eq(201L)))
                .thenReturn(ResponseEntity.ok("Product exists"));
        when(restTemplate.postForEntity(eq("http://product-service/api/product/{productId}/reduceInventory"), eq(5),
                eq(String.class), eq(201L)))
                .thenReturn(ResponseEntity.ok("Inventory reduced"));

        when(orderRepository.save(any(Order.class))).thenReturn(order1);

        Order createdOrder = orderService.createOrder(order1);

        assertNotNull(createdOrder);
        assertEquals(order1.getId(), createdOrder.getId());
        assertEquals(OrderStatus.PENDING, createdOrder.getStatus());

        verify(restTemplate, times(1)).getForEntity(eq("http://user-service/api/user/{userId}"), eq(String.class), eq(101L));
        verify(restTemplate, times(1)).getForEntity(eq("http://product-service/api/product/{productId}"), eq(String.class), eq(201L));
        verify(restTemplate, times(1)).postForEntity(eq("http://product-service/api/product/{productId}/reduceInventory"), eq(5),
                eq(String.class), eq(201L));
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    public void testCreateOrderUserNotFound() throws Exception {
        HttpClientErrorException ex = new HttpClientErrorException(HttpStatus.NOT_FOUND, "User not found");

        when(restTemplate.getForEntity(eq("http://user-service/api/user/{userId}"), eq(String.class), eq(101L)))
                .thenThrow(ex);

        assertThrows(SubServiceException.class, () -> orderService.createOrder(order1));

        verify(restTemplate, times(1)).getForEntity(eq("http://user-service/api/user/{userId}"), eq(String.class), eq(101L));
        verify(restTemplate, never()).getForEntity(eq("http://product-service/api/product/{productId}"), eq(String.class), eq(201L));
        verify(restTemplate, never()).postForEntity(eq("http://product-service/api/product/{productId}/reduceInventory"), eq(5),
                eq(String.class), eq(201L));
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    public void testCreateOrderProductNotFound() throws Exception {
        when(restTemplate.getForEntity(eq("http://user-service/api/user/{userId}"), eq(String.class), eq(101L)))
                .thenReturn(ResponseEntity.ok("User exists"));
        HttpClientErrorException ex = new HttpClientErrorException(HttpStatus.NOT_FOUND, "Product not found");

        when(restTemplate.getForEntity(eq("http://product-service/api/product/{productId}"), eq(String.class), eq(201L)))
                .thenThrow(ex);

        assertThrows(SubServiceException.class, () -> orderService.createOrder(order1));

        verify(restTemplate, times(1)).getForEntity(eq("http://user-service/api/user/{userId}"), eq(String.class), eq(101L));
        verify(restTemplate, times(1)).getForEntity(eq("http://product-service/api/product/{productId}"), eq(String.class), eq(201L));
        verify(restTemplate, never()).postForEntity(eq("http://product-service/api/product/{productId}/reduceInventory"), eq(5),
                eq(String.class), eq(201L));
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    public void testCreateOrderInsufficientInventory() throws Exception {
        when(restTemplate.getForEntity(eq("http://user-service/api/user/{userId}"), eq(String.class), eq(101L)))
                .thenReturn(ResponseEntity.ok("User exists"));
        when(restTemplate.getForEntity(eq("http://product-service/api/product/{productId}"), eq(String.class), eq(201L)))
                .thenReturn(ResponseEntity.ok("Product exists"));
        HttpClientErrorException ex = new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Insufficient inventory");

        when(restTemplate.postForEntity(eq("http://product-service/api/product/{productId}/reduceInventory"), eq(5),
                eq(String.class), eq(201L)))
                .thenThrow(ex);

        assertThrows(SubServiceException.class, () -> orderService.createOrder(order1));

        verify(restTemplate, times(1)).getForEntity(eq("http://user-service/api/user/{userId}"), eq(String.class), eq(101L));
        verify(restTemplate, times(1)).getForEntity(eq("http://product-service/api/product/{productId}"), eq(String.class), eq(201L));
        verify(restTemplate, times(1)).postForEntity(eq("http://product-service/api/product/{productId}/reduceInventory"), eq(5),
                eq(String.class), eq(201L));
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    public void testGetOrderByIdFound() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order1));

        Order foundOrder = orderService.getOrderById(1L);

        assertNotNull(foundOrder);
        assertEquals(order1.getId(), foundOrder.getId());
        assertEquals(order1.getUserId(), foundOrder.getUserId());

        verify(orderRepository, times(1)).findById(1L);
    }

    @Test
    public void testGetOrderByIdNotFound() {
        when(orderRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(SubServiceException.class, () -> orderService.getOrderById(999L));

        verify(orderRepository, times(1)).findById(999L);
    }

    @Test
    public void testUpdateOrderStatusToCompleted() throws Exception {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order1));
        when(orderRepository.save(any(Order.class))).thenReturn(order1);

        Order updatedOrder = orderService.updateOrderStatus(1L, "COMPLETED");

        assertNotNull(updatedOrder);
        assertEquals(OrderStatus.COMPLETED, updatedOrder.getStatus());

        verify(orderRepository, times(1)).findById(1L);
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(restTemplate, never()).postForEntity(eq("http://product-service/api/product/{productId}/increaseInventory"),
                any(), eq(String.class), eq(201L));
    }

    @Test
    public void testUpdateOrderStatusToCancelled() throws Exception {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order1));
        when(orderRepository.save(any(Order.class))).thenReturn(order1);
        when(restTemplate.postForEntity(eq("http://product-service/api/product/{productId}/increaseInventory"),
                eq(5), eq(String.class), eq(201L)))
                .thenReturn(ResponseEntity.ok("Inventory increased"));

        Order updatedOrder = orderService.updateOrderStatus(1L, "CANCELLED");

        assertNotNull(updatedOrder);
        assertEquals(OrderStatus.CANCELLED, updatedOrder.getStatus());

        verify(orderRepository, times(1)).findById(1L);
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(restTemplate, times(1)).postForEntity(eq("http://product-service/api/product/{productId}/increaseInventory"),
                eq(5), eq(String.class), eq(201L));
    }

    @Test
    public void testUpdateOrderStatusInvalidStatus() throws Exception {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order1));

        assertThrows(SubServiceException.class, () -> orderService.updateOrderStatus(1L, "INVALID_STATUS"));

        verify(orderRepository, times(1)).findById(1L);
        verify(orderRepository, never()).save(any(Order.class));
        verify(restTemplate, never()).postForEntity(eq("http://product-service/api/product/{productId}/increaseInventory"),
                any(), eq(String.class), eq(201L));
    }

    @Test
    public void testCheckProductExistsTrue() {
        when(orderRepository.existsByProductId(201L)).thenReturn(true);

        boolean productExists = orderService.checkProductExists(201L);

        assertTrue(productExists);

        verify(orderRepository, times(1)).existsByProductId(201L);
    }

    @Test
    public void testCheckProductExistsFalse() {
        when(orderRepository.existsByProductId(999L)).thenReturn(false);

        boolean productExists = orderService.checkProductExists(999L);

        assertFalse(productExists);

        verify(orderRepository, times(1)).existsByProductId(999L);
    }
}



