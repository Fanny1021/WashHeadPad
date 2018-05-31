package com.fanny.washheadpad.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Created by Fanny on 18/4/18.
 */

public class ThreadPoolManager {
    /** 线程执行器 **/
    private static ExecutorService executorService = null;
    /** 固定5个线程 **/
    private static int nThreads = 5;
    /** 单例 **/
    private static ThreadPoolManager taskExecutorPool = null;
    /** 初始化线程池 **/
    static {
        taskExecutorPool = new ThreadPoolManager(nThreads * getNumCores());
    }
    /** 构造函数 **/
    private ThreadPoolManager(int threads) {
//executorService = Executors.newFixedThreadPool(threads);
        executorService = Executors.newScheduledThreadPool(threads);
    }
    /**
     * 取得单例
     *
     * @return
     */
    public static ThreadPoolManager getInstance() {
        return taskExecutorPool;
    }
    /**
     * 取得线程执行器
     *
     * @return
     */
    public ExecutorService getExecutorService() {
        return executorService;
    }
    /**
     * 取得周期性线程执行器
     * @return
     */
    public ScheduledExecutorService getScheduledExcutorService(){
        return (ScheduledExecutorService)executorService;
    }
    /**
     * 获得手机cup个数
     * @return
     */
    public static int getNumCores() {
        int threadCount = Runtime.getRuntime().availableProcessors();
        return threadCount;
    }
}
