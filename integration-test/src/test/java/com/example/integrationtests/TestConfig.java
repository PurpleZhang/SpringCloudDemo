package com.example.integrationtests;

import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:application-test.properties")
public class TestConfig {
    @Bean
    public TestRestTemplate restTemplate() {
        return new TestRestTemplate();
    }
}



