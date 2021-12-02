package com.hodur.loadbalance;

import com.alibaba.nacos.api.naming.pojo.Instance;
import com.hodur.loadbalance.LoadBalance;

import java.util.List;
import java.util.Random;

/**
 * @author Hodur
 * @className AbstractLoadBalance.java
 * @description
 * @date 2021年04月15日 9:47
 */
public abstract class AbstractLoadBalance implements LoadBalance {
    protected abstract String doSelect(List<String> serviceAddress,String rpcServiceName);
    @Override
    public String selectServiceAddress(List<String> serviceAddress, String rpcServiceName) {
        if (serviceAddress == null || serviceAddress.size() ==0) {
            return null;
        }
        if (serviceAddress.size() == 1) {
            return serviceAddress.get(0);
        }
        return doSelect(serviceAddress,rpcServiceName);
    }

}
