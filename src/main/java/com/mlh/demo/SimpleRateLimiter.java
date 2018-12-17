package com.mlh.demo;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;

import java.io.IOException;

/**
 * @author: linghan.ma
 * @DATE: 2018/12/13
 * @description: 简单限流 比如 60s内最多访问5次
 * 可以用于控制用户访问太频繁导致误操作或耗费大量无意义的资源
 */
public class SimpleRateLimiter {


    private Jedis jedis;

    public SimpleRateLimiter(Jedis jedis) {
        this.jedis = jedis;
    }

    public boolean isActionAllowed(String userId, String actionKey, int period, int maxCount) throws IOException {
        //hist:laoqian:reply
        //recruitment:xiaoxin:add-interview
        String key = String.format("hist:%s:%s", userId, actionKey);
        long nowTs = System.currentTimeMillis();
        Pipeline pipe = jedis.pipelined();
        pipe.multi();
        //添加key-time
        pipe.zadd(key, nowTs, "" + nowTs);
        //移除有序集: 60秒前之前的数据都移除
        pipe.zremrangeByScore(key, 0, nowTs - period * 1000);
        //返回有序集 key 的基数
        Response<Long> count = pipe.zcard(key);
        //设置key 60+1过去，访问次数
        pipe.expire(key, period + 1);
        pipe.exec();
        pipe.close();
        return count.get() <= maxCount;
    }

    public static void main(String[] args) throws IOException{
        Jedis jedis = new Jedis();
        SimpleRateLimiter limiter = new SimpleRateLimiter(jedis);
        for(int i=0;i<20;i++) {
            System.out.println(limiter.isActionAllowed("laoqian", "reply", 60, 5));
        }
    }
}
