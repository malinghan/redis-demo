package com.mlh.demo;

import redis.clients.jedis.Jedis;

/**
 * @author: linghan.ma
 * @DATE: 2018/12/13
 * @description:
 */
public class PfTest {
        public static void main(String[] args) {
            Jedis jedis = new Jedis();
            for (int i = 0; i < 100000; i++) {
                jedis.pfadd("codehole", "user" + i);
            }
            long total = jedis.pfcount("codehole");
            System.out.printf("%d %d\n", 100000, total);
            jedis.close();
        }
}