package com.cvenhance.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = "com.cvenhance")
@org.springframework.data.jpa.repository.config.EnableJpaRepositories(basePackages = "com.cvenhance")
@org.springframework.boot.autoconfigure.domain.EntityScan(basePackages = "com.cvenhance")
@EnableAsync
@EnableScheduling
public class AuthServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthServiceApplication.class, args);
    }
}
