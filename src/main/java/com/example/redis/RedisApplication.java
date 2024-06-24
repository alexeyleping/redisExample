package com.example.redis;

import com.example.redis.service.ServiceData;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RKeys;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import static jodd.util.ThreadUtil.sleep;

@SpringBootApplication
@Slf4j
public class RedisApplication implements CommandLineRunner {

    private ServiceData serviceData;

    public RedisApplication(ServiceData serviceData) {
        this.serviceData = serviceData;
    }


    public static void main(String[] args) {
        SpringApplication.run(RedisApplication.class, args);
    }

    @Override
    public void run(String... args) {
        log.info("printCache()...");
        serviceData.printCache();

        log.info("dataLoad()...");
        serviceData.dataLoad();

        log.info("printCache()...");
        serviceData.printCache();

        log.info("clearCache()...");
        serviceData.clearCache();

        log.info("printCache()...");
        serviceData.printCache();

        System.exit(0);
    }
}
