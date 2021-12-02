package com.hodur.extension.noAdaptiveMethod;

import com.hodur.extension.SPI;

import java.net.URL;

@SPI("impl1")
public interface NoAdaptiveMethodExt {
    String echo(URL url, String s);
}
