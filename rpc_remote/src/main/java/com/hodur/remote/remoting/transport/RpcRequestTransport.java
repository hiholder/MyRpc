package com.hodur.remote.remoting.transport;

import com.hodur.common.extension.SPI;
import com.hodur.remote.remoting.dto.RpcRequest;

@SPI
public interface RpcRequestTransport {
    /**
     * @describe 向服务器发送rpc请求，并得到结果
     * @author Hodur
     * @date 2021/4/13 22:34
     * @param rpcRequest
     * @return java.lang.Object
     */
    Object sendRpcRequest(RpcRequest rpcRequest);
}
