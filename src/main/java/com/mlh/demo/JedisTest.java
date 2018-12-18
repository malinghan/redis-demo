package com.mlh.demo;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;


/**
 * @author: linghan.ma
 * @DATE: 2018/12/18
 * @description:
 */

public class JedisTest {
    public static void main(String[] args) {
        JedisPool pool = new JedisPool();
         // 拿出 Jedis 链接对象
        // try-with-sources
        try (Jedis jedis = pool.getResource()){// 用完自动 close
            doSomething(jedis);
        }
    }

    private static void doSomething(Jedis jedis) {
        // code it here
        jedis.set("code","java");
        System.out.println("hh");
    }

}