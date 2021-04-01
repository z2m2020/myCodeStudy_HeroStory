package com.z2m2020.herostory.async;

public interface IAsyncOperation {
    /**
     * 执行异步操作
     */
    void doAsync();
    /**
     * 执行完整逻辑
     */
    default void doFinish(){

    };
}
