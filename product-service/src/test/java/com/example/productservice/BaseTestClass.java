package com.example.productservice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import com.example.productservice.repository.ProductRepository;
import com.example.productservice.service.ProductService;

import io.restassured.module.mockmvc.RestAssuredMockMvc;

import static org.mockito.MockitoAnnotations.openMocks;

@SpringBootTest
@AutoConfigureMockMvc
//@WebMvcTest
@ExtendWith(MockitoExtension.class)
public abstract class BaseTestClass {

    @Autowired
    protected MockMvc mockMvc;
    


    @Mock
    protected ProductRepository productRepository;

    @InjectMocks
    protected ProductService productService;

    @BeforeEach
    public void setup() {
        openMocks(this);
        RestAssuredMockMvc.mockMvc(mockMvc);
    }
}

