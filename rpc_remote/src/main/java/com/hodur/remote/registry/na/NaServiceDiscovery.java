package com.hodur.remote.registry.na;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.hodur.remote.registry.ServiceDiscovery;
import com.hodur.remote.registry.na.util.NacosUtil;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Random;

/**
 * @author Hodur
 * @className NaServiceDiscovery.java
 * @description
 * @date 2021年04月18日 19:24
 */
@Slf4j
public class NaServiceDiscovery implements ServiceDiscovery {

    /*private final LoadBalance loadBalance;
    public NaServiceDiscovery() {
        this.loadBalance = ExtensionLoader.getExtensionLoader(LoadBalance.class).getExtension("loadBalance");
    }*/
    @Override
    public InetSocketAddress lookupService(String rpcServiceName) {
        try {
            List<Instance> instances = NacosUtil.getAllInstance(rpcServiceName);
            System.out.println("==============================");
            System.out.println(rpcServiceName);
            //String targetServiceUrl = loadBalance.selectServiceAddress(Collections.singletonList(instances.toString()),rpcServiceName);
            Instance instance = instances.get(new Random().nextInt(instances.size()));//
            log.info("na服务发现成功:"+instance.getIp()+","+instance.getPort());
            return new InetSocketAddress(instance.getIp(), instance.getPort());
        } catch (NacosException e) {
            log.error("获取服务时有错误发生:", e);
        }
        return null;
    }
}
