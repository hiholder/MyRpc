package com.hodur.call.proxy;

import java.rmi.RemoteException;

/**
 * @author Hodur
 * @className RemoteServiceImpl.java
 * @description
 * @date 2021年12月03日 20:19
 */
public class RemoteServiceImpl implements RemoteService{
    @Override
    public String sayHello(String name) throws RemoteException {
        return "hello " + name + "@" + RemoteServiceImpl.class.getName();
    }

    @Override
    public String getThreadName() throws RemoteException {
        //System.out.println("RpcContext.getContext().getRemoteHost()=" + RpcContext.getContext().getRemoteHost());
        return Thread.currentThread().getName();
    }
}
