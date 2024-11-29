package com.example.orderservice;

import static org.assertj.core.api.Assertions.assertThat;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

import io.restassured.RestAssured;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties.StubsMode;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import io.restassured.response.Response;

@AutoConfigureStubRunner(
    ids = {"com.example:product-service:+:stubs:8084"},
    stubsMode = StubsMode.LOCAL
)
@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration(classes = { OrderServiceApplication.class })
public class ProductClientTest {

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        RestAssuredMockMvc.mockMvc(mockMvc);
    }

    @Test
    public void testReduceProductInventory() throws Exception {
        // given:
        int productId = 2;
        String requestBody = "3";

        // when:
        Response response = RestAssured.given()
            .header("Content-Type", "application/json;charset=UTF-8")
            .body(requestBody)
            .when()
            .post("/api/product/" + productId + "/reduceInventory");

        // then:
        assertThat(response.getStatusCode()).isEqualTo(200);
        assertThat(response.getBody().asString()).isEqualTo("Inventory reduced successfully");
    }

    @Test
    public void testIncreaseProductInventory() throws Exception {
        // given:
        int productId = 2;
        String requestBody = "5";

        // when:
        Response response = RestAssured.given()
            .header("Content-Type", "application/json;charset=UTF-8")
            .body(requestBody)
            .when()
            .post("/api/product/" + productId + "/increaseInventory");

        // then:
        assertThat(response.getStatusCode()).isEqualTo(200);
        assertThat(response.getBody().asString()).isEqualTo("Inventory increased successfully");
    }

    @Test
    void testGetProductById() throws Exception {
        // when:
        Response response = RestAssured.given()
            .header("Content-Type", "application/json")
            .when()
            .get("/api/product/1");

        // then:
        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.header("Content-Type")).matches("application/json.*");
        // and:
        DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
        assertThat(parsedJson.read("$.id", Integer.class)).isEqualTo(1);
        assertThat(parsedJson.read("$.name", String.class)).isEqualTo("Laptop");
        assertThat(parsedJson.read("$.price", Double.class)).isEqualTo(999.99);
        assertThat(parsedJson.read("$.inventory", Integer.class)).isNotNull();
    }
   
    @Test
    public void testAddProduct() throws Exception {
        // given:
        String requestBody = "{\"name\":\"productName\",\"price\":10.99,\"inventory\":10}";

        // when:
        Response response = RestAssured.given()
            .header("Content-Type", "application/json")
            .body(requestBody)
            .when()
            .post("/api/product");

        // then:
        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.header("Content-Type")).matches("application/json.*");

        // and:
        DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
         assertThat(parsedJson.read("$.id", Integer.class)).isNotNull();
        assertThat(parsedJson.read("$.name", String.class)).isEqualTo("productName");
        assertThat(parsedJson.read("$.price", Double.class)).isEqualTo(10.99);
        assertThat(parsedJson.read("$.inventory", Integer.class)).isEqualTo(10);
    }
}



