package com.cvenhance.ai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication(scanBasePackages = "com.cvenhance")
@org.springframework.data.jpa.repository.config.EnableJpaRepositories(basePackages = "com.cvenhance")
@org.springframework.boot.autoconfigure.domain.EntityScan(basePackages = "com.cvenhance")
@EnableAsync
public class AiAgentServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AiAgentServiceApplication.class, args);
    }
}
