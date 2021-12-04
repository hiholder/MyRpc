package com.hodur.call.api;

import com.hodur.common.URL;
import com.hodur.common.exception.RpcException;
import com.hodur.common.extension.Adaptive;
import com.hodur.common.extension.SPI;

@SPI
public interface Protocol {

    int getDefaultPort();

    @Adaptive
    <T> Exporter<T> export(Invoker<T> invoker) throws RpcException;

    @Adaptive
    <T> Invoker<T> refer(Class<T> type, URL url) throws RpcException;

    void destroy();
}
