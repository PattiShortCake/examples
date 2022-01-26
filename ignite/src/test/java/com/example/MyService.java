package com.example;

import org.apache.ignite.services.Service;

public interface MyService extends Service {

  void sayHello() throws Exception;
}
