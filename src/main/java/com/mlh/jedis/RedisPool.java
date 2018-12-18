package com.mlh.jedis;
/**
 * @author: linghan.ma
 * @DATE: 2018/12/18
 * @description: 封装try-with-source
 */
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

interface CallWithJedis {
    public void call(Jedis jedis);
}

class RedisPool {

    private JedisPool pool;

    public RedisPool() {
        this.pool = new JedisPool();
    }

    public void execute(CallWithJedis caller) {
        try (Jedis jedis = pool.getResource()) {
            caller.call(jedis);
        }
    }
}
