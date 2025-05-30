package com.lb.pingme;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.lb.pingme.repository")
public class PingMeApplication {
    public static void main(String[] args) {
        SpringApplication.run(PingMeApplication.class, args);
    }
}
