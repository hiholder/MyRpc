package com.hodur.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * @author Hodur
 * @className RpcResponseCodeEnum.java
 * @description
 * @date 2021年04月13日 18:24
 */
@AllArgsConstructor
@Getter
@ToString
public enum RpcResponseCodeEnum {
    SUCCESS(200, "The remote call is successful"),
    FAIL(500, "The remote call is fail");
    private final int code;

    private final String message;
}
