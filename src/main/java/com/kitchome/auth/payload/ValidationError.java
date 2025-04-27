package com.kitchome.auth.payload;

import java.util.Map;

import com.kitchome.auth.util.ErrorCode;

public class ValidationError {
	private Map<String,String> errorparams;
	private String errorMessage;
	private ErrorCode errCode;
	public ValidationError() {
		super();
	}
	public ValidationError(Map<String, String> errorparams, ErrorCode errCode, String errorMessage) {
		super();
		this.errorparams = errorparams;
		this.errorMessage = errorMessage;
		this.errCode = errCode;
	}
	public ValidationError(Map<String, String> errorparams,  ErrorCode errCode) {
		this(errorparams,errCode,null);
		
	}
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	public Map<String, String> getErrorparams() {
		return errorparams;
	}
	public ErrorCode getErrCode() {
		return errCode;
	}
	
	
	

}
