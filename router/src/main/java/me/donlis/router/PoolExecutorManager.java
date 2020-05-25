package me.donlis.router;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public final class PoolExecutorManager {

    //cpu核心数
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();

    private static final long THREAD_TIME = 30L;

    public static ThreadPoolExecutor newExecutor(int corePoolSize){
        if(corePoolSize <= 0){
            corePoolSize = CPU_COUNT;
        }else{
            corePoolSize = Math.min(corePoolSize,CPU_COUNT);
        }
        ThreadPoolExecutor executor = new ThreadPoolExecutor(corePoolSize, corePoolSize, THREAD_TIME, TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(64));
        executor.allowCoreThreadTimeOut(true);
        return executor;
    }

}
