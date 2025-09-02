package com.hmall.cart.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class CartRestTemplateConfig {

    @Bean
    @LoadBalanced
    public RestTemplate CartRestTemplate() {
        return new RestTemplate();
    }
}