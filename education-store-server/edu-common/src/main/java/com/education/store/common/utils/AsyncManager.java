package com.education.store.common.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 异步任务管理器 - 用于异步保存日志等操作
 */
public class AsyncManager {

    private static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(4);

    private AsyncManager() {}

    public static void execute(Runnable task) {
        EXECUTOR.execute(task);
    }
}
