package com.hodur.loadbalance;

import com.alibaba.nacos.api.naming.pojo.Instance;
import com.hodur.extension.SPI;

import java.util.List;
import java.util.Random;

@SPI
public interface LoadBalance {
    String selectServiceAddress(List<String> serviceAddress,String rpcServiceName);

}
