package com.hodur.registry.support;

import com.hodur.URL;
import com.hodur.utils.concurrent.ConcurrentHashSet;
import com.hodur.utils.concurrent.threadpool.ThreadPoolFactoryUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * @author Hodur
 * @className FailbackRegistry.java
 * @description 负责在操作失败后进行定期重试
 * @date 2021年12月01日 15:00
 */
@Slf4j
public abstract class FailbackRegistry extends AbstractRegistry{
    // 定时任务执行器
    private final ScheduledExecutorService retryExecutor = Executors.newScheduledThreadPool(1, ThreadPoolFactoryUtils.createThreadFactory("RegistryFailedRetryTimer", true));
    // 注册失败的URL集合
    private final Set<URL> failedRegistered = new ConcurrentHashSet<URL>();
    // 取消注册失败的URL集合
    private final Set<URL> failedUnregistered = new ConcurrentHashSet<URL>();
    public FailbackRegistry(URL url) {
        super(url);
    }

    @Override
    public void register(URL url) {
        super.register(url);
        failedRegistered.remove(url);
        failedUnregistered.remove(url);
        try {
            // 向注册中心发送一个注册请求
            doRegister(url);
        } catch (Exception e) {
            // 将注册失败的url放入缓存定期重试
            failedRegistered.add(url);
            e.printStackTrace();
        }
    }

    @Override
    public void unregister(URL url) {
        super.unregister(url);
        try {
            doUnregister(url);
        } catch (Exception e) {
            failedUnregistered.add(url);
            e.printStackTrace();
        }
    }

    protected abstract void doRegister(URL url);

    protected abstract void doUnregister(URL url);
}
