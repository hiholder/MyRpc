package com.hodur.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Hodur
 * @className CompressTypeEnum.java
 * @description
 * @date 2021年04月13日 18:23
 */
@AllArgsConstructor
@Getter
public enum CompressTypeEnum {
    GZIP((byte) 0x01, "gzip");

    private final byte code;
    private final String name;

    public static String getName(byte code) {
        for (CompressTypeEnum c : CompressTypeEnum.values()) {
            if (c.getCode() == code) {
                return c.name;
            }
        }
        return null;
    }
}
