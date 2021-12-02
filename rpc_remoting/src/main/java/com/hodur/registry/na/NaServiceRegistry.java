package com.hodur.registry.na;

import com.alibaba.nacos.api.exception.NacosException;
import com.hodur.enums.RpcErrorMessageEnum;
import com.hodur.exception.RpcException;
import com.hodur.registry.ServiceRegistry;
import com.hodur.registry.na.util.NacosUtil;
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
