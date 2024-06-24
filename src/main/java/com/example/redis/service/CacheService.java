package com.example.redis.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.util.Set;

@Service
@Slf4j
public class CacheService implements MessageListener {

    @Autowired
    private RedisTemplate<String, Integer> redisTemplate;

    @Autowired
    private RedissonClient redisson;

    @Autowired
    private Jedis jedis;

    @Autowired
    private ChannelTopic channelTopic;

    public void doCacheLockAndChange(String key) {
        RLock lock = redisson.getLock("cacheMap");
        try {
            if (lock.tryLock()) {
                // change cache Redis
            }
        } finally {
            lock.unlock();
        }
    }


    public Long publishToRedisTemplate(String key, Integer value){
        log.info("Sending message Sync: " + key + " " + value);
        return redisTemplate.convertAndSend(channelTopic.getTopic(), value);
    }

    public void publishToTopicRedisson(String key, Integer value) {
        RTopic topic = redisson.getTopic("channel-events");
        topic.publish(value);
    }


    public void addToCacheRedisTemplate(String key, Integer value) {
        redisTemplate.opsForValue().set(key, value);
    }

    public Integer getFromCacheRedisTemplate(String key) {
        return redisTemplate.opsForValue().get(key);
    }


    public void deleteFromCacheRedisTemplate(String key){
        redisTemplate.delete(key);
    }
    public Boolean isInCacheRedisTemplate(String key) {
        return redisTemplate.hasKey(key);
    }

    public void clearCacheRedisTemplate() {
        Set<String> keys = redisTemplate.keys("*");
        if (keys != null)
            redisTemplate.delete(keys);
    }

    public void printCacheRedisTemplate(){
        Set<String> keys = redisTemplate.keys("*");
        for (String key : keys) {
            Integer value = getFromCacheRedisTemplate(key);
            System.out.println(key + " " + value);
        }
    }

    public void addToCacheRedisson(String key, Integer value) {
        RMap<String, Integer> cacheMap = redisson.getMap("cacheMap"); //topicName
        cacheMap.put(key, value);
    }

    public void deleteFromCacheRedisson(String key){
        RMap<String, Integer> cacheMap = redisson.getMap("cacheMap");
        cacheMap.remove(key);
    }

    public Integer getFromCacheRedisson(String key) {
        RMap<String, Integer> cacheMap = redisson.getMap("cacheMap");
        return cacheMap.get(key);
    }

    public void printCacheRedisson(){
        RKeys keys = redisson.getKeys();
        Iterable<String> allKeys = keys.getKeys();
        for (String key : allKeys) {
            Integer value = getFromCacheRedisTemplate(key);
            System.out.println(key + " " + value);
        }
    }

    public void clearCacheRedisson(){
        RMap<String, Integer> cacheMap = redisson.getMap("cacheMap");
        cacheMap.clear();
    }

    public void addToCacheJedis(String key, Integer value) {
        jedis.set(key, String.valueOf(value));
    }

    public void deleteFromCacheJedis(String key){
        jedis.del(key);
    }

    public Integer getFromCacheJedis(String key) {
        String value = jedis.get(key);
        return value != null ? Integer.parseInt(value) : null;
    }

    public void printCacheJedis(){
        for (String key : jedis.keys("*")) {
            System.out.println(key + ": " + jedis.get(key));
        }
    }

    public void clearCacheJedis(){
        jedis.flushAll();
    }


    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            log.info("New message received: {}", message);
            ObjectMapper objectMapper = new ObjectMapper();
            String key = objectMapper.readValue(message.getChannel(), String.class);
            Integer value = objectMapper.readValue(message.getBody(), Integer.class);
            redisTemplate.opsForValue().set(key, value);
        } catch (IOException e) {
            log.error("error while parsing message");
        }
    }
}
