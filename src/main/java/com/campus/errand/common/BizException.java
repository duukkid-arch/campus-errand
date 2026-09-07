package com.campus.errand.common;

/**
 * 业务异常。凡是可以预料、要提示给用户的错误都抛它，
 * 由 GlobalExceptionHandler 统一转成 Result.fail。
 */
public class BizException extends RuntimeException {
    public BizException(String message) {
        super(message);
    }
}
