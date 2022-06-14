package com.pattycake.example;

import com.pattycake.example.cache.loader.CacheLoader;
import com.pattycake.example.service.IgniteService;
import java.util.Map;
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
    private final CacheLoader cacheLoader;

    public IgniteApplication(final IgniteService service, final CacheLoader cacheLoader) {
        this.service = service;
        this.cacheLoader = cacheLoader;
    }

    public static void main(final String[] args) {
        SpringApplication.run(IgniteApplication.class, args);
    }

    @GetMapping
    public String get() {
        return "sum: " + service.printEven();
    }

    @GetMapping("printEven2")
    public String get2() {
        return "sum: " + service.printEven2();
    }

    @GetMapping("red")
    public String red() {
        service.runRedJob();
        return null;
    }

    @GetMapping("blue")
    public String blue() {
        service.runBlueJob();
        return null;
    }

    @GetMapping("print")
    public Map<String, Object> printAttributes() {
        return service.printAttributes();
    }

    @GetMapping("load")
    public void load() {
        cacheLoader.loadCache();
    }

}
