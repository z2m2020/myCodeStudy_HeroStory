package com.z2m2020.herostory.async;

public interface IAsyncOperation {
    /**
     * 获取绑定id
     * @return 角色id
     */
    default int getBindId(){
        return 0;
    }
    /**
     * 执行异步操作
     */
    void doAsync();
    /**
     * 执行完整逻辑
     */
    default void doFinish(){

    }
}
