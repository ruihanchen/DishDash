package com.chendev.dishdash;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;


@SpringBootApplication
@EnableCaching
public class DishDashApplication {

    public static void main(String[] args) {
        SpringApplication.run(DishDashApplication.class, args);
    }
}