package com.cafe_shop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class CafeShopApplication {

    public static void main(String[] args) {
        SpringApplication.run(CafeShopApplication.class, args);
    }

}
