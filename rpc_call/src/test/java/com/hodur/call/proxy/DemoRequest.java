package com.hodur.call.proxy;

import java.io.Serializable;

/**
 * @author Hodur
 * @className DemoRequest.java
 * @description
 * @date 2021年12月03日 20:22
 */
public class DemoRequest implements Serializable {
    private static final long serialVersionUID = -2579095288792344869L;

    private String mServiceName;

    private String mMethodName;

    private Class<?>[] mParameterTypes;

    private Object[] mArguments;

    public DemoRequest(String serviceName, String methodName, Class<?>[] parameterTypes, Object[] args) {
        mServiceName = serviceName;
        mMethodName = methodName;
        mParameterTypes = parameterTypes;
        mArguments = args;
    }

    public String getServiceName() {
        return mServiceName;
    }

    public String getMethodName() {
        return mMethodName;
    }

    public Class<?>[] getParameterTypes() {
        return mParameterTypes;
    }

    public Object[] getArguments() {
        return mArguments;
    }
}
