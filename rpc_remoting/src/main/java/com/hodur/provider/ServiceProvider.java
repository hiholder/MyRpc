package com.hodur.provider;

import com.hodur.URL;

/**
 * @author Hodur
 * @className ServiceProvider.java
 * @description
 * @date 2021年04月14日 19:09
 */
public interface ServiceProvider {
    /**
     * @describe
     * @author Hodur
     * @date 2021/4/14 19:13
     * @param service 服务器对象
     * @param serviceClass 由服务器实例对象实现的接口类
     * @param URL 服务器相关属性
     */
    void addService(Object service, Class<?> serviceClass, URL URL);
    /**
     * @describe
     * @author Hodur
     * @date 2021/4/14 19:18
     * @param URL 服务器属性
     * @return java.lang.Object
     */
    Object getService(URL URL);
    /**
     * @describe
     * @author Hodur
     * @date 2021/4/14 19:21
     * @param service 服务器对象
     * @param URL 服务器相关的属性
     */
    void publishService(Object service, URL URL);
    /**
     * @describe
     * @author Hodur
     * @date 2021/4/14 19:24
     * @param service 服务器对象
     */
    void publishService(Object service);
}
