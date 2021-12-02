package com.hodur.remoting.dto;

import com.hodur.enums.RpcResponseCodeEnum;
import lombok.*;

import java.io.Serializable;

/**
 * @author Hodur
 * @className RpcResponse.java
 * @description RPC响应数据传输对象
 * @date 2021年04月13日 19:41
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class RpcResponse<T> implements Serializable {
    private static final long serialVersionUID = 715745410605631233L;
    private String requestId;
    private Integer code;//响应码，成功为200，失败为500
    private String message;//响应信息，成功和失败信息不同
    private T data;//响应体
    /**
     * @describe 传输成功
     * @author Hodur
     * @date 2021/7/21 10:02
     * @param data
     * @param requestId
     * @return com.hodur.remoting.dto.RpcResponse<T>
     */
    public static <T> RpcResponse<T> success(T data,String requestId) {
        RpcResponse<T> response = new RpcResponse<>();
        response.setCode(RpcResponseCodeEnum.SUCCESS.getCode());
        response.setMessage(RpcResponseCodeEnum.SUCCESS.getMessage());
        response.setRequestId(requestId);
        if (null != data) {
            response.setData(data);
        }
        return response;
    }

    public static <T> RpcResponse<T> fail(RpcResponseCodeEnum rpcResponseCodeEnum) {
        RpcResponse<T> response = new RpcResponse<>();
        response.setCode(rpcResponseCodeEnum.getCode());
        response.setMessage(rpcResponseCodeEnum.getMessage());
        return response;
    }
}
