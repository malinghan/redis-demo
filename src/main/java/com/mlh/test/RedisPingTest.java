package com.mlh.test;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;

/**
 * @author : linghan.ma
 * @Package com.mlh.test
 * @Description:
 * @date Date : 2019年11月04日 3:27 PM
 **/
public class RedisPingTest {

    @Autowired
    private RedisTemplate redisTemplate;


    @Test
    public  void testPing(){
        String pong  = redisTemplate.getRequiredConnectionFactory().getConnection().ping();
        System.out.println(pong);
        System.out.println("aaa");
    }
}
