package com.kitchome.auth.Exception;

import com.kitchome.auth.util.ErrorCode;

public class AuthException extends BaseException{


    public AuthException(ErrorCode errorCode,Throwable cause) {
        super(errorCode,cause);
    }
    public AuthException(ErrorCode errorCode,String message, Throwable cause) {
        super(errorCode,message,cause);
    }
    public AuthException(ErrorCode errorCode) {
        super(errorCode);
    }


    public AuthException(ErrorCode errorCode, String message) {
        super(errorCode,message);
    }
}
