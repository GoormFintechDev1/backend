package com.example.backend.controller;

import com.example.backend.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/redis")
public class RedisTestController {

    @Autowired
    private RedisService redisService;

    @PostMapping("/set")
    public String setKeyValue(@RequestParam String key, @RequestParam String value) {
        redisService.saveValue(key, value);
        return "Saved key-value pair to Redis: " + key + " - " + value;
    }

    @GetMapping("/get")
    public String getKeyValue(@RequestParam String key) {
        String value = redisService.getValue(key);
        return value != null ? "Retrieved from Redis: " + value : "Key not found";
    }

    @DeleteMapping("/delete")
    public String deleteKeyValue(@RequestParam String key) {
        redisService.deleteValue(key);
        return "Deleted key from Redis: " + key;
    }
}
