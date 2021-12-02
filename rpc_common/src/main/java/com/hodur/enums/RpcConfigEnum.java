package com.hodur.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Hodur
 * @className RpcConfigEnum.java
 * @description
 * @date 2021年04月13日 18:24
 */
@AllArgsConstructor
@Getter
public enum RpcConfigEnum {
    RPC_CONFIG_PATH("rpc.properties"),
    ZK_ADDRESS("rpc.zookeeper.address");

    private final String propertyValue;
}
