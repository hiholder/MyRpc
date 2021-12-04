package com.hodur.call.support;

import com.hodur.call.api.Exporter;
import com.hodur.call.api.Invoker;

/**
 * @author Hodur
 * @className AbstractExporter.java
 * @description
 * @date 2021年12月04日 17:51
 */
public abstract class AbstractExporter<T> implements Exporter<T> {

    /**
     * 实体域
     */
    private final Invoker<T> invoker;

    /**
     * 是否取消暴露服务
     */
    private volatile boolean unexported = false;

    public AbstractExporter(Invoker<T> invoker) {
        if (invoker == null)
            throw new IllegalStateException("service invoker == null");
        if (invoker.getInterface() == null)
            throw new IllegalStateException("service type == null");
        if (invoker.getUrl() == null)
            throw new IllegalStateException("service url == null");
        this.invoker = invoker;
    }

    @Override
    public Invoker<T> getInvoker() {
        return invoker;
    }

    @Override
    public void unexport() {
        // 如果已经消取消暴露，则之间返回
        if (unexported) {
            return;
        }
        // 设置为true
        unexported = true;
        // 销毁该实体域
        getInvoker().destroy();
    }

    @Override
    public String toString() {
        return getInvoker().toString();
    }
}
