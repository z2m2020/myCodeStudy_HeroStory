package com.z2m2020.herostory.async;

import com.z2m2020.herostory.MainMsgProcessor;

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

    private final ExecutorService[] _esArray =new ExecutorService[8];

    private final ExecutorService _es= Executors.newSingleThreadExecutor((newRunnable)->{
        Thread newThread=new Thread(newRunnable);
        newThread.setName("AsyncOperationProcessor");
        return newThread;
    });

    /**
     * 私有化构造对象
     */
    private AsyncOperationProcessor(){
        for(int i=0;i<_esArray.length;i++){
            final String threadName="AsyncOperationProcessor["+i+"]";
            _esArray[i]=Executors.newSingleThreadExecutor((r)->{
                Thread t=new Thread(r);
                t.setName(threadName);
                return t;
            });
        }
    }

    /**
     * 获得单例对象
     * @return
     */
    public static AsyncOperationProcessor getInstance(){
        return _instance;
    }
    /**
     * 执行异步操作
     * @param op
     */
    public void process(IAsyncOperation op){
        if(null==op){
            return;

        }

        int bindId=Math.abs(op.getBindId());
        int esIndex=bindId%_esArray.length;


        _esArray[esIndex].submit(()->{
            //执行异步操作
            op.doAsync();
            //回到主线程,执行完成逻辑
            MainMsgProcessor.getInstance().process(op::doFinish);
//            MainMsgProcessor.getInstance().process(()->op.doFinish());
        });
    }

}
