package com.hodur.utils;

/**
 * @author Hodur
 * @className RuntimeUtil.java
 * @description 获取CPU核心数
 * @date 2021年04月13日 19:13
 */
public class RuntimeUtil {
    public static int cpus() {
        return Runtime.getRuntime().availableProcessors();
    }
}
