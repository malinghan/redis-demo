package com.mlh;

import com.ApplicationTest;
import org.junit.Test;
import org.springframework.data.redis.core.RedisTemplate;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import javax.annotation.Resource;

/**
 * @author : linghan.ma
 * @Package com.mlh
 * @Description:
 * @date Date : 2019年11月04日 3:41 PM
 **/
public class RedisPingTest extends ApplicationTest{


    @Resource(name = "redisTemplate")
    private RedisTemplate redisTemplate;


    @Test
    public  void testPing(){
        String pong  = redisTemplate.getRequiredConnectionFactory().getConnection().ping();
        System.out.println(pong);
        System.out.println("aaa");
    }


    @Test
    public  void testJedisPing(){
        JedisPool jedisPool = new JedisPool("r-2zewjkt12xxtjdwa1opd.redis.rds.aliyuncs.com",6379);
        Jedis jedis  = jedisPool.getResource();
        jedis.auth("yfs6TNA1");
        System.out.println(jedis.ping());
        System.out.println("aaa");
    }
}
