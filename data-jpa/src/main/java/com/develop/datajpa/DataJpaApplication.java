package com.develop.datajpa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {
    "com.develop.datajpa",
    "com.develop.core",
    "com.develop.websocket"
})
@EntityScan(basePackages = {"com.develop.domain"})
@EnableJpaRepositories(basePackages = {"com.develop.domain"})
public class DataJpaApplication {

    public static void main(String[] args) {
        SpringApplication.run(DataJpaApplication.class, args);
    }

}
