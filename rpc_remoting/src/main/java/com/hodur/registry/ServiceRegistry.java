package com.hodur.registry;

import com.hodur.extension.SPI;

import java.net.InetSocketAddress;

/**
 * @author Hodur
 * @className ServiceRegistry.java
 * @description
 * @date 2021年04月13日 21:59
 */
@SPI
public interface ServiceRegistry {
    void registerService(String rpcServiceName, InetSocketAddress inetSocketAddress);
}
