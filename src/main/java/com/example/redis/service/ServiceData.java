package com.example.redis.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@Slf4j
public class ServiceData {
    private final CacheService cacheService;


    public ServiceData(CacheService cacheService) {
        this.cacheService = cacheService;
    }

    private static final Integer[] NUMBERS = {1, 2, 3, 4, 5, 6, 7, 8, 9, 0};
    private static final Random random = new Random();


    public void dataLoad() {
        Integer number;
        String baseKey = "randomNumber";
        for (int i = 0; i < 10; i++) {
            String key = baseKey + i;
            if (cacheService.isInCacheRedisTemplate(key)) {
                number = cacheService.getFromCacheRedisTemplate(key);
                log.info("dataLoad() (from cache) " + key + " " + number);
            } else {
                number = getRandomNumber(random);
                log.info("dataLoad() (generated) " + key + " " + number);
                cacheService.addToCacheRedisTemplate(key, number);
            }
        }
    }

    private static Integer getRandomNumber(Random random) {
        Integer index = random.nextInt(NUMBERS.length);
        return NUMBERS[index];
    }

    public void printCache() {
        cacheService.printCacheRedisson();
    }

    public void clearCache(){
        cacheService.clearCacheJedis();
    }

}
