package com.lb.pingme.common.exception;

import com.lb.pingme.common.enums.APIErrorCommonEnum;

public class AuthException extends RuntimeException {

    private int code = APIErrorCommonEnum.UN_AUTH.getCode();

    public AuthException() {
        super();
    }

    public AuthException(String s) {
        super(s);
    }

    public AuthException(int code, String s) {
        super(s);
        this.code = code;
    }

    public AuthException(String message, Throwable cause) {
        super(message, cause);
    }

    public AuthException(Throwable cause) {
        super(cause);
    }

    public int getCode() {
        return code;
    }
}
