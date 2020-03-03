package com.example.dependency;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application implements CommandLineRunner {

  private final DependencyExecutorService service;

  public Application(final DependencyExecutorService service) {
    this.service = service;
  }

  public static void main(final String[] args) {
    SpringApplication.run(Application.class, args);
  }

  @Override
  public void run(final String... args) throws Exception {
    service.run();
  }
}
