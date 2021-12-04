package com.hodur.remote.loadbalance;

import com.hodur.common.extension.SPI;

import java.util.List;

@SPI
public interface LoadBalance {
    String selectServiceAddress(List<String> serviceAddress,String rpcServiceName);

}
