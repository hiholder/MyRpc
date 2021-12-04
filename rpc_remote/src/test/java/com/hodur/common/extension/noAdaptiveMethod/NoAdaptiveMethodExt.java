package com.hodur.common.extension.noAdaptiveMethod;

import com.hodur.common.extension.SPI;

import java.net.URL;

@SPI("impl1")
public interface NoAdaptiveMethodExt {
    String echo(URL url, String s);
}
