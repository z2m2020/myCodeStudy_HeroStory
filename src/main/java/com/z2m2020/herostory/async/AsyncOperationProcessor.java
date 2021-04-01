package com.z2m2020.herostory.async;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 异步操作处理器
 *
 */
public class AsyncOperationProcessor {
    /**
     * singleton
     */
    static private final AsyncOperationProcessor _instance=new AsyncOperationProcessor();



    private final ExecutorService _es= Executors.newSingleThreadExecutor((newRunnable)->{
        Thread newThread=new Thread(newRunnable);
        newThread.setName("AsyncOperationProcessor");
        return newThread;
    });

    /**
     * 私有化构造对象
     */
    private AsyncOperationProcessor(){}

    /**
     * 获得单例对象
     * @return
     */
    public static AsyncOperationProcessor getInstance(){
        return _instance;
    }
    /**
     * 执行异步操作
     * @param r
     */
    public void process(Runnable r){
        if(null==r){
            return;

        }
        _es.submit(r);
    }

}
