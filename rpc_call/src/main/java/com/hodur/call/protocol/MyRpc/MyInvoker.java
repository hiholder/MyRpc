package com.hodur.call.protocol.MyRpc;

import com.hodur.call.api.Invocation;
import com.hodur.call.api.Invoker;
import com.hodur.call.support.AbstractInvoker;
import com.hodur.common.Constants;
import com.hodur.common.URL;
import com.hodur.remote.remoting.transport.netty.client.NettyRpcClient;

import java.util.Set;

/**
 * @author Hodur
 * @className MyInvoker.java
 * @description
 * @date 2021年12月04日 23:20
 */
public class MyInvoker<T> extends AbstractInvoker<T> {

    private final String version;

    private final Set<Invoker<?>> invokers;

    private final NettyRpcClient[] clients;

    public MyInvoker(Class<T> serviceType, URL url, NettyRpcClient[] clients, Set<Invoker<?>> invokers) {
        super(serviceType, url, new String[]{Constants.INTERFACE_KEY, Constants.GROUP_KEY, Constants.TOKEN_KEY, Constants.TIMEOUT_KEY});
        this.clients = clients;
        // get version.
        // 从url配置中获得版本配置，默认0.0.0
        this.version = url.getParameter(Constants.VERSION_KEY, "0.0.0");
        this.invokers = invokers;
    }

    @Override
    protected Object doInvoke(Invocation invocation) throws Throwable {
        return null;
    }
}
