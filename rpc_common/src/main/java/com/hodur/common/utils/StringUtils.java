package com.hodur.common.utils;

import com.hodur.common.io.UnsafeStringWriter;

import java.io.PrintWriter;

/**
 * @author Hodur
 * @className StringUtils.java
 * @description
 * @date 2021年12月03日 22:59
 */
public class StringUtils {
    public static String toString(Throwable e) {
        UnsafeStringWriter w = new UnsafeStringWriter();
        PrintWriter p = new PrintWriter(w);
        p.print(e.getClass().getName());
        if (e.getMessage() != null) {
            p.print(": " + e.getMessage());
        }
        p.println();
        try {
            e.printStackTrace(p);
            return w.toString();
        } finally {
            p.close();
        }
    }
}
