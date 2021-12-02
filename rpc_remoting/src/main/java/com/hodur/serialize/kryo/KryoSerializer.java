package com.hodur.serialize.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.hodur.exception.SerializeException;
import com.hodur.remoting.dto.RpcRequest;
import com.hodur.remoting.dto.RpcResponse;
import com.hodur.serialize.Serializer;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author Hodur
 * @className kryoSerializer.java
 * @description Kryo序列化类
 * @date 2021年04月14日 14:52
 */

@Slf4j
public class KryoSerializer implements Serializer {
    /**
     * @describe 因为Kryo不是线程安全的，因此要用ThreadLocal来存储Kryo对象
     * @author Hodur
     * @date 2021/4/14 15:20
     * @param null
     * @return null
     */
    private final ThreadLocal<Kryo> kryoThreadLocal = ThreadLocal.withInitial(()->{
       Kryo kryo = new Kryo();
       kryo.register(RpcResponse.class);
       kryo.register(RpcRequest.class);
       return kryo;
    });

    @Override
    public byte[] serialize(Object obj) {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             Output output = new Output(byteArrayOutputStream)){
            Kryo kryo = kryoThreadLocal.get();
            //Object->byte:将对象序列化为byte数组
            kryo.writeObject(output,obj);
            kryoThreadLocal.remove();
            return output.toBytes();
        } catch (Exception e) {
            throw new SerializeException("Serialization failed");
        }
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
             Input input = new Input(byteArrayInputStream)){
            Kryo kryo = kryoThreadLocal.get();
            //byte->Object:从byte数组中反序列化出对象
            Object o = kryo.readObject(input, clazz);
            kryoThreadLocal.remove();
            return clazz.cast(o);
        } catch (IOException e) {
            throw new SerializeException("Deserialization failed");
        }
    }
}
