package com.hodur.spring;

import com.hodur.annotation.RpcReference;
import com.hodur.annotation.RpcService;
import com.hodur.URL;
import com.hodur.extension.ExtensionLoader;
import com.hodur.factory.SingletonFactory;
import com.hodur.provider.ServiceProvider;
import com.hodur.provider.ServiceProviderImpl;
import com.hodur.proxy.RpcClientProxy;
import com.hodur.remoting.transport.RpcRequestTransport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

/**
 * @author Hodur
 * @className SpringBeanPostProcessor.java
 * @description 在常见新对象前调用该方法，用来判断该对象是否使用了注解
 * @date 2021年04月15日 17:20
 */
@Slf4j
@Component
public class SpringBeanPostProcessor implements BeanPostProcessor {
    private final ServiceProvider serviceProvider;
    private final RpcRequestTransport rpcClient;

    public SpringBeanPostProcessor() {
        this.serviceProvider = SingletonFactory.getInstance(ServiceProviderImpl.class);
        this.rpcClient = ExtensionLoader.getExtensionLoader(RpcRequestTransport.class).getExtension("netty");
    }

    public Object postProcessBeforeInitialization(Object bean,String beanName) {
        if (bean.getClass().isAnnotationPresent(RpcService.class)) {
            log.info("[{}] is annotated with [{}]",bean.getClass().getName(),RpcService.class.getCanonicalName());
            //获得RpcService注解
            RpcService rpcService = bean.getClass().getAnnotation(RpcService.class);
            //构建RpcServiceProperties
            URL url = new URL(rpcService.group(), rpcService.version());
                    //URL.builder().group(rpcService.group()).version(rpcService.version()).build();
            serviceProvider.publishService(bean, url);
        }
        return bean;
    }

    public Object postProcessAfterInitialization(Object bean,String beanName) {
        Class<?> targetClass = bean.getClass();
        Field[] declaredFields = targetClass.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            RpcReference rpcReference = declaredField.getAnnotation(RpcReference.class);
            if (rpcReference != null) {
                URL url = new URL(rpcReference.group(), rpcReference.version());
                        /*URL.builder()
                        .group(rpcReference.group()).version(rpcReference.version()).build();*/
                RpcClientProxy rpcClientProxy = new RpcClientProxy(url, rpcClient);
                Object clientProxy = rpcClientProxy.getProxy(declaredField.getType());
                declaredField.setAccessible(true);
                try {
                    declaredField.set(bean, clientProxy);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return bean;
    }
}
