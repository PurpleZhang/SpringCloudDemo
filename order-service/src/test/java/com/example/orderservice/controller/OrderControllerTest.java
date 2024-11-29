package com.example.orderservice.controller;

import com.example.orderservice.dto.ErrorResponse;
import com.example.orderservice.exception.SubServiceException;
import com.example.orderservice.model.Order;
import com.example.orderservice.model.OrderStatus;
import com.example.orderservice.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderControllerTest {

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderController orderController;

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
        when(orderService.createOrder(any(Order.class))).thenReturn(order1);

        ResponseEntity<?> response = orderController.createOrder(order1);

        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(order1, response.getBody());

        verify(orderService, times(1)).createOrder(any(Order.class));
    }

    @Test
    public void testCreateOrderFailure() throws Exception {
        SubServiceException ex = new SubServiceException("Failed to create order", HttpStatus.BAD_REQUEST);
        when(orderService.createOrder(any(Order.class))).thenThrow(ex);

        ResponseEntity<?> response = orderController.createOrder(order1);

        assertNotNull(response.getBody());
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody() instanceof ErrorResponse);
        ErrorResponse errorResponse = (ErrorResponse) response.getBody();
        assertEquals("Failed to create order", errorResponse.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST.value(), errorResponse.getStatus());

        verify(orderService, times(1)).createOrder(any(Order.class));
    }

    @Test
    public void testGetOrderByIdFound() throws Exception {
        when(orderService.getOrderById(1L)).thenReturn(order1);

        ResponseEntity<?> response = orderController.getOrderById(1L);

        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(order1, response.getBody());

        verify(orderService, times(1)).getOrderById(1L);
    }

    @Test
    public void testGetOrderByIdNotFound() throws Exception {
        SubServiceException ex = new SubServiceException("Order not found with id 999", HttpStatus.NOT_FOUND);
        when(orderService.getOrderById(999L)).thenThrow(ex);

        ResponseEntity<?> response = orderController.getOrderById(999L);

        assertNotNull(response.getBody());
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody() instanceof ErrorResponse);
        ErrorResponse errorResponse = (ErrorResponse) response.getBody();
        assertEquals("Order not found with id 999", errorResponse.getMessage());
        assertEquals(HttpStatus.NOT_FOUND.value(), errorResponse.getStatus());

        verify(orderService, times(1)).getOrderById(999L);
    }

    @Test
    public void testCheckProductExistsTrue() {
        when(orderService.checkProductExists(201L)).thenReturn(true);

        boolean productExists = orderController.checkProductExists(201L);

        assertTrue(productExists);

        verify(orderService, times(1)).checkProductExists(201L);
    }

    @Test
    public void testCheckProductExistsFalse() {
        when(orderService.checkProductExists(999L)).thenReturn(false);

        boolean productExists = orderController.checkProductExists(999L);

        assertFalse(productExists);

        verify(orderService, times(1)).checkProductExists(999L);
    }

    @Test
    public void testUpdateOrderStatusToCompleted() throws Exception {
        when(orderService.updateOrderStatus(1L, "COMPLETED")).thenReturn(order1);

        ResponseEntity<?> response = orderController.updateOrderStatus(1L, "COMPLETED");

        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(order1, response.getBody());

        verify(orderService, times(1)).updateOrderStatus(1L, "COMPLETED");
    }

    @Test
    public void testUpdateOrderStatusInvalidStatus() throws Exception {
        SubServiceException ex = new SubServiceException("Invalid order status: INVALID_STATUS", HttpStatus.BAD_REQUEST);
        when(orderService.updateOrderStatus(1L, "INVALID_STATUS")).thenThrow(ex);

        ResponseEntity<?> response = orderController.updateOrderStatus(1L, "INVALID_STATUS");

        assertNotNull(response.getBody());
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody() instanceof ErrorResponse);
        ErrorResponse errorResponse = (ErrorResponse) response.getBody();
        assertEquals("Invalid order status: INVALID_STATUS", errorResponse.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST.value(), errorResponse.getStatus());

        verify(orderService, times(1)).updateOrderStatus(1L, "INVALID_STATUS");
    }
}



