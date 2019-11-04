package com.mlh.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * @author : linghan.ma
 * @Package com.mlh.config
 * @Description:
 * @date Date : 2019年11月04日 3:20 PM
 **/
@Configuration
public class RedisConfig {

    @Value("${spring.redis.host}")
    private String host;
    @Value("${spring.redis.port}")
    private int port;
    @Value("${spring.redis.password}")
    private String password;
    @Value("${spring.redis.database}")
    private int db;


    /**
     * 序列化方式
     * @return
     */
    @Bean("stringRedisSerializer")
    public StringRedisSerializer genStringRedisSerializer() {
        return new StringRedisSerializer();
    }


    /**
     * 单机模式-主从模式 使用的配置
     * @return
     */
    @Bean("redisConfiguration")
    public RedisStandaloneConfiguration redisStandaloneConfiguration() {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setDatabase(db);
        redisStandaloneConfiguration.setHostName(host);
        redisStandaloneConfiguration.setPort(port);
        redisStandaloneConfiguration.setPassword(password);
        return redisStandaloneConfiguration;
    }


    /**
     * 连接池
     * @return
     */
    @Bean(name = "redisConnectionFactory")
    public JedisConnectionFactory jedisConnectionFactoryTransport(@Qualifier("redisConfiguration") RedisStandaloneConfiguration redisConfiguration) {
        JedisConnectionFactory  jedisConnectionFactory = new JedisConnectionFactory(redisConfiguration);
        return jedisConnectionFactory;
    }

    /**
     *
     * @param redisConnectionFactory
     * @param valueRedisSerializer
     * @return
     */
    @Bean(name = "redisTemplate")
    public StringRedisTemplate redisTemplate(@Qualifier("redisConnectionFactory") RedisConnectionFactory redisConnectionFactory,
                                                      @Qualifier("stringRedisSerializer") RedisSerializer<?> valueRedisSerializer) {
        StringRedisTemplate template = new StringRedisTemplate(redisConnectionFactory);
        template.setValueSerializer(valueRedisSerializer);
        return template;
    }



}
