package com.hodur.call.proxy;

public interface DemoService {
    void sayHello(String name);

    String echo(String text);

    long timestamp();

    String getThreadName();

    int getSize(String[] strs);

    int getSize(Object[] os);

    Object invoke(String service, String method) throws Exception;

    int stringLength(String str);

    Type enumlength(Type... types);
}
