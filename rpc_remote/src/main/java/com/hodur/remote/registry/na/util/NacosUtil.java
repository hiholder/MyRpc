package com.hodur.remote.registry.na.util;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * @author Hodur
 * @className NacosUtil.java
 * @description
 * @date 2021年04月18日 19:30
 */
@Slf4j
public class NacosUtil {

    private static final NamingService namingService;
    private static final Set<String> serviceNames = new HashSet<>();
    private static InetSocketAddress address;
    private static final String Address = "localhost:8848";
    static {
        namingService = getNacosNamingService();
    }

    public static NamingService getNacosNamingService() {
        try {
            return NamingFactory.createNamingService(Address);
        } catch (NacosException e) {
            log.info("连接到Nacos上时发生错误");
            throw new RuntimeException("CANNOT CONNENT TO REGISTRY");
        }
    }

    public static void registerService(String serviceName, InetSocketAddress address) {
        try {
            namingService.registerInstance(serviceName, address.getHostName(), address.getPort());
        } catch (NacosException e) {
            e.printStackTrace();
        }
        NacosUtil.address = address;
        serviceNames.add(serviceName);
    }

    public static void unregisterService(String serviceName, InetSocketAddress address) throws NacosException {
        namingService.deregisterInstance(serviceName, address.getHostName(), address.getPort());
        NacosUtil.address = address;
        serviceNames.remove(serviceName);
    }

    public static List<Instance> getAllInstance(String serviceName) throws NacosException {
        return namingService.getAllInstances(serviceName);
    }

    public static void clearRegistry() {
        if(!serviceNames.isEmpty() && address != null) {
            String host = address.getHostName();
            int port = address.getPort();
            Iterator<String> iterator = serviceNames.iterator();
            while(iterator.hasNext()) {
                String serviceName = iterator.next();
                try {
                    namingService.deregisterInstance(serviceName, host, port);
                } catch (NacosException e) {
                    log.error("注销服务 {} 失败", serviceName, e);
                }
            }
        }
    }

}
