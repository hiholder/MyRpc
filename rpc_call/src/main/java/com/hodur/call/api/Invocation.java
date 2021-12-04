package com.hodur.call.api;

import java.util.Map;

/**
 * @author Hodur
 * @className Invocation.java
 * @description 动态代理类
 * @date 2021年04月13日 22:29
 */
public interface Invocation {
    /**
     * @describe 获得方法名称
     * @author Hodur
     * @date 2021/12/3 20:13
     * @return java.lang.String
     */
    String getMethodName();
    /**
     * @describe 获得参数类型
     * @author Hodur
     * @date 2021/12/3 20:14
     * @return java.lang.Class<?>[]
     */
    Class<?>[] getParameterTypes();
    /**
     * @describe 获得参数
     * @author Hodur
     * @date 2021/12/3 20:14
     * @return java.lang.Object[]
     */
    Object[] getArguments();

    /**
     * get attachments.
     * 获得附加值集合
     * @return attachments.
     * @serial
     */
    Map<String, String> getAttachments();

    /**
     * get attachment by key.
     * 获得附加值
     * @return attachment value.
     * @serial
     */
    String getAttachment(String key);

    /**
     * get attachment by key with default value.
     * 获得附加值
     * @return attachment value.
     * @serial
     */
    String getAttachment(String key, String defaultValue);
    /**
     * @describe 获得上下文的invoker
     * @author Hodur
     * @date 2021/12/3 20:14
     * @return com.hodur.call.api.Invoker<?>
     */
    Invoker<?> getInvoker();


}
