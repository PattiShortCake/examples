package com.example;

import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;
import org.apache.ignite.binary.BinaryObject;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.services.ServiceConfiguration;
import org.junit.jupiter.api.Test;
import sun.misc.Unsafe;

class BinaryObjectTest {

  @Test
  void binaryObject() {
    final IgniteConfiguration configuration = new IgniteConfiguration();
    try (final Ignite ignite = Ignition.start(configuration)) {

      final IgniteCache<String, MyEntry> cache = ignite.getOrCreateCache("test");

      final byte[] content = "hello".getBytes(StandardCharsets.UTF_8);
      final String key = "hihi";
      cache.put(key, new MyEntry(key, content));

      final IgniteCache<String, BinaryObject> withBinary = cache.withKeepBinary();
      final BinaryObject binaryObject = withBinary.get(key);

      final String keyDe = binaryObject.field("key");
      final byte[] value = binaryObject.field("content");

    }
  }

  @Test
  void binaryObject2() {
    final IgniteConfiguration configuration = new IgniteConfiguration();
    try (final Ignite ignite = Ignition.start(configuration)) {

      final IgniteCache<String, byte[]> cache = ignite.getOrCreateCache("test");

      final byte[] content = "hello".getBytes(StandardCharsets.UTF_8);
      final String key = "hihi";
      cache.put(key, content);

      final byte[] withBinaryContent = cache.get(key);
    }
  }

  @Test
  void igniteService() throws Exception {
    final IgniteConfiguration configuration = new IgniteConfiguration();
    try (final Ignite ignite = Ignition.start(configuration)) {

      final ServiceConfiguration serviceCfg = new ServiceConfiguration();

      serviceCfg.setName("myService");
      serviceCfg.setMaxPerNodeCount(1);
      serviceCfg.setTotalCount(1);
      serviceCfg.setService(new MyServiceImpl());

      ignite.services().deploy(serviceCfg);

      //access the service by name
      final MyService my = ignite.services().serviceProxy("myService",
          MyService.class, false); //non-sticky proxy

//call a service method
      try {
        my.sayHello();
      } catch (final Throwable e) {
        System.err.println("Yikes something bad happen. Reason: " + e.getMessage());
      }

      System.out.println("Now we wait");
      Thread.sleep(15_000L);
      System.out.println("Almost done.");
    }
  }

  @Test
  void testOOM() {
    try {
      final Field f = Unsafe.class.getDeclaredField("theUnsafe");
      f.setAccessible(true);
      final Unsafe unsafe = (Unsafe) f.get(null);
      unsafe.putAddress(0, 0);
    } catch (final Throwable t) {
      System.err.println("Woah.  Reason: " + t.getMessage());
    }

  }


  static class MyEntry {

    private final String key;
    private final byte[] content;

    public MyEntry(final String key, final byte[] content) {
      this.key = key;
      this.content = content;
    }

    public byte[] getContent() {
      return content;
    }

  }

}
