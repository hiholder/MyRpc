package com.hodur;

import com.hodur.annotation.RpcScan;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author Hodur
 * @className NettyClientMain.java
 * @description
 * @date 2021年04月15日 22:14
 */
@RpcScan(basePackage = {"com.hodur"})
public class NettyClientMain {
    public static void main(String[] args) throws InterruptedException {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(NettyClientMain.class);
        HelloController helloController = (HelloController) applicationContext.getBean("helloController");
        helloController.test();
    }
}
