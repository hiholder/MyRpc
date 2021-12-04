package com.hodur.call.proxy;

import com.hodur.call.RpcInvocation;
import com.hodur.call.api.Invoker;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author Hodur
 * @className InvokerInvocationHandler.java
 * @description 包装invoke的具体实现
 * @date 2021年12月03日 22:14
 */
public class InvokerInvocationHandler implements InvocationHandler {
    private final Invoker<?> invoker;

    public InvokerInvocationHandler(Invoker<?> handler) {
        this.invoker = handler;
    }
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 获得方法名
        String methodName = method.getName();
        // 获得参数类型
        Class<?>[] parameterTypes = method.getParameterTypes();
        // 如果方法参数类型是object类型，则直接反射调用
        if (method.getDeclaringClass() == Object.class) {
            return method.invoke(invoker, args);
        }

        // 基础方法，不使用RPC调用
        if ("toString".equals(methodName) && parameterTypes.length == 0) {
            return invoker.toString();
        }
        if ("hashCode".equals(methodName) && parameterTypes.length == 0) {
            return invoker.hashCode();
        }
        if ("equals".equals(methodName) && parameterTypes.length == 1) {
            return invoker.equals(args[0]);
        }
        return invoker.invoke(new RpcInvocation(method, args));
    }
}
