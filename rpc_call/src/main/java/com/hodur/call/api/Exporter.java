package com.hodur.call.api;

public interface Exporter<T> {
    /**
     * @describe 获得对应的实体域invoker
     * @author Hodur
     * @date 2021/12/3 21:42
     * @return com.hodur.call.api.Invoker<T>
     */
    Invoker<T> getInvoker();
    /**
     * @describe 取消暴露
     * @author Hodur
     * @date 2021/12/3 21:43
     */
    void unexport();
}
