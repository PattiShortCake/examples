package com.pattycake.example;

import com.pattycake.example.service.IgniteService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@Slf4j
public class IgniteApplication implements CommandLineRunner{

private IgniteService service;

    public IgniteApplication(IgniteService service) {
        this.service = service;
    }

    public static void main(String[] args) {
        SpringApplication.run(IgniteApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        service.printEven();
    }

}
