package com.example.integrationtests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ContextConfiguration(classes = TestConfig.class)
public class OrderIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Value("${order.service.url}")
    private String orderServiceUrl;

    @Value("${product.service.url}")
    private String productServiceUrl;

    @Value("${user.service.url}")
    private String userServiceUrl;

    @BeforeEach
    public void setUp() {
        // Ensure that all services are running and ready to accept requests
        // This can be done by checking a health endpoint or simply making a request
        // For simplicity, we assume they are already running.
    }

    @Test
    public void testCreateOrderWithExistingUserAndProduct() {
        Long userId = 1L; // Assuming there's a user with ID 1 in UserService
        Long productId = 1L; // Assuming there's a product with ID 1 in ProductService
        Integer quantity = 5;

        // Create a user if it doesn't exist
        UserRequest userRequest = new UserRequest("john.doe", "securepassword", "USER");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<UserRequest> userRequestEntity = new HttpEntity<>(userRequest, headers);

        ResponseEntity<UserResponse> userResponse = restTemplate.postForEntity(userServiceUrl + "/api/user", userRequestEntity, UserResponse.class);
        assertEquals(200, userResponse.getStatusCodeValue(), "User creation should be successful.");
        assertNotNull(userResponse.getBody(), "User response body should not be null.");

        // Create a product if it doesn't exist
        ProductRequest productRequest = new ProductRequest("Sample Product", 100.0, 10);
        HttpEntity<ProductRequest> productRequestEntity = new HttpEntity<>(productRequest, headers);

        ResponseEntity<ProductResponse> productResponse = restTemplate.postForEntity(productServiceUrl + "/api/product", productRequestEntity, ProductResponse.class);
        assertEquals(200, productResponse.getStatusCodeValue(), "Product creation should be successful.");
        assertNotNull(productResponse.getBody(), "Product response body should not be null.");

        // Check if the user exists using UserService
        ResponseEntity<UserResponse> getUserResponse = restTemplate.getForEntity(userServiceUrl + "/api/user/{userId}", UserResponse.class, userId);
        assertEquals(200, getUserResponse.getStatusCodeValue(), "User should exist.");
        assertNotNull(getUserResponse.getBody(), "User response body should not be null.");

        // Check if the product exists using ProductService
        ResponseEntity<ProductResponse> getProductResponse = restTemplate.getForEntity(productServiceUrl + "/api/product/{productId}", ProductResponse.class, productId);
        assertEquals(200, getProductResponse.getStatusCodeValue(), "Product should exist.");
        assertNotNull(getProductResponse.getBody(), "Product response body should not be null.");

        // Create an order using OrderService
        OrderRequest orderRequest = new OrderRequest(userId, productId, quantity);
        HttpEntity<OrderRequest> orderRequestEntity = new HttpEntity<>(orderRequest, headers);

        ResponseEntity<OrderResponse> orderResponse = restTemplate.postForEntity(orderServiceUrl + "/api/order", orderRequestEntity, OrderResponse.class);
        assertEquals(200, orderResponse.getStatusCodeValue(), "Order creation should be successful.");
        assertNotNull(orderResponse.getBody(), "Order response body should not be null.");
        assertEquals(userId, orderResponse.getBody().getUserId(), "User ID in response should match request.");
        assertEquals(productId, orderResponse.getBody().getProductId(), "Product ID in response should match request.");
        assertEquals(quantity, orderResponse.getBody().getQuantity(), "Quantity in response should match request.");
    }

    // Helper classes for request and response

    static class UserRequest {
        private String username;
        private String password;
        private String role;

        public UserRequest(String username, String password, String role) {
            this.username = username;
            this.password = password;
            this.role = role;
        }

        // Getters and setters
        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }
    }

    static class UserResponse {
        private Long id;
        private String username;
        private String password;
        private String role;

        // Getters and setters
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }
    }

    static class ProductRequest {
        private String name;
        private double price;
        private int inventory;

        public ProductRequest(String name, double price, int inventory) {
            this.name = name;
            this.price = price;
            this.inventory = inventory;
        }

        // Getters and setters
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public double getPrice() {
            return price;
        }

        public void setPrice(double price) {
            this.price = price;
        }

        public int getInventory() {
            return inventory;
        }

        public void setInventory(int inventory) {
            this.inventory = inventory;
        }
    }

    static class ProductResponse {
        private Long id;
        private String name;
        private double price;
        private int inventory;

        // Getters and setters
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public double getPrice() {
            return price;
        }

        public void setPrice(double price) {
            this.price = price;
        }

        public int getInventory() {
            return inventory;
        }

        public void setInventory(int inventory) {
            this.inventory = inventory;
        }
    }

    static class OrderRequest {
        private Long userId;
        private Long productId;
        private int quantity;

        public OrderRequest(Long userId, Long productId, int quantity) {
            this.userId = userId;
            this.productId = productId;
            this.quantity = quantity;
        }

        // Getters and setters
        public Long getUserId() {
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }

        public Long getProductId() {
            return productId;
        }

        public void setProductId(Long productId) {
            this.productId = productId;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }
    }

    static class OrderResponse {
        private Long id;
        private Long userId;
        private Long productId;
        private int quantity;
        private OrderStatus status;

        // Getters and setters
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public Long getUserId() {
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }

        public Long getProductId() {
            return productId;
        }

        public void setProductId(Long productId) {
            this.productId = productId;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        public OrderStatus getStatus() {
            return status;
        }

        public void setStatus(OrderStatus status) {
            this.status = status;
        }
    }
}



