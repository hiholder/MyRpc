package com.hodur.remote.registry;

import com.hodur.common.extension.SPI;

import java.net.InetSocketAddress;

@SPI
public interface ServiceDiscovery {
    /**
     * @describe
     * @author Hodur
     * @date 2021/4/14 19:02
 * @param rpcServiceName
 * @return java.net.InetSocketAddress
     */
    InetSocketAddress lookupService(String rpcServiceName);
}
