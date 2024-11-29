package com.example.productservice.service;

import com.example.productservice.model.Product;
import com.example.productservice.repository.ProductRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceIntegrationTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

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
        when(productRepository.findAll()).thenReturn(Arrays.asList(product1, product2));
        List<Product> products = productService.getAllProducts();
        assertEquals(2, products.size());
        assertTrue(products.contains(product1));
        assertTrue(products.contains(product2));
    }

    @Test
    public void testGetProductByIdFound() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));
        Optional<Product> product = productService.getProductById(1L);
        assertTrue(product.isPresent());
        assertEquals(product1, product.get());
    }

    @Test
    public void testGetProductByIdNotFound() {
        when(productRepository.findById(999L)).thenReturn(Optional.empty());
        Optional<Product> product = productService.getProductById(999L);
        assertFalse(product.isPresent());
    }

    @Test
    public void testAddProduct() {
        when(productRepository.save(any(Product.class))).thenReturn(product1);
        Product savedProduct = productService.addProduct(product1);
        assertNotNull(savedProduct);
        assertEquals(product1.getId(), savedProduct.getId());
    }

    @Test
    public void testUpdateProductFound() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));
        when(productRepository.save(any(Product.class))).thenReturn(product1);
        Product updatedProduct = productService.updateProduct(1L, product1);
        assertNotNull(updatedProduct);
        assertEquals(product1.getId(), updatedProduct.getId());
    }

    @Test
    public void testUpdateProductNotFound() {
        when(productRepository.findById(999L)).thenReturn(Optional.empty());
        Product updatedProduct = productService.updateProduct(999L, product1);
        assertNull(updatedProduct);
    }

    @Test
    public void testDeleteProduct() {
        doNothing().when(productRepository).deleteById(1L);
        productService.deleteProduct(1L);
        verify(productRepository, times(1)).deleteById(1L);
    }

    @Test
    public void testReduceInventorySuccess() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));
        productService.reduceInventory(1L, 5);
        verify(productRepository, times(1)).save(product1);
        assertEquals(5, product1.getInventory());
    }

    @Test
    public void testReduceInventoryInsufficientQuantity() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            when(productRepository.findById(1L)).thenReturn(Optional.of(product1));
            productService.reduceInventory(1L, 15);
        });
        assertEquals("Insufficient inventory for product ID: 1", exception.getMessage());
    }

    @Test
    public void testIncreaseInventory() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));
        productService.increaseInventory(1L, 5);
        verify(productRepository, times(1)).save(product1);
        assertEquals(15, product1.getInventory());
    }
}



