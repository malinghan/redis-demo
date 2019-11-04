package com.mlh.test;

import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @author : linghan.ma
 * @Package com.mlh.test
 * @Description:
 * @date Date : 2019年11月04日 3:03 PM
 **/
public class RedisReHashTest {

    @Resource(name = "redisTemplate")
    private RedisTemplate redisTemplate;


    public static void main(String[] args) throws Exception{
        RedisReHashTest redisReHashTest = new RedisReHashTest();
        redisReHashTest.testReHash(10);
    }

    public  void testReHash(int n) throws Exception{
            //2的n次方
            int rehashThreshold = (int) Math.pow(2,n);
            int offset = 10;
           ValueOperations<String,String> valueOperations =  redisTemplate.opsForValue();
            for(int i=0;i<rehashThreshold+offset;i++){
                valueOperations.set(String.valueOf(i),String.valueOf(i));
                //用于观察临界点内存的变化。
                if (i > rehashThreshold - offset) {
                    TimeUnit.SECONDS.sleep(1);
                }
            }
    }
}
