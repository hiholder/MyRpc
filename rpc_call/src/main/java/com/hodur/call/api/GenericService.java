package com.hodur.call.api;

import com.hodur.common.exception.GenericException;

public interface GenericService {
    Object $invoke(String method, String[] parameterTypes, Object[] args) throws GenericException;
}
