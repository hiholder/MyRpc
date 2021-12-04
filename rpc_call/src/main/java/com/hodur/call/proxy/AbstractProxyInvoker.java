package com.hodur.call.proxy;

import com.hodur.call.api.Invocation;
import com.hodur.call.api.Invoker;
import com.hodur.common.URL;
import com.hodur.common.exception.RpcException;


/**
 * @author Hodur
 * @className AbstractProxyInvoker.java
 * @description
 * @date 2021年12月03日 21:47
 */
public abstract class AbstractProxyInvoker<T> implements Invoker<T> {
    /**
     * 代理对象
     */
    private final T proxy;
    /**
     * 类型
     */
    private final Class<T> type;
    /**
     * url对象
     */
    private final URL url;

    public AbstractProxyInvoker(T proxy, Class<T> type, URL url) {
        if (proxy == null) {
            throw new IllegalArgumentException("proxy == null");
        }
        if (type == null) {
            throw new IllegalArgumentException("interface == null");
        }
        if (!type.isInstance(proxy)) {
            throw new IllegalArgumentException(proxy.getClass().getName() + " not implement interface " + type);
        }
        this.proxy = proxy;
        this.type = type;
        this.url = url;
    }

    @Override
    public Class<T> getInterface() {
        return type;
    }

    @Override
    public Object invoke(Invocation invocation) throws RpcException {
        try {
            return doInvoke(proxy, invocation.getMethodName(), invocation.getParameterTypes(), invocation.getArguments());
        }  catch (Throwable e) {
            throw new RpcException("Failed to invoke remote proxy method " + invocation.getMethodName() + " to " + getUrl() + ", cause: " + e.getMessage(), e);
        }
    }

    @Override
    public URL getUrl() {
        return url;
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public void destroy() {

    }

    protected abstract Object doInvoke(T proxy, String methodName, Class<?>[] parameterTypes, Object[] arguments) throws Throwable;

    public String toString() {
        return getInterface() + "->" + (getUrl()==null?" " : getUrl().toString());
    }
}
