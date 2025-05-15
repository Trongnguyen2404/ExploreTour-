package com.vivu.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {

    @Bean
    public RestTemplate restTemplate() {
        // Bạn có thể cấu hình thêm cho RestTemplate ở đây nếu cần
        // (ví dụ: timeout, interceptor,...)
        return new RestTemplate();
    }
}