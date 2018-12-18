package com.mlh.demo;

/**
 * @author: linghan.ma
 * @DATE: 2018/12/17
 * @description:
 */
import java.util.List;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

public class TransactionDemo {

    public static void main(String[] args) {
        Jedis jedis = new Jedis(); // jedis 命令
        String userId = "abc"; //
        String key = format(userId);
        jedis.setnx(key, String.valueOf(5));  // setnx做初始化
        System.out.println(doubleAccount(jedis, userId));
        jedis.close();
    }

    /**
     * watch 实现分布式事务
     * @param jedis
     * @param userId
     * @return
     */
    public static int doubleAccount(Jedis jedis, String userId) {
        String key = format(userId);
        while (true) {
            jedis.watch(key);
            int value = Integer.parseInt(jedis.get(key));
            value *= 2; // 加倍
            Transaction tx = jedis.multi();
            tx.set(key, String.valueOf(value));
            List<Object> res = tx.exec();
            if (res != null) {
                break; // 成功了
            }
        }
        return Integer.parseInt(jedis.get(key)); // 重新获取余额
    }

    public static String format(String userId) {
        return String.format("account_%s", userId);
    }

}