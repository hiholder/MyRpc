package com.hodur.exception;

import com.hodur.enums.RpcErrorMessageEnum;

/**
 * @author Hodur
 * @className RpcException.java
 * @description
 * @date 2021年04月13日 18:32
 */
public class RpcException extends RuntimeException {
    public RpcException(RpcErrorMessageEnum rpcErrorMessageEnum,String detail) {
        super(rpcErrorMessageEnum.getMessage() + ":" + detail);
    }

    public RpcException(String message,Throwable cause) {
        super(message, cause);
    }

    public RpcException(RpcErrorMessageEnum rpcErrorMessageEnum) {
        super(rpcErrorMessageEnum.getMessage());
    }
}
