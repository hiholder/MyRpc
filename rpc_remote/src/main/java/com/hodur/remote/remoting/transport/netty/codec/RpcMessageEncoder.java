package com.hodur.remote.remoting.transport.netty.codec;

import com.hodur.remote.compress.Compress;
import com.hodur.common.enums.CompressTypeEnum;
import com.hodur.common.enums.SerializationTypeEnum;
import com.hodur.common.extension.ExtensionLoader;
import com.hodur.remote.remoting.constants.RpcConstants;
import com.hodur.remote.remoting.dto.RpcMessage;
import com.hodur.remote.serialize.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;


import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Hodur
 * @className RpcMessageEncoder.java
 * @description
 * @date 2021年04月14日 12:27
 */
@Slf4j
public class RpcMessageEncoder extends MessageToByteEncoder<RpcMessage> {
    private static final AtomicInteger ATOMIC_INTEGER = new AtomicInteger(0);
    @Override
    protected void encode(ChannelHandlerContext ctx, RpcMessage rpcMessage, ByteBuf out) throws Exception {
        try {
            out.writeBytes(RpcConstants.MAGIC_NUMBER);
            out.writeByte(RpcConstants.VERSION);
            // leave a place to write the value of full length
            out.writerIndex(out.writerIndex()+4);
            byte messageType = rpcMessage.getMessageType();
            out.writeByte(messageType);
            out.writeByte(rpcMessage.getCodec());
            out.writeByte(CompressTypeEnum.GZIP.getCode());
            out.writeInt(ATOMIC_INTEGER.getAndIncrement());
            byte[] bodyBytes = null;
            int fullLength = RpcConstants.HEAD_LENGTH;

            if (messageType != RpcConstants.HEARTBEAT_RESPONSE_TYPE
                    && messageType != RpcConstants.HEARTBEAT_RESPONSE_TYPE) {
                //序列化对象
                String codecName = SerializationTypeEnum.getName(rpcMessage.getCodec());
                log.info("codec name: [{}]",codecName);
                Serializer serializer = ExtensionLoader.getExtensionLoader(Serializer.class)
                        .getExtension(codecName);
                //KryoSerializer serializer = new KryoSerializer();
                bodyBytes = serializer.serialize(rpcMessage.getData());
                //压缩字节
                String compressName = CompressTypeEnum.getName(rpcMessage.getCompress());
                Compress compress = ExtensionLoader.getExtensionLoader(Compress.class)
                        .getExtension(compressName);
                bodyBytes = compress.compress(bodyBytes);
                fullLength += bodyBytes.length;
            }
            if (bodyBytes != null) {
                out.writeBytes(bodyBytes);
            }
            int writeIndex = out.writerIndex();
            out.writerIndex(writeIndex-fullLength + RpcConstants.MAGIC_NUMBER.length+1);
            out.writeInt(fullLength);
            out.writerIndex(writeIndex);
        } catch (Exception e) {
            log.error("Encode request error!",e);
        }
    }
}
