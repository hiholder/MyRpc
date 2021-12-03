package com.hodur.registry.support;

import com.hodur.Constants;
import com.hodur.URL;
import com.hodur.utils.concurrent.ConcurrentHashSet;
import com.hodur.utils.concurrent.ExecutorUtil;
import com.hodur.utils.concurrent.threadpool.ThreadPoolFactoryUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

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

    // Timer for failure retry, regular check if there is a request for failure, and if there is, an unlimited retry
    // 失败重试定时器，定时去检查是否有请求失败的，如有，无限次重试。
    private final ScheduledFuture<?> retryFuture;

    // 注册失败的URL集合
    private final Set<URL> failedRegistered = new ConcurrentHashSet<URL>();

    // 取消注册失败的URL集合
    private final Set<URL> failedUnregistered = new ConcurrentHashSet<URL>();

    // 重试频率
    private final int retryPeriod;

    public FailbackRegistry(URL url) {
        super(url);
        // 从url中读取重试频率，如果为空，则默认5000ms
        this.retryPeriod = url.getParameter(Constants.REGISTRY_RETRY_PERIOD_KEY, Constants.DEFAULT_REGISTRY_RETRY_PERIOD);
        // 创建失败重试定时器
        this.retryFuture = retryExecutor.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                try {
                    retry();
                } catch (Throwable t) {
                    log.error("Unexpected error occur at failed retry, cause: " + t.getMessage(), t);
                }
            }
        }, retryPeriod, retryPeriod, TimeUnit.MICROSECONDS);
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

    /**
     * @describe 在AbstractRegistry基础上增加了定时重试的功能
     * @author Hodur
     * @date 2021/12/3 9:08
     */
    @Override
    protected void recover() {
        Set<URL> recoverRegistered = new HashSet<>(getRegistered());
        if (!recoverRegistered.isEmpty()) {
            log.info("Recover register url " + recoverRegistered);
            for (URL url : recoverRegistered) {
                failedRegistered.add(url);
            }
        }
    }

    /**
     * @describe 行动失败后重试
     * @author Hodur
     * @date 2021/12/3 9:43
     */
    protected void retry() {
        // 重试执行注册
        if (!failedRegistered.isEmpty()) {
            // 避免发生冲突
            Set<URL> failed = new HashSet<URL>(failedRegistered);
            if (failed.size() > 0) {
                log.info("Retry register " + failed);
                try {
                    for (URL url : failed) {
                        try {
                            // 注册
                            doRegister(url);
                            // 从注册失败的缓存中移除
                            failedRegistered.remove(url);
                        } catch (Throwable t) {
                            log.warn("Failed to retry register " + failed + ", waiting for again, cause: " + t.getMessage(), t);
                        }
                    }
                } catch (Throwable t) {
                    log.warn("Failed to retry register " + failed + ", waiting for again, cause: " + t.getMessage(), t);
                }
            }
        }
        // 重试取消注册
        if (!failedUnregistered.isEmpty()) {
            // 避免发生冲突
            Set<URL> failed = new HashSet<URL>(failedUnregistered);
            if (!failed.isEmpty()) {
                log.info("Retry unregister " + failed);
            }
            try {
                for (URL url : failed) {
                    try {
                        // 取消注册
                        doUnregister(url);
                        // 从缓存中移除
                        failedUnregistered.remove(url);
                    } catch (Throwable t) {
                        log.warn("Failed to retry unregister " + failed + ", waiting for again, cause: " + t.getMessage(), t);
                    }
                }
            } catch (Throwable t) {
                log.warn("Failed to retry unregister " + failed + ", waiting for again, cause: " + t.getMessage(), t);
            }
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        try {
            retryFuture.cancel(true);
        } catch (Throwable t) {
            log.warn(t.getMessage());
        }
        ExecutorUtil.gracefulShutdown(retryExecutor, retryPeriod);
    }

    protected abstract void doRegister(URL url);

    protected abstract void doUnregister(URL url);
}
