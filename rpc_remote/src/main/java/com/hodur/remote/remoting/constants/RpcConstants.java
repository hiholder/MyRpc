package com.hodur.remote.remoting.constants;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @author Hodur
 * @className RpcConstants.java
 * @description RPC的常量属性设置
 * @date 2021年04月14日 10:10
 */
public class RpcConstants {
    /**
     * Magic number. Verify RpcMessage
     */
    //魔数
    public static final byte[] MAGIC_NUMBER = {(byte) 'g', (byte) 'r', (byte) 'p', (byte) 'c'};
    //默认文件编码
    public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;   //默认字符集
    //version information
    public static final byte VERSION = 1;   //版本号
    public static final byte TOTAL_LENGTH = 16; //总长度
    public static final byte REQUEST_TYPE = 1;  //请求类型
    public static final byte RESPONSE_TYPE = 2; //响应类型
    //ping
    public static final byte HEARTBEAT_REQUEST_TYPE = 3;    //心跳请求类型
    //pong
    public static final byte HEARTBEAT_RESPONSE_TYPE = 4;   //心跳响应类型
    public static final int HEAD_LENGTH = 16;   //报文头长度
    public static final String PING = "ping";
    public static final String PONG = "pong";
    public static final int MAX_FRAME_LENGTH = 8 * 1024 * 1024;
}

