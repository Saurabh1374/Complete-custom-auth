package com.kitchome.auth.util;

public enum ErrorCode {
	VALIDATION_ERROR("E-11000"),
	USER_ALREADY_AVAILABLE("VE-10000");
	
	private String ErrorCode;

	ErrorCode(String errorCode) {
		ErrorCode = errorCode;
	}
	public String getErrorCode() {
		return this.ErrorCode;
	}
}
