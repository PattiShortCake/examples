package com.pattycake.example.javacpp;

import com.pattycake.example.javacpp.Greeter.GreeterClass;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class JavaCppApplication implements CommandLineRunner {

  public static void main(String[] args) {
    SpringApplication.run(JavaCppApplication.class, args);
  }

  @Override
  public void run(String... args) throws Exception {
    // Pointer objects allocated in Java get deallocated once they become unreachable,
    // but C++ destructors can still be called in a timely fashion with Pointer.deallocate()
    GreeterClass l = new GreeterClass();
    System.out.println(l.greeting());
  }
}
