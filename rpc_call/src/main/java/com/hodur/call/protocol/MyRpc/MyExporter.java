package com.hodur.call.protocol.MyRpc;

import com.hodur.call.api.Exporter;
import com.hodur.call.api.Invoker;
import com.hodur.call.support.AbstractExporter;

import java.util.Map;

/**
 * @author Hodur
 * @className MyExporter.java
 * @description
 * @date 2021年12月04日 17:50
 */
public class MyExporter<T> extends AbstractExporter<T> {

    private final String key;

    private final Map<String, Exporter<?>> exporterMap;

    public MyExporter(Invoker<T> invoker, String key, Map<String, Exporter<?>> exporterMap) {
        super(invoker);
        this.key = key;
        this.exporterMap = exporterMap;
    }

    public void unexport() {
        super.unexport();
        // 从集合中移除该key
        exporterMap.remove(key);
    }
}
