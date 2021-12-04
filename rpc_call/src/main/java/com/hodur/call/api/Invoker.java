package com.hodur.call.api;


import com.hodur.common.Node;
import com.hodur.common.exception.RpcException;
/**
 * @author Hodur
 * @className Invoker.java
 * @description 动态代理类
 * @date 2021年04月13日 22:29
 */
public interface Invoker<T> extends Node {
    /**
     * @describe 获得服务接口
     * @author Hodur
     * @date 2021/12/3 20:06
     * @return java.lang.Class<T>
     */
    Class<T> getInterface();

    Object invoke(Invocation invocation) throws RpcException;

}
