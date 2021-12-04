package com.hodur.serviceimpl;

import com.hodur.Hello;
import com.hodur.HelloService;
import com.hodur.remote.annotation.RpcService;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Hodur
 * @className HelloServiceImpl.java
 * @description
 * @date 2021年04月15日 21:56
 */
@Slf4j
@RpcService(group = "test1",version = "version1")
public class HelloServiceImpl implements HelloService {
    static {
        System.out.println("HelloServiceImpl被创建");
    }

    @Override
    public String hello(Hello hello) {
        log.info("HelloServiceImpl收到：{}.",hello.getMessage());
        String result = "Hello description is " + hello.getDescription();
        log.info("HelloServiceImpl返回：{}.",result);
        return result;
    }
}
