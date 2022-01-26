package com.example;

import java.lang.reflect.Field;
import org.apache.ignite.services.ServiceContext;
import sun.misc.Unsafe;

public class MyServiceImpl implements MyService {

  @Override
  public void cancel(final ServiceContext ctx) {

  }

  @Override
  public void init(final ServiceContext ctx) throws Exception {

  }

  @Override
  public void execute(final ServiceContext ctx) throws Exception {
    for (int i = 0; i < 100; i++) {
      System.out.println("HelloExecute-" + i);
      Thread.sleep(1_000L);
    }
  }

  @Override
  public void sayHello() throws NoSuchFieldException, IllegalAccessException, InterruptedException {
    for (int i = 10; i > 0; i--) {
      System.out.println("Will crash in " + i);
      Thread.sleep(1_000L);
    }

    final Field f = Unsafe.class.getDeclaredField("theUnsafe");
    f.setAccessible(true);
    final Unsafe unsafe = (Unsafe) f.get(null);
    unsafe.putAddress(0, 0);
  }

}
