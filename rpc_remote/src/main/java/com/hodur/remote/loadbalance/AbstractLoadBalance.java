package com.hodur.remote.loadbalance;

import com.hodur.remote.loadbalance.LoadBalance;

import java.util.List;

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
