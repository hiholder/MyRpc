package com.hodur.call.protocol.MyRpc;

import com.hodur.call.api.Exporter;
import com.hodur.call.api.Invocation;
import com.hodur.call.api.Invoker;
import com.hodur.call.support.AbstractProtocol;
import com.hodur.common.Constants;
import com.hodur.common.URL;
import com.hodur.common.exception.RpcException;
import com.hodur.remote.remoting.transport.netty.client.NettyRpcClient;
import com.hodur.remote.remoting.transport.netty.server.NettyRpcServer;
import com.hodur.remote.remoting.transport.netty.server.NettyRpcServerHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Hodur
 * @className MyProtocol.java
 * @description
 * @date 2021年12月04日 10:09
 */
public class MyProtocol extends AbstractProtocol {

    private NettyRpcServerHandler serverHandler= new NettyRpcServerHandler();

    private final Map<String, NettyRpcServer> serverMap = new ConcurrentHashMap<String, NettyRpcServer>();

    Invoker<?> getInvoker(URL url, Invocation inv) {
        boolean isCallBackServiceInvoke = false;
        boolean isStubServiceInvoke = false;
        // 得到服务key  group+"/"+serviceName+":"+serviceVersion+":"+port
        String serviceKey = serviceKey(url.getPort(), url.getPath(), inv.getAttachments().get(Constants.VERSION_KEY), inv.getAttachments().get(Constants.GROUP_KEY));
        // 根据服务key从集合中获得服务暴露者
        MyExporter<?> exporter = (MyExporter<?>) exporterMap.get(serviceKey);

        // 返回invoker
        return exporter.getInvoker();
    }

    @Override
    public int getDefaultPort() {
        return 0;
    }
    /**
     * @describe 实现ServiceProviderImpl类服务暴露的功能
     * @author Hodur
     * @date 2021/12/4 19:03
     * @param invoker
     * @return com.hodur.call.api.Exporter<T>
     */
    @Override
    public <T> Exporter<T> export(Invoker<T> invoker) throws RpcException {
        URL url = invoker.getUrl();

        String key = serviceKey(url);
        // 创建exporter
        MyExporter<T> exporter = new MyExporter<T>(invoker, key, exporterMap);
        // 加入到集合
        exporterMap.put(key, exporter);

        // 打开服务
        openServer(url);
        return exporter;
    }
    /**
     * @describe 打开服务
     * @author Hodur
     * @date 2021/12/4 19:07
     * @param url
     */
    private void openServer(URL url) {
        boolean isServer = url.getParameter(Constants.IS_SERVER_KEY, true);
        String key = url.getAddress();
        if (isServer) {
            NettyRpcServer server = serverMap.get(key);
            if (server == null) {
                serverMap.put(key, createServer(url));
            }
        }
    }

    private NettyRpcServer createServer(URL url) {
        url = url.addParameterIfAbsent(Constants.CHANNEL_READONLYEVENT_SENT_KEY, Boolean.TRUE.toString());

        url = url.addParameterIfAbsent(Constants.HEARTBEAT_KEY, String.valueOf(Constants.DEFAULT_HEARTBEAT));
        // 获得远程通讯服务端实现方式，默认用netty
        String str = url.getParameter(Constants.SERVER_KEY, Constants.DEFAULT_REMOTING_SERVER);
        NettyRpcServer server = null;
        try {
            server = new NettyRpcServer(url, serverHandler);
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return server;

    }

    @Override
    public <T> Invoker<T> refer(Class<T> serviceType, URL url) throws RpcException {
        // create rpc invoker. 创建一个DubboInvoker对象
        MyInvoker<T> invoker = new MyInvoker<T>(serviceType, url, getClients(url), invokers);
        // 把该invoker放入集合
        invokers.add(invoker);
        return invoker;
    }

    public NettyRpcClient[] getClients(URL url) {
        // whether to share connection
        // 一个连接是否对于一个服务
        boolean service_share_connect = false;
        int connections = url.getParameter(Constants.CONNECTIONS_KEY, 0);
        // if not configured, connection is shared, otherwise, one connection for one service
        // 如果为0，则是共享类，并且连接数为1
        if (connections == 0) {
            service_share_connect = true;
            connections = 1;
        }
        NettyRpcClient[] clients = new NettyRpcClient[connections];
        return clients;
    }
}
