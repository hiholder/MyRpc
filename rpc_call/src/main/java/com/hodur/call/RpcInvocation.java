package com.hodur.call;



import com.hodur.call.api.Invocation;
import com.hodur.call.api.Invoker;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Hodur
 * @className RpcInvocation.java
 * @description
 * @date 2021年12月03日 22:37
 */
public class RpcInvocation implements Invocation, Serializable {

    private static final long serialVersionUID = -4355285085441097045L;

    /**
     * 方法名称
     */
    private String methodName;

    /**
     * 参数类型集合
     */
    private Class<?>[] parameterTypes;

    /**
     * 参数集合
     */
    private Object[] arguments;

    /**
     * 附加值
     */
    private Map<String, String> attachments;

    /**
     * 实体域
     */
    private transient Invoker<?> invoker;

    public RpcInvocation() {
    }

    public RpcInvocation(String methodName, Class<?>[] parameterTypes, Object[] arguments, Map<String, String> attachments, Invoker<?> invoker) {
        this.methodName = methodName;
        this.parameterTypes = parameterTypes == null ? new Class<?>[0] : parameterTypes;
        this.arguments = arguments == null ? new Object[0] : arguments;
        this.attachments = attachments == null ? new HashMap<String, String>() : attachments;
        this.invoker = invoker;
    }

    public RpcInvocation(Method method, Object[] arguments) {
        this(method.getName(), method.getParameterTypes(), arguments, null, null);
    }

    @Override
    public String getMethodName() {
        return methodName;
    }

    @Override
    public Class<?>[] getParameterTypes() {
        return parameterTypes;
    }

    @Override
    public Object[] getArguments() {
        return arguments;
    }

    @Override
    public Invoker<?> getInvoker() {
        return invoker;
    }
}
