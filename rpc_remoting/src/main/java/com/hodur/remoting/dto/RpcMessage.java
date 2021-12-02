package com.hodur.remoting.dto;

import lombok.*;

/**
 * @author Hodur
 * @className RpcMessage.java
 * @description 报文格式，前面的几项组成了报文头，报文头所需的常量来自于constants。后面的data根据请求和响应有所不同
 * @date 2021年04月13日 19:31
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class RpcMessage {
    //rpc消息类型
    private byte messageType;//1请求，2响应，3心跳请求，4心跳响应
    //序列化类型
    private byte codec;
    //压缩类型
    private byte compress;
    //请求id
    private int requestId;
    //请求数据
    private Object data;
}
