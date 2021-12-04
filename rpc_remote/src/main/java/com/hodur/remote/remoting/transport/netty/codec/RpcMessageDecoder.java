package com.hodur.remote.remoting.transport.netty.codec;

import com.hodur.remote.compress.Compress;
import com.hodur.common.enums.CompressTypeEnum;
import com.hodur.common.enums.SerializationTypeEnum;
import com.hodur.common.extension.ExtensionLoader;
import com.hodur.remote.remoting.constants.RpcConstants;
import com.hodur.remote.remoting.dto.RpcMessage;
import com.hodur.remote.remoting.dto.RpcRequest;
import com.hodur.remote.remoting.dto.RpcResponse;
import com.hodur.remote.serialize.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import lombok.extern.slf4j.Slf4j;


import java.util.Arrays;

/**
 * @author Hodur
 * @className RpcMessageDecoder.java
 * @description
 * @date 2021年04月14日 12:27
 */
@Slf4j
public class RpcMessageDecoder extends LengthFieldBasedFrameDecoder {
    public RpcMessageDecoder() {
        //lengthFieldOffset:魔数4B，版本1B，所以值是5
        //lengthFieldLength: 全长是4B，所有值为4
        //lengthAdjustment：魔数，版本号，全长一共9B，所以偏移值是-9
        //initialBytesToStrip:因为要检查最开始的魔数和版本号，所以不需要删除任何字符
        this(RpcConstants.MAX_FRAME_LENGTH,5,4,-9,0);
    }

    /**
     * @describe
     * @author Hodur
     * @date 2021/4/14 12:44
     * @param maxFrameLength
     * @param lengthFieldOffset
     * @param lengthFieldLength
     * @param lengthAdjustment
     * @param initialBytesToStrip
     * @return null
     */
    public RpcMessageDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength,
                             int lengthAdjustment, int initialBytesToStrip) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength,lengthAdjustment,initialBytesToStrip);
    }

    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        Object decoded = super.decode(ctx, in);
        if (decoded instanceof ByteBuf) {
            ByteBuf frame = (ByteBuf) decoded;
            if (frame.readableBytes() >= RpcConstants.TOTAL_LENGTH) {
                try {
                    return decodeFrame(frame);
                } catch (Exception e) {
                    log.error("Decode frame error!",e);
                    throw e;
                } finally {
                    frame.release();
                }
            }
        }
        return decoded;
    }

    private Object decodeFrame(ByteBuf in) {
        //必须按序读取
        checkMagicNumber(in);
        checkVersion(in);
        int fullLength = in.readInt();

        byte messageType = in.readByte();
        byte codecType = in.readByte();
        byte compressType = in.readByte();
        int requestId = in.readInt();
        RpcMessage rpcMessage = RpcMessage.builder()
                .codec(codecType).requestId(requestId)
                .messageType(messageType).build();
        if (messageType == RpcConstants.HEARTBEAT_REQUEST_TYPE) {
            rpcMessage.setData(RpcConstants.PING);
            return rpcMessage;
        }
        if (messageType == RpcConstants.HEARTBEAT_RESPONSE_TYPE) {
            rpcMessage.setData(RpcConstants.PONG);
            return rpcMessage;
        }
        int bodyLength = fullLength - RpcConstants.HEAD_LENGTH;
        if (bodyLength > 0) {
            byte[] bs = new byte[bodyLength];
            in.readBytes(bs);
            //decompress the bytes
            String compressName = CompressTypeEnum.getName(compressType);
            Compress compress = ExtensionLoader.getExtensionLoader(Compress.class).getExtension(compressName);
            bs = compress.decompress(bs);
            //deserialize the object
            String codecName = SerializationTypeEnum.getName(rpcMessage.getCodec());
            log.info("codec name: [{}]", codecName);
            Serializer serializer = ExtensionLoader.getExtensionLoader(Serializer.class).getExtension(codecName);
            if (messageType == RpcConstants.REQUEST_TYPE) {
                RpcRequest tmpValue = serializer.deserialize(bs,RpcRequest.class);
                rpcMessage.setData(tmpValue);
            } else {
                RpcResponse tmpValue = serializer.deserialize(bs,RpcResponse.class);
                rpcMessage.setData(tmpValue);
            }
        }
        return rpcMessage;
    }

    private void checkVersion(ByteBuf in) {
        //read the version and compare
        byte version = in.readByte();
        if (version != RpcConstants.VERSION) {
            throw new RuntimeException("version isn't compatible" + version);
        }
    }

    private void checkMagicNumber(ByteBuf in) {
        int len =RpcConstants.MAGIC_NUMBER.length;
        byte[] tmp = new byte[len];
        in.readBytes(tmp);
        for (int i = 0; i < len; i++) {
            if (tmp[i] != RpcConstants.MAGIC_NUMBER[i]) {
                throw new IllegalArgumentException("Unknown magic code:"+ Arrays.toString(tmp));
            }
        }
    }
}
