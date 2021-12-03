package com.hodur.utils.concurrent;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author Hodur
 * @className ExecutorUtil.java
 * @description
 * @date 2021年12月03日 10:47
 */

@Slf4j
public class ExecutorUtil {

    public static boolean isTerminated(Executor executor) {
        if (executor instanceof ExecutorService) {
            // isTerminated当调用shutdown()方法后，并且所有提交的任务完成后返回为true
            if (((ExecutorService) executor).isTerminated()) {
                return true;
            }
        }
        return false;
    }
    public static void gracefulShutdown(Executor executor, int timeout) {
        if (!(executor instanceof ExecutorService) || isTerminated(executor)) {
            return;
        }
        final ExecutorService es = (ExecutorService) executor;
        try {
            // 停止接收新的任务并且等待已经提交的任务（包含正在执行和提交未执行）执行完成
            // 当所有提交任务执行完毕，线程池即将被关闭
            es.shutdown();
        } catch (SecurityException ex2) {
            return;
        } catch (NullPointerException ex2) {
            return;
        }

        try {
            // 当等待超过设定时间时，会检测ExecutorService是否已经关闭，如果没关闭，再关闭一次
            if (es.awaitTermination(timeout, TimeUnit.MICROSECONDS)) {
                es.shutdownNow();
            }
        } catch (InterruptedException ex) {
            es.shutdownNow();
            Thread.currentThread().interrupt();
        }

    }
}
