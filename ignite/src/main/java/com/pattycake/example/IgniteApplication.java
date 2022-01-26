package com.pattycake.example;

import com.pattycake.example.service.IgniteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@Slf4j
@RestController
public class IgniteApplication {

    private final IgniteService service;

    public IgniteApplication(final IgniteService service) {
        this.service = service;
    }

    public static void main(final String[] args) {
        SpringApplication.run(IgniteApplication.class, args);
    }

    @GetMapping
    public String get() {
        return "sum: " + service.printEven();
    }

}
