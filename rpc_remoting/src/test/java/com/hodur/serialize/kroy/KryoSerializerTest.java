package com.hodur.serialize.kroy;

import com.hodur.remoting.dto.RpcRequest;
import com.hodur.serialize.kryo.KryoSerializer;
import org.junit.Test;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
/**
 * @author Hodur
 * @className kryoSerializerTest.java
 * @description
 * @date 2021年04月15日 19:49
 */
public class KryoSerializerTest {
    @Test
    public void kryoSerializerTest() {
        RpcRequest target = RpcRequest.builder().methodName("hello")
                .parameters(new Object[]{"sayhelooloo", "sayhelooloosayhelooloo"})
                .interfaceName("com.hodur.HelloService")
                .paramTypes(new Class<?>[]{String.class,String.class})
                .requestId(UUID.randomUUID().toString())
                .group("group1")
                .version("version1")
                .build();
        KryoSerializer kryoSerializer = new KryoSerializer();
        byte[] bytes = kryoSerializer.serialize(target);
        RpcRequest actual = kryoSerializer.deserialize(bytes, RpcRequest.class);
        assertEquals(target.getGroup(),actual.getGroup());
        assertEquals(target.getVersion(),actual.getVersion());
        assertEquals(target.getRequestId(),actual.getRequestId());
    }
}
