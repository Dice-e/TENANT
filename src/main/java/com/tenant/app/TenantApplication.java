package com.tenant.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point of TENANT (Chapter 4.1).
 * @SpringBootApplication combines configuration, auto-configuration and
 * component scanning. Scanning only finds classes in com.tenant.app and below.
 */
@SpringBootApplication
public class TenantApplication {

    public static void main(String[] args) {
        SpringApplication.run(TenantApplication.class, args);
    }
}
