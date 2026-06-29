package com.edu.common.core.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * Redis分布式锁工具
 * 用于高并发场景: 排课冲突、报名占位、消课扣减
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RedisDistributedLock {

    private final RedissonClient redissonClient;

    private static final long DEFAULT_WAIT_TIME = 3;
    private static final long DEFAULT_LEASE_TIME = 10;

    /**
     * 获取锁并执行业务
     */
    public <T> T executeWithLock(String lockKey, Supplier<T> supplier) {
        return executeWithLock(lockKey, DEFAULT_WAIT_TIME, DEFAULT_LEASE_TIME, supplier);
    }

    public <T> T executeWithLock(String lockKey, long waitTime, long leaseTime, Supplier<T> supplier) {
        RLock lock = redissonClient.getLock(lockKey);
        try {
            if (lock.tryLock(waitTime, leaseTime, TimeUnit.SECONDS)) {
                try {
                    return supplier.get();
                } finally {
                    lock.unlock();
                }
            } else {
                throw new RuntimeException("系统繁忙，请稍后重试");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("操作被中断");
        }
    }

    /**
     * 获取锁并执行无返回值业务
     */
    public void executeWithLock(String lockKey, Runnable runnable) {
        executeWithLock(lockKey, () -> { runnable.run(); return null; });
    }

    /**
     * 生成业务锁Key
     */
    public static String scheduleConflictLock(Long teacherId, String date) {
        return "lock:schedule:teacher:" + teacherId + ":" + date;
    }

    public static String classroomLock(Long classroomId, String date) {
        return "lock:classroom:" + classroomId + ":" + date;
    }

    public static String enrollmentLock(Long classId) {
        return "lock:enrollment:class:" + classId;
    }

    public static String consumeLock(Long studentId, Long classId) {
        return "lock:consume:student:" + studentId + ":class:" + classId;
    }

    public static String studentNoSeqLock() {
        return "lock:seq:student_no";
    }

    public static String orderNoSeqLock() {
        return "lock:seq:order_no";
    }
}
