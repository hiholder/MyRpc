package com.hodur.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Hodur
 * @className SerializationTypeEnum.java
 * @description
 * @date 2021年04月13日 18:25
 */
@AllArgsConstructor
@Getter
public enum SerializationTypeEnum {
    kryo((byte) 0x01, "kryo");//,PROTOSTUFF((byte) 0x02, "protostuff");;

    private final byte code;
    private final String name;

    public static String getName(byte code) {
        for (SerializationTypeEnum c : SerializationTypeEnum.values()) {
            if (c.getCode() == code) {
                return c.name;
            }
        }
        return null;
    }
}
