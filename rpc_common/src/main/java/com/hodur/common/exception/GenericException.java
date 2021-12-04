package com.hodur.common.exception;

import com.hodur.common.utils.StringUtils;

/**
 * @author Hodur
 * @className GenericException.java
 * @description
 * @date 2021年12月03日 22:59
 */
public class GenericException extends RuntimeException{
    private static final long serialVersionUID = -1182299763306599962L;

    /**
     * 异常类名
     */
    private String exceptionClass;

    /**
     * 异常信息
     */
    private String exceptionMessage;

    public GenericException() {
    }

    public GenericException(String exceptionClass, String exceptionMessage) {
        super(exceptionMessage);
        this.exceptionClass = exceptionClass;
        this.exceptionMessage = exceptionMessage;
    }

    public GenericException(Throwable cause) {
        super(StringUtils.toString(cause));
        this.exceptionClass = cause.getClass().getName();
        this.exceptionMessage = cause.getMessage();
    }

    public String getExceptionClass() {
        return exceptionClass;
    }

    public void setExceptionClass(String exceptionClass) {
        this.exceptionClass = exceptionClass;
    }

    public String getExceptionMessage() {
        return exceptionMessage;
    }

    public void setExceptionMessage(String exceptionMessage) {
        this.exceptionMessage = exceptionMessage;
    }
}
