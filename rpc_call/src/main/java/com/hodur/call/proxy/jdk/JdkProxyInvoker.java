package com.hodur.call.proxy.jdk;

import com.hodur.call.api.GenericService;
import com.hodur.call.api.Invoker;
import com.hodur.call.proxy.AbstractProxyInvoker;
import com.hodur.call.proxy.InvokerInvocationHandler;
import com.hodur.call.service.EchoService;
import com.hodur.common.Constants;
import com.hodur.common.URL;
import com.hodur.common.exception.RpcException;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author Hodur
 * @className JdkProxyInvoker.java
 * @description 实现dubbo中ProxyFactory的功能
 * @date 2021年12月03日 22:07
 */
public class JdkProxyInvoker {




    @SuppressWarnings("unchecked")
    public <T> T getProxy(Invoker<T> invoker, Class<?>[] interfaces) {
        // 调用了 Proxy.newProxyInstance直接获得代理类
        return (T) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), interfaces, new InvokerInvocationHandler(invoker));
    }


    public <T> Invoker<T> getInvoker(T proxy, Class<T> type, URL url) {
        // 创建AbstractProxyInvoker对象
        return new AbstractProxyInvoker<T>(proxy, type, url) {
            @Override
            protected Object doInvoke(T proxy, String methodName,
                                      Class<?>[] parameterTypes,
                                      Object[] arguments) throws Throwable {
                // 反射获得方法
                Method method = proxy.getClass().getMethod(methodName, parameterTypes);
                // 执行方法
                return method.invoke(proxy, arguments);
            }
        };
    }

    public <T> T getProxy(Invoker<T> invoker) throws RpcException, ClassNotFoundException {
        return getProxy(invoker, false);
    }

    public <T> T getProxy(Invoker<T> invoker, boolean generic) throws RpcException, ClassNotFoundException {
        Class<?>[] interfaces = null;
        // 获得需要代理的接口
        String config = invoker.getUrl().getParameter("interfaces");
        if (config != null && config.length() > 0) {
            // 根据逗号把每个接口分割开
            String[] types = Constants.COMMA_SPLIT_PATTERN.split(config);
            if (types != null && types.length > 0) {
                // 创建接口类型数组
                interfaces = new Class<?>[types.length + 2];
                // 第一个放invoker的服务接口
                interfaces[0] = invoker.getInterface();
                // 第二个位置放回声测试服务的接口类
                interfaces[1] = EchoService.class;
                // 其他接口循环放入
                for (int i = 0; i < types.length; i++) {
                    // todo: 用reflectUtils实现
                    interfaces[i+1] = Class.forName(types[i]);
                }
            }
        }
        // 如果接口为空，就是config为空，则是回声测试
        if (interfaces == null) {
            interfaces = new Class<?>[]{invoker.getInterface(), EchoService.class};
        }

        // 如果是泛化服务，那么在代理的接口集合中加入泛化服务类型
        if (!invoker.getInterface().equals(GenericService.class) && generic) {
            int len = interfaces.length;
            Class<?>[] temp = interfaces;
            interfaces = new Class<?>[len + 1];
            System.arraycopy(temp, 0, interfaces, 0, len);
            interfaces[len] = GenericService.class;
        }

        // 获得代理
        return getProxy(invoker, interfaces);
    }


}
