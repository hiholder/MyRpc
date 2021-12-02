package com.hodur.registry.zk;

import com.hodur.enums.RpcErrorMessageEnum;
import com.hodur.exception.RpcException;
import com.hodur.extension.ExtensionLoader;
import com.hodur.loadbalance.LoadBalance;
import com.hodur.registry.ServiceDiscovery;
import com.hodur.registry.zk.util.CuratorUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * @author Hodur
 * @className ZkServiceDiscovery.java
 * @description
 * @date 2021年04月15日 9:34
 */
@Slf4j
public class ZkServiceDiscovery implements ServiceDiscovery {
    private final LoadBalance loadBalance;

    public ZkServiceDiscovery() {
        this.loadBalance = ExtensionLoader.getExtensionLoader(LoadBalance.class).getExtension("loadBalance");
    }
    @Override
    public InetSocketAddress lookupService(String rpcServiceName) {
        CuratorFramework zkClient = CuratorUtils.getZkClient();
        List<String> serviceUrlList = CuratorUtils.getChildrenNodes(zkClient,rpcServiceName);
        if (serviceUrlList == null || serviceUrlList.size()==0) {
            throw new RpcException(RpcErrorMessageEnum.SERVICE_CAN_NOT_BE_FOUND,rpcServiceName);
        }
        //load balancing
        String targetServiceUrl = loadBalance.selectServiceAddress(serviceUrlList,rpcServiceName);
        log.info("Successfully found the service address:[{}]",targetServiceUrl);
        String[] socketAddressArray = targetServiceUrl.split(":");
        String host = socketAddressArray[0];
        int port = Integer.parseInt(socketAddressArray[1]);
        return new InetSocketAddress(host,port);
    }
}
