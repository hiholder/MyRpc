package com.hodur.proxy;

import com.hodur.URL;
import com.hodur.enums.RpcErrorMessageEnum;
import com.hodur.enums.RpcResponseCodeEnum;
import com.hodur.exception.RpcException;
import com.hodur.remoting.dto.RpcRequest;
import com.hodur.remoting.dto.RpcResponse;
import com.hodur.remoting.transport.RpcRequestTransport;
import com.hodur.remoting.transport.netty.client.NettyRpcClient;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * @author Hodur
 * @className RpcClientProxy.java
 * @description 动态代理类
 * @date 2021年04月13日 22:29
 */
@Slf4j
public class RpcClientProxy implements InvocationHandler {
    private static final String INTERFACE_NAME = "interfaceName";
    /**
     * @describe 用来向server发送请求
     * @author Hodur
     * @date 2021/4/13 22:31
     * @param proxy
     * @param method
     * @param args
     * @return java.lang.Object
     */
    private URL URL = null;
    private final RpcRequestTransport rpcRequestTransport;

    public RpcClientProxy(URL URL, RpcRequestTransport rpcRequestTransport) {
        this.rpcRequestTransport = rpcRequestTransport;
        if (URL.getGroup()==null) {
            URL.setGroup("");
        }
        if (URL.getVersion()==null) {
            URL.setVersion("");
        }
        this.URL = URL;
    }

    public RpcClientProxy(RpcRequestTransport rpcRequestTransport) {
        this.rpcRequestTransport = rpcRequestTransport;
        this.URL = new URL("", "");
    }
    @SuppressWarnings("unchecked")
    public <T> T getProxy(Class<T> clazz) {
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(),new Class<?>[]{clazz},this);
    }
    /**
     * @describe 实际调用的方法
     * @author Hodur
     * @date 2021/4/13 22:45
     * @param proxy
     * @param method
     * @param args
     * @return java.lang.Object
     */
    @SneakyThrows
    @SuppressWarnings("unchecked")
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        log.info("invoked method:[{}]",method.getName());
        RpcRequest rpcRequest = RpcRequest.builder().methodName(method.getName())
                .parameters(args)
                .interfaceName(method.getDeclaringClass().getName())
                .paramTypes(method.getParameterTypes())
                .requestId(UUID.randomUUID().toString())
                .group(URL.getGroup())
                .version(URL.getVersion())
                .build();
        RpcResponse<Object> rpcResponse = null;
        if (rpcRequestTransport instanceof NettyRpcClient) {
            CompletableFuture<RpcResponse<Object>> completableFuture = (CompletableFuture<RpcResponse<Object>>) rpcRequestTransport.sendRpcRequest(rpcRequest);
            rpcResponse = completableFuture.get();
        }
        this.check(rpcResponse,rpcRequest);
        return rpcResponse.getData();
    }

    private void check(RpcResponse<Object> rpcResponse ,RpcRequest rpcRequest) {
        if (rpcResponse == null) {
            throw new RpcException(RpcErrorMessageEnum.SERVICE_INVOCATION_FAILURE,INTERFACE_NAME+":"+rpcRequest.getInterfaceName());
        }
        if (!rpcRequest.getRequestId().equals(rpcResponse.getRequestId())) {
            throw new RpcException(RpcErrorMessageEnum.REQUEST_NOT_MATCH_RESPONSE,INTERFACE_NAME+":"+rpcRequest.getInterfaceName());
        }
        if (rpcResponse.getCode()==null || !rpcResponse.getCode().equals(RpcResponseCodeEnum.SUCCESS.getCode())) {
            throw new RpcException(RpcErrorMessageEnum.SERVICE_INVOCATION_FAILURE,INTERFACE_NAME+":"+rpcRequest.getInterfaceName());
        }
    }
}
