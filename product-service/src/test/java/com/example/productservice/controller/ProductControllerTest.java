package com.example.productservice.controller;

import com.example.productservice.client.OrderClient;
import com.example.productservice.model.ErrorResponse;
import com.example.productservice.model.Product;
import com.example.productservice.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductControllerTest {

    @Mock
    private ProductService productService;

    @Mock
    private OrderClient orderClient;

    @InjectMocks
    private ProductController productController;

    private Product product1;
    private Product product2;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        product1 = new Product();
        product1.setId(1L);
        product1.setName("Product1");
        product1.setPrice(10.0);
        product1.setInventory(10);

        product2 = new Product();
        product2.setId(2L);
        product2.setName("Product2");
        product2.setPrice(20.0);
        product2.setInventory(20);
    }

    @Test
    public void testGetAllProducts() {
        when(productService.getAllProducts()).thenReturn(Arrays.asList(product1, product2));
        List<Product> products = productController.getAllProducts();
        assertEquals(2, products.size());
        assertTrue(products.contains(product1));
        assertTrue(products.contains(product2));
    }

    @Test
    public void testGetProductByIdFound() {
        when(productService.getProductById(1L)).thenReturn(Optional.of(product1));
        ResponseEntity<Product> response = productController.getProductById(1L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(product1, response.getBody());
    }

    @Test
    public void testGetProductByIdNotFound() {
        when(productService.getProductById(999L)).thenReturn(Optional.empty());
        ResponseEntity<Product> response = productController.getProductById(999L);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    public void testAddProduct() {
        when(productService.addProduct(any(Product.class))).thenReturn(product1);
        Product savedProduct = productController.addProduct(product1);
        assertNotNull(savedProduct);
        assertEquals(product1.getId(), savedProduct.getId());
    }

    @Test
    public void testUpdateProductFound() {
        when(productService.updateProduct(eq(1L), any(Product.class))).thenReturn(product1);
        ResponseEntity<Product> response = productController.updateProduct(1L, product1);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(product1, response.getBody());
    }

    @Test
    public void testUpdateProductNotFound() {
        when(productService.updateProduct(eq(999L), any(Product.class))).thenReturn(null);
        ResponseEntity<Product> response = productController.updateProduct(999L, product1);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    public void testDeleteProductNoOrderAssociation() {
        when(orderClient.checkProductExists(1L)).thenReturn(false);
        doNothing().when(productService).deleteProduct(1L);
        ResponseEntity<?> response = productController.deleteProduct(1L);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(productService, times(1)).deleteProduct(1L);
    }

    @Test
    public void testDeleteProductWithOrderAssociation() {
        when(orderClient.checkProductExists(1L)).thenReturn(true);
        ResponseEntity<?> response = productController.deleteProduct(1L);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ErrorResponse errorResponse = (ErrorResponse) response.getBody();
        assertEquals("Product cannot be deleted as it is associated with an order", errorResponse.getMessage());
        verify(productService, never()).deleteProduct(1L);
    }

    @Test
    public void testReduceInventorySuccess() {
        doNothing().when(productService).reduceInventory(1L, 5);
        ResponseEntity<String> response = productController.reduceInventory(1L, 5);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Inventory reduced successfully", response.getBody());
    }

    @Test
    public void testReduceInventoryInsufficientQuantity() {
        doThrow(new IllegalArgumentException("Insufficient inventory for product ID: 1")).when(productService).reduceInventory(1L, 15);
        ResponseEntity<String> response = productController.reduceInventory(1L, 15);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Insufficient inventory for product ID: 1", response.getBody());
    }

    @Test
    public void testReduceInventoryRuntimeError() {
        doThrow(new RuntimeException("Unexpected error")).when(productService).reduceInventory(1L, 15);
        ResponseEntity<String> response = productController.reduceInventory(1L, 15);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Unexpected error", response.getBody());
    }

    @Test
    public void testIncreaseInventorySuccess() {
        doNothing().when(productService).increaseInventory(1L, 5);
        ResponseEntity<String> response = productController.increaseInventory(1L, 5);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Inventory increased successfully", response.getBody());
    }

    @Test
    public void testIncreaseInventoryRuntimeError() {
        doThrow(new RuntimeException("Unexpected error")).when(productService).increaseInventory(1L, 15);
        ResponseEntity<String> response = productController.increaseInventory(1L, 15);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Unexpected error", response.getBody());
    }
}



