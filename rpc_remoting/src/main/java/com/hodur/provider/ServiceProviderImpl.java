package com.hodur.provider;

import com.hodur.URL;
import com.hodur.enums.RpcErrorMessageEnum;
import com.hodur.exception.RpcException;
import com.hodur.extension.ExtensionLoader;
import com.hodur.registry.ServiceRegistry;
import com.hodur.remoting.transport.netty.server.NettyRpcServer;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Hodur
 * @className ServiceProviderImpl.java
 * @description
 * @date 2021年04月14日 19:10
 */
@Slf4j
public class ServiceProviderImpl implements ServiceProvider{
    private final Map<String,Object> serviceMap;
    private final Set<String> registeredService;
    private final ServiceRegistry serviceRegistry;

    public ServiceProviderImpl() {
        serviceMap = new ConcurrentHashMap<>();
        registeredService = ConcurrentHashMap.newKeySet();
        serviceRegistry = ExtensionLoader.getExtensionLoader(ServiceRegistry.class).getExtension("na");
    }

    @Override
    public void addService(Object service, Class<?> serviceClass, URL URL) {
        String rpcServiceName = URL.toRpcServiceName();
        if (registeredService.contains(rpcServiceName)) {
            return;
        }
        registeredService.add(rpcServiceName);
        serviceMap.put(rpcServiceName,service);
        log.info("Add service: {} and interrfaces: {}",rpcServiceName,service.getClass().getInterfaces());
    }

    @Override
    public Object getService(URL URL) {
        Object service = serviceMap.get(RpcErrorMessageEnum.SERVICE_CAN_NOT_BE_FOUND);
        if (null == service) {
            throw new RpcException(RpcErrorMessageEnum.SERVICE_CAN_NOT_BE_FOUND);
        }
        return service;
    }

    @Override
    public void publishService(Object service, URL URL) {
        try {
            String host = InetAddress.getLocalHost().getHostAddress();
            Class<?> serviceRelatedInterface = service.getClass().getInterfaces()[0];
            String serviceName = serviceRelatedInterface.getCanonicalName();
            URL.setServiceName(serviceName);
            this.addService(service,serviceRelatedInterface, URL);
            serviceRegistry.registerService(URL.toRpcServiceName(),new InetSocketAddress(host, NettyRpcServer.PORT));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void publishService(Object service) {
        this.publishService(service, new URL("", ""));
    }
}
