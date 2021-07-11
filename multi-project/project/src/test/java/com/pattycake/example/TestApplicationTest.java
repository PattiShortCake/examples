package com.pattycake.example;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class TestApplicationTest {

  @Autowired
  private TestApplication sut;

  @Test
  public void testRun() throws Exception {
    sut.run();

    throw new Exception();
  }
}
