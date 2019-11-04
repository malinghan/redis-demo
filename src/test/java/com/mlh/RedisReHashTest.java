package com.mlh;

import com.ApplicationTest;
import org.junit.Test;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @author : linghan.ma
 * @Package com.mlh
 * @Description:
 * @date Date : 2019年11月04日 4:15 PM
 **/
public class RedisReHashTest extends ApplicationTest {


    @Resource(name = "redisTemplate")
    private RedisTemplate redisTemplate;

    @Test
    public  void testReHash() throws Exception{
        int n = 20;
        //2的n次方
        int rehashThreshold = (int) Math.pow(2,n);
        int offset = 10;
        ValueOperations<String,String> valueOperations =  redisTemplate.opsForValue();
        for(int i=0;i<rehashThreshold+offset;i++){
            System.out.println(String.valueOf(i));
            valueOperations.set(String.valueOf(i),String.valueOf(i));
            //用于观察临界点内存的变化。
            if (i > rehashThreshold - offset) {
                TimeUnit.SECONDS.sleep(1);
            }
        }
    }
}
