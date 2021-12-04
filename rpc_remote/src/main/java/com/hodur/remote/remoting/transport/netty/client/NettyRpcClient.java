package com.hodur.remote.remoting.transport.netty.client;

import com.hodur.common.enums.CompressTypeEnum;
import com.hodur.common.enums.SerializationTypeEnum;
import com.hodur.common.extension.ExtensionLoader;
import com.hodur.common.factory.SingletonFactory;
import com.hodur.remote.registry.ServiceDiscovery;
import com.hodur.remote.remoting.constants.RpcConstants;
import com.hodur.remote.remoting.dto.RpcMessage;
import com.hodur.remote.remoting.dto.RpcRequest;
import com.hodur.remote.remoting.dto.RpcResponse;
import com.hodur.remote.remoting.transport.RpcRequestTransport;
import com.hodur.remote.remoting.transport.netty.codec.RpcMessageDecoder;
import com.hodur.remote.remoting.transport.netty.codec.RpcMessageEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author Hodur
 * @className NettyRpcClient.java
 * @description 处理传输的类
 * @date 2021年04月14日 15:39
 */
@Slf4j
public class NettyRpcClient implements RpcRequestTransport {
    private final ServiceDiscovery serviceDiscovery;
    private final UnprocessedRequests unprocessedRequests;
    private final ChannelProvider channelProvider;
    private final Bootstrap bootstrap;
    private final EventLoopGroup eventLoopGroup;
    //初始化
    public NettyRpcClient() {
        eventLoopGroup = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup).channel(NioSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                .handler(new LoggingHandler(LogLevel.INFO))
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS,5000)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline p = ch.pipeline();
                        p.addLast(new IdleStateHandler(0,5,0, TimeUnit.SECONDS));
                        p.addLast(new RpcMessageEncoder());
                        p.addLast(new RpcMessageDecoder());
                        p.addLast(new NettyRpcClientHandler());
                    }
                });
        this.serviceDiscovery = ExtensionLoader.getExtensionLoader(ServiceDiscovery.class).getExtension("na");
        this.unprocessedRequests = SingletonFactory.getInstance(UnprocessedRequests.class);
        this.channelProvider = SingletonFactory.getInstance(ChannelProvider.class);
    }

    /**
     * @describe
     * @author Hodur
     * @date 2021/7/21 19:48
 * @param inetSocketAddress
 * @return io.netty.channel.Channel
     */
    @SneakyThrows
    public Channel doConnect(InetSocketAddress inetSocketAddress) {
        CompletableFuture<Channel> completableFuture = new CompletableFuture<>();
        bootstrap.connect(inetSocketAddress).addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                log.info("The client has connected [{}] successful!", inetSocketAddress.toString());
                completableFuture.complete(future.channel());
            } else {
                throw new IllegalStateException();
            }
        });
        return completableFuture.get();
    }
    /**
     * @describe 实现对外暴露的接口，最重要的内容
     * @author Hodur
     * @date 2021/7/21 19:57
     * @param rpcRequest
     * @return java.lang.Object
     */
    @Override
    public Object sendRpcRequest(RpcRequest rpcRequest) {
        //构建返回值
        CompletableFuture<RpcResponse<Object>> resultFuture = new CompletableFuture<>();
        //用rpcRequest创建服务名
        String rpcServiceName = rpcRequest.toRpcProperties().toRpcServiceName();
        //获得服务器地址
        InetSocketAddress inetSocketAddress = serviceDiscovery.lookupService(rpcServiceName);
        //获得服务器地址对应的Channel
        Channel channel = getChannel(inetSocketAddress);
        if (channel.isActive()) {
            unprocessedRequests.put(rpcRequest.getRequestId(),resultFuture);
            RpcMessage rpcMessage = RpcMessage.builder().data(rpcRequest)
                    .codec(SerializationTypeEnum.kryo.getCode())
                    .compress(CompressTypeEnum.GZIP.getCode())
                    .messageType(RpcConstants.REQUEST_TYPE).build();
            channel.writeAndFlush(rpcMessage).addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    log.info("client send message: [{}]",rpcMessage);
                } else {
                    future.channel().close();
                    resultFuture.completeExceptionally(future.cause());
                    log.error("Send failed:",future.cause());
                }
            });
        } else {
            throw new IllegalStateException();
        }
        return resultFuture;
    }

    public Channel getChannel(InetSocketAddress inetSocketAddress) {
        Channel channel = channelProvider.get(inetSocketAddress);
        if (channel == null) {
            channel = doConnect(inetSocketAddress);
            channelProvider.set(inetSocketAddress,channel);
        }
        return channel;
    }

    public void close() {
        eventLoopGroup.shutdownGracefully();
    }
}
