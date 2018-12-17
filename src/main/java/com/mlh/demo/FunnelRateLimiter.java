package com.mlh.demo;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: linghan.ma
 * @DATE: 2018/12/13
 * @description: 漏斗限流
 *
 * 这个就是漏桶算法。
 * 漏桶和令牌桶的关键区别：计算剩余令牌（Token）或配额（Quota）。
 * 漏桶算法是令牌消耗的速度恒定，生成的速度可变。
 * 令牌桶算法是令牌生成的速度很定，消耗的速度可变。
 */
public class FunnelRateLimiter {
    //漏斗
    static class Funnel {
        //漏斗容量
        int capacity;
        //流水频率
        float leakingRate;
        //剩余空间
        int leftQuota;
        //上一次漏水time
        long leakingTs;

        public Funnel(int capacity,float leakingRate){
            this.capacity = capacity;
            this.leakingRate = leakingRate;
            this.leftQuota = capacity;
            this.leakingTs = System.currentTimeMillis();

        }

        void makeSpace(){
            long nowTs = System.currentTimeMillis();
            long deltaTs = nowTs - leakingTs; // 距离上一次漏水过去多久
            int deltaQuota = (int) (deltaTs * leakingRate); // 腾出的空间

            if(deltaQuota<0){
                this.leftQuota = capacity;
                this.leakingTs = nowTs;
                return;
            }

            if(deltaQuota<1){//没有空间
                return;
            }

            this.leftQuota += deltaQuota;
            this.leakingTs = nowTs;

            if(this.leftQuota > this.capacity) {
                this.leftQuota = this.capacity;
            }
        }

        //quota 配额
        boolean watering(int quota) {
            makeSpace();
            if (this.leftQuota >= quota) {//判断剩余空间是否足够
                this.leftQuota -= quota;
                return true;
            }
            return false;
        }
    }

    private Map<String, Funnel> funnels = new HashMap<>();

    public boolean isActionAllowed(String userId, String actionKey, int capacity, float leakingRate) {
        String key = String.format("%s:%s", userId, actionKey);
        Funnel funnel = funnels.get(key);
        if (funnel == null) {
            funnel = new Funnel(capacity, leakingRate);
            funnels.put(key, funnel);
        }
        return funnel.watering(1); // 需要1个quota
    }

}
