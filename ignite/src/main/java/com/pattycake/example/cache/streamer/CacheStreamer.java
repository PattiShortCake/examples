package com.pattycake.example.cache.streamer;

import com.pattycake.example.cache.loader.CacheLoader;
import com.pattycake.example.model.InputModel;
import java.util.stream.IntStream;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteDataStreamer;
import org.apache.ignite.binary.BinaryObject;
import org.apache.ignite.stream.StreamVisitor;
import org.springframework.stereotype.Component;

@Component
public class CacheStreamer {

  private final Ignite ignite;

  public CacheStreamer(final Ignite ignite) {
    this.ignite = ignite;
  }

  public void load() {
    try (final IgniteDataStreamer<String, BinaryObject> streamer = ignite.dataStreamer(
        CacheLoader.CACHE_NAME)) {
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
            final String key = Integer.toString(i);
            final BinaryObject value = ignite.binary().builder(InputModel.class.getName())
                .setField("id", key)
                .setField("value", i)
                .build();
            streamer.addData(key, value);
          });
    }
  }
}
