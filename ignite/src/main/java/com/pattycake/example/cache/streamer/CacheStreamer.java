package com.pattycake.example.cache.streamer;

import com.pattycake.example.config.ApacheIgniteCacheConfiguration;
import com.pattycake.example.model.InputModel;
import java.util.stream.IntStream;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteDataStreamer;
import org.apache.ignite.binary.BinaryObject;
import org.apache.ignite.binary.BinaryObjectBuilder;
import org.apache.ignite.stream.StreamVisitor;
import org.springframework.stereotype.Component;

@Component
public class CacheStreamer {

  private Ignite ignite;

  public CacheStreamer(Ignite ignite) {
    this.ignite = ignite;
  }

  public void load() {
    try (IgniteDataStreamer<String, BinaryObject> streamer = ignite.dataStreamer(ApacheIgniteCacheConfiguration.CACHE_NAME)) {
      streamer.keepBinary(true);
      streamer.allowOverwrite(true);

      streamer.receiver(StreamVisitor.from((cache, e) -> {
        BinaryObject binaryObject = cache.get(e.getKey());

        if (e == null) {
          binaryObject = e.getValue();
        } else {
          binaryObject = binaryObject.toBuilder()
              .setField("value", e.getValue().field("value"))
              .build();
        }

        cache.put(e.getKey(), binaryObject);
      }));

      IntStream.range(0, 10_000_000) //
          .forEach(i -> {
            String key = Integer.toString(i);
            BinaryObject value = ignite.binary().builder(InputModel.class.getName())
                .setField("id", key)
                .setField("value", i)
                .build();
            streamer.addData(key, value);
          });
    }
  }
}
