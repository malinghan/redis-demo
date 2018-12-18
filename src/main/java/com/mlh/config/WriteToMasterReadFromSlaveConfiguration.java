package com.mlh.config;

import io.lettuce.core.ReadFrom;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

/**
 * @author: linghan.ma
 * @DATE: 2018/11/26
 * @description:
 */
//@Configuration
public class WriteToMasterReadFromSlaveConfiguration {

    /**
     * 主写从读redis(无故障切换)
     * Redis Master/Slave setup, without automatic failover
     * (for automatic failover see: Sentinel), 故障切换
     * not only allows data to be savely stored at more nodes.
     * It also allows, using Lettuce,
     * reading data from slaves while pushing writes to the master.
     * Set the read/write strategy to be used via LettuceClientConfiguration.
     * @return
     */
    @Bean
    public  LettuceConnectionFactory redisConnectionFactory(){
        //client
        LettuceClientConfiguration clientConfiguration = LettuceClientConfiguration.builder()
                .readFrom(ReadFrom.SLAVE_PREFERRED)
                .build();
        //server
        RedisStandaloneConfiguration serverConfiguration = new RedisStandaloneConfiguration("server",6379);
        return new LettuceConnectionFactory(serverConfiguration,clientConfiguration);
    }
}
