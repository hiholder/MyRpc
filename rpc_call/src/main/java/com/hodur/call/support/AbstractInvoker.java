package com.hodur.call.support;

import com.hodur.call.api.Invocation;
import com.hodur.call.api.Invoker;
import com.hodur.common.URL;
import com.hodur.common.exception.RpcException;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Hodur
 * @className AbstractInvoker.java
 * @description
 * @date 2021年12月04日 23:20
 */
public abstract class AbstractInvoker<T> implements Invoker<T> {

    /**
     * 服务类型
     */
    private final Class<T> type;

    /**
     * url对象
     */
    private final URL url;

    /**
     * 附加值
     */
    private final Map<String, String> attachment;

    public AbstractInvoker(Class<T> type, URL url) {
        this(type, url, (Map<String, String>) null);
    }

    public AbstractInvoker(Class<T> type, URL url, String[] keys) {
        this(type, url, convertAttachment(url, keys));
    }

    public AbstractInvoker(Class<T> type, URL url, Map<String, String> attachment) {
        if (type == null)
            throw new IllegalArgumentException("service type == null");
        if (url == null)
            throw new IllegalArgumentException("service url == null");
        this.type = type;
        this.url = url;
        // 设置附加值为只读模式
        this.attachment = attachment == null ? null : Collections.unmodifiableMap(attachment);
    }

    /**
     * 转化为附加值，把url中的值转化为服务调用invoker的附加值
     * @param url
     * @param keys
     * @return
     */
    private static Map<String, String> convertAttachment(URL url, String[] keys) {
        if (keys == null || keys.length == 0) {
            return null;
        }
        Map<String, String> attachment = new HashMap<String, String>();
        // 遍历key，把值放入附加值集合中
        for (String key : keys) {
            String value = url.getParameter(key);
            if (value != null && value.length() > 0) {
                attachment.put(key, value);
            }
        }
        return attachment;
    }
    @Override
    public Class<T> getInterface() {
        return null;
    }

    @Override
    public Object invoke(Invocation invocation) throws RpcException {
        return null;
    }

    @Override
    public URL getUrl() {
        return null;
    }

    @Override
    public boolean isAvailable() {
        return false;
    }

    @Override
    public void destroy() {

    }

    protected abstract Object doInvoke(Invocation invocation) throws Throwable;
}
