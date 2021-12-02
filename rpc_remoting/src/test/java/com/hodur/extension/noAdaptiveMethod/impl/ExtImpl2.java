package com.hodur.extension.noAdaptiveMethod.impl;

import com.hodur.extension.noAdaptiveMethod.NoAdaptiveMethodExt;

import java.net.URL;

/**
 * @author Hodur
 * @className ExtImpl2.java
 * @description
 * @date 2021年11月21日 15:39
 */
public class ExtImpl2 implements NoAdaptiveMethodExt {
    @Override
    public String echo(URL url, String s) {
        return "ExtImpl2-echo";
    }
}
