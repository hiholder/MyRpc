package com.hodur.remote.serialize;

import com.hodur.common.extension.SPI;

@SPI
public interface Serializer {
    byte[] serialize(Object obj);

    <T> T deserialize(byte[] bytes, Class<T> clazz);
}
