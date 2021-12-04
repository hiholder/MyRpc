package com.hodur.call.support;

import com.hodur.call.api.Exporter;
import com.hodur.call.api.Invoker;
import com.hodur.call.api.Protocol;
import com.hodur.common.Constants;
import com.hodur.common.URL;
import com.hodur.common.exception.RpcException;
import com.hodur.common.utils.concurrent.ConcurrentHashSet;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Hodur
 * @className AbstractProtocol.java
 * @description
 * @date 2021年12月03日 23:10
 */
@Slf4j
public abstract class AbstractProtocol implements Protocol {
    /**
     * 服务暴露集合
     */
    protected final Map<String, Exporter<?>> exporterMap = new ConcurrentHashMap<String, Exporter<?>>();

    protected final Set<Invoker<?>> invokers = new ConcurrentHashSet<Invoker<?>>();

    protected static String serviceKey(int port, String serviceName, String serviceVersion, String serviceGroup) {
        return ProtocolUtils.serviceKey(port, serviceName, serviceVersion, serviceGroup);
    }

    protected static String serviceKey(URL url) {
        // 获得绑定的端口号
        int port = url.getParameter(Constants.BIND_PORT_KEY, url.getPort());
        return serviceKey(port, url.getPath(), url.getParameter(Constants.VERSION_KEY), url.getParameter(Constants.GROUP_KEY));
    }
    @Override
    public void destroy() {
        // 遍历服务引用实体
        for (Invoker<?> invoker : invokers) {
            if (invoker != null) {
                invokers.remove(invoker);
                try {
                    log.info("Destroy reference: " + invoker.getUrl());
                    invoker.destroy();
                } catch (Throwable t) {
                    log.warn(t.getMessage(), t);
                }
            }
        }
        // 遍历服务暴露者
        for (String key : new ArrayList<String>(exporterMap.keySet())) {
            // 从集合中移除
            Exporter<?> exporter = exporterMap.remove(key);
            if (exporter != null) {
                try {
                    log.info("Unexport service: " + exporter.getInvoker().getUrl());
                    exporter.unexport();
                } catch (Throwable t) {
                    log.warn(t.getMessage(), t);
                }
            }
        }
    }
}
