//package com.mlh.test;
//
//import org.springframework.dao.DataAccessException;
//import org.springframework.data.redis.connection.RedisConnection;
//import org.springframework.data.redis.connection.StringRedisConnection;
//import org.springframework.data.redis.core.RedisCallback;
//import org.springframework.data.redis.core.RedisTemplate;
//
//import javax.annotation.Resource;
//import java.net.URL;
//
///**
// * @author: linghan.ma
// * @DATE: 2018/12/12
// * @description:
// */
//public class RedisTemplateTest {
//    @Resource(name="stringRedisTemplate")
//    private RedisTemplate stringRedisTemplate;
//
//    public  void addLink(String userId, URL url) {
//        stringRedisTemplate.opsForList().leftPush(userId, url.toExternalForm());
//    }
//
//    /**
//     * 直接对话redis 执行命令
//     */
//    public void useCallback() {
//        stringRedisTemplate.execute(new RedisCallback<Object>() {
//            public Object doInRedis(RedisConnection connection) throws DataAccessException {
//                Long size = connection.dbSize();
//                // Can cast to StringRedisConnection if using a StringRedisTemplate
//                ((StringRedisConnection) connection).set("key", "value");
//                return null;
//            }
//        });
//    }
//
//    public static void main(String[] args) throws Exception{
//        RedisTemplateTest redisTemplateTest = new RedisTemplateTest();
//        redisTemplateTest.addLink("12121",new URL("https://www.baidu.com"));
//    }
//}
