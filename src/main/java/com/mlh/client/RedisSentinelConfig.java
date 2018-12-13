package com.mlh.client;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

/**
 * @author: linghan.ma
 * @DATE: 2018/11/26
 * @description:
 */
//@Configuration
public class RedisSentinelConfig {
    /**
     * Jedis
     * @return
     */
    @Bean
    public RedisConnectionFactory redisConnectionFactory(){
        //client
        RedisSentinelConfiguration sentinelConfiguration = new RedisSentinelConfiguration()
                .master("mymaster")
                .sentinel("127.0.0.1",26666)
                .sentinel("127.0.0.1",264545);
        return new JedisConnectionFactory(sentinelConfiguration);
    }


    /**
     *RedisSentinelConfiguration can also be defined via PropertySource.
     * Configuration Properties
     * spring.redis.sentinel.master: name of the master node.
     * spring.redis.sentinel.nodes: Comma delimited list of host:port pairs.
     */
//    @Bean
//    public RedisConnectionFactory redisConnectionFactory(){
//        //client
//        RedisSentinelConfiguration sentinelConfiguration = new RedisSentinelConfiguration()
//                .master("mymaster")
//                .sentinel("127.0.0.1",26666)
//                .sentinel("127.0.0.1",264545);
//        return new LettuceConnectionFactory(sentinelConfiguration);
//    }

}
