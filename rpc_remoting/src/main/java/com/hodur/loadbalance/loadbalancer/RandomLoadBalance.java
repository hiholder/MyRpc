package com.hodur.loadbalance.loadbalancer;

import com.hodur.loadbalance.AbstractLoadBalance;

import java.util.List;
import java.util.Random;

/**
 * @author Hodur
 * @className RandomLoadBalance.java
 * @description
 * @date 2021年04月15日 10:13
 */
public class RandomLoadBalance extends AbstractLoadBalance {
    @Override
    protected String doSelect(List<String> serviceAddresses, String rpcServiceName) {
        Random random = new Random();
        return serviceAddresses.get(random.nextInt(serviceAddresses.size()));
    }
}
