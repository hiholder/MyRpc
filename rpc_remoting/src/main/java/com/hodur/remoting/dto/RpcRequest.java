package com.hodur.remoting.dto;

import com.hodur.URL;
import lombok.*;

import java.io.Serializable;

/**
 * @author Hodur
 * @className RpcRequest.java
 * @description RpcRequest数据传输对象
 * @date 2021年04月13日 19:36
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
@Builder
public class RpcRequest implements Serializable {
    private static final long serialVersionUID = 1905122041950251207L;
    private String requestId;
    private String interfaceName;
    private String methodName;
    private Object[] parameters;
    private Class<?>[] paramTypes;
    private String version;
    private String group;
    /**
     * @describe 通过服务名，版本号和组号构建一个RpcServiceProperties
     * @author Hodur
     * @date 2021/7/21 10:20
     * @return com.hodur.entity.RpcServiceProperties
     */
    public URL toRpcProperties() {
        /*return URL.builder().serviceName(this.getInterfaceName())
                .version(this.getVersion())
                .group(this.getGroup()).build();*/
        return new URL(this.group, this.version);
    }
}
