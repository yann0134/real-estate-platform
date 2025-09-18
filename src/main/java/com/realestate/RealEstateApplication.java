package com.realestate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class RealEstateApplication {
    public static void main(String[] args) {
        SpringApplication.run(RealEstateApplication.class, args);
    }
}