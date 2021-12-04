package com.hodur.remote.registry.na;

import com.hodur.remote.registry.ServiceRegistry;
import com.hodur.remote.registry.na.util.NacosUtil;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

/**
 * @author Hodur
 * @className NaServiceRegistry.java
 * @description
 * @date 2021年04月18日 19:24
 */
@Slf4j
public class NaServiceRegistry implements ServiceRegistry {

    @Override
    public void registerService(String rpcServiceName, InetSocketAddress inetSocketAddress) {
        System.out.println("=================");
        System.out.println(rpcServiceName);
        NacosUtil.registerService(rpcServiceName, inetSocketAddress);
        log.info("na服务注册成功:"+rpcServiceName+" "+inetSocketAddress);
    }
}
