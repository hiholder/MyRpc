package com.hodur.remoting.provider;

import com.hodur.URL;

/**
 * @author Hodur
 * @className ServiceProvider.java
 * @description 用来存储和提供服务类（ServiceHandler）
 * @date 2021年04月13日 20:04
 */
public interface ServiceProvider {
    /**
     * @describe    添加服务
     * @author Hodur
     * @date 2021/4/13 20:07
     * @param service 服务的对象
     * @param serviceClass 由服务对象进行实现的接口类
     * @param URL 服务类的相关属性
     */
    void addService(Object service, Class<?> serviceClass, URL URL);
    /**
     * @describe  根据服务名获取服务类，服务名存储在一个RpcServiceProperties对象中
     * @author Hodur
     * @date 2021/4/13 20:08
     * @param URL 服务名存储在里面
     * @return java.lang.Object
     */
    Object getService(URL URL);
    /**
     * @describe
     * @author Hodur
     * @date 2021/10/31 16:07
     * @param service 服务对象
     * @param URL 服务对象相关属性
     */
    void publishService(Object service, URL URL);
    void publishService(Object service);
}
