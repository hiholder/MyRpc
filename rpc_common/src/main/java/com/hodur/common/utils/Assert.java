package com.hodur.common.utils;

/**
 * @author Hodur
 * @className Assert.java
 * @description
 * @date 2021年12月01日 22:19
 */
public class Assert {
    protected Assert() {
    }

    public static void notNull(Object obj, String message) {
        if (obj == null) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void notNull(Object obj, RuntimeException exeception) {
        if (obj == null) {
            throw exeception;
        }
    }
}
