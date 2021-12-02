package com.hodur.remoting.handler;

import com.hodur.exception.RpcException;
import com.hodur.factory.SingletonFactory;
import com.hodur.remoting.dto.RpcRequest;
import com.hodur.remoting.provider.ServiceProvider;
import com.hodur.remoting.provider.ServiceProviderImpl;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author Hodur
 * @className RpcRequestHandler.java
 * @description rpc请求的处理器，在获得动态代理生成的类后会负责具体方法的调用
 * @date 2021年04月13日 20:00
 */
@Slf4j
public class RpcRequestHandler {
    private final ServiceProvider serviceProvider;

    public RpcRequestHandler() {
        serviceProvider = SingletonFactory.getInstance(ServiceProviderImpl.class);
    }
    /**
     * @describe Processing rpcRequest:调用相应方法，并返回方法
     * @author Hodur
     * @date 2021/4/13 22:10
     * @param rpcRequest
     * @return java.lang.Object
     */
    public Object handle(RpcRequest rpcRequest) {
        Object service = serviceProvider.getService(rpcRequest.toRpcProperties());
        return invokeTargetMethod(rpcRequest,service);
    }
    /**
     * @describe 使用反射执行方法后，获得方法的执行结果
     * @author Hodur
     * @date 2021/4/13 22:12
     * @param rpcRequest 客户请求
     * @param service    服务器对象
     * @return java.lang.Object  目标方法的执行结果
     */
    private Object invokeTargetMethod(RpcRequest rpcRequest,Object service) {
        Object result;
        try {
            Method method = service.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getParamTypes());
            result = method.invoke(service, rpcRequest.getParameters());
            log.info("service:[{}] successful invoke method:[{}]", rpcRequest.getInterfaceName(), rpcRequest.getMethodName());
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RpcException(e.getMessage(), e);
        }
        return result;
    }
}
