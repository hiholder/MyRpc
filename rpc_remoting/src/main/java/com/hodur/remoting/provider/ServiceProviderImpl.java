package com.hodur.remoting.provider;

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
 * @date 2021年04月13日 20:04
 */
@Slf4j
public class ServiceProviderImpl implements ServiceProvider{
    private final Map<String,Object> serviceMap;//键值时服务名：接口名+版本+组名
    private final Set<String> registeredService;    //判断服务是否存在的集合
    private final ServiceRegistry serviceRegistry;

    public ServiceProviderImpl() {
        serviceMap = new ConcurrentHashMap<>();
        registeredService = ConcurrentHashMap.newKeySet();
        serviceRegistry = ExtensionLoader.getExtensionLoader(ServiceRegistry.class).getExtension("na");
    }

    @Override
    public void addService(Object service, Class<?> serviceClass, URL URL) {
        String rpcServiceName = URL.toRpcServiceName();
        if (registeredService.contains(rpcServiceName)) { //如果服务已经存在，直接返回
            return;
        }
        //否则将服务名放入集合，将服务名和服务放入map
        registeredService.add(rpcServiceName);
        serviceMap.put(rpcServiceName,service);
        log.info("Add service:{} and interfaces:{}",rpcServiceName,service.getClass().getInterfaces());
    }
    /**
     * @describe 获取服务
     * @author Hodur
     * @date 2021/7/21 16:13
     * @param URL 服务属性
     * @return java.lang.Object
     */
    @Override
    public Object getService(URL URL) {
        Object service = serviceMap.get(URL.toRpcServiceName());
        if (service==null) {
            throw new RpcException(RpcErrorMessageEnum.SERVICE_CAN_NOT_BE_FOUND);
        }
        return service;
    }
    /**
     * @describe 进行服务注册
     * @author Hodur
     * @date 2021/7/21 16:15
     * @param service
     * @param URL
     */
    @Override
    public void publishService(Object service, URL URL) {
        try {
            String host = InetAddress.getLocalHost().getHostAddress();
            Class<?> serviceRelateInterface = service.getClass().getInterfaces()[0];
            String serviceName = serviceRelateInterface.getCanonicalName();//返回更容易理解的类名，和getName类似
            URL.setServiceName(serviceName);
            this.addService(service, serviceRelateInterface, URL);
            serviceRegistry.registerService(URL.toRpcServiceName(), new InetSocketAddress(host, NettyRpcServer.PORT));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void publishService(Object service) {
        this.publishService(service, new URL("", ""));
    }
}
