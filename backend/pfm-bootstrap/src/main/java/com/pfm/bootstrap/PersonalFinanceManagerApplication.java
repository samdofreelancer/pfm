package com.pfm.bootstrap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = {
    "com.pfm.api",
    "com.pfm.application",
    "com.pfm.infrastructure",
    "com.pfm.domain"
})
@EntityScan(basePackages = {"com.pfm.infrastructure.persistence.jpa.entity"})
@EnableJpaRepositories(basePackages = {"com.pfm.infrastructure.persistence.jpa.repository"})
public class PersonalFinanceManagerApplication {

    public static void main(String[] args) {
        SpringApplication.run(PersonalFinanceManagerApplication.class, args);
    }
}