package com.kitchome.auth.Exception;

import com.kitchome.auth.util.ErrorCode;

public class BaseException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2320389357401020217L;

	private ErrorCode errCode;

	public BaseException(ErrorCode errCode) {
		this(errCode, errCode.getErrorCode());

	}

	public BaseException(ErrorCode errCode, String message) {
		super(message);
		this.errCode = errCode;
	}

	public BaseException(ErrorCode errCode, Throwable cause) {
		this(errCode, errCode.getErrorCode(), cause);

	}

	public BaseException(ErrorCode errCode, String message, Throwable cause) {
		super(message, cause);
		this.errCode = errCode;
	}

	public ErrorCode getErrCode() {
		return errCode;
	}

}
