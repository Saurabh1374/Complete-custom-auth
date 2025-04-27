package com.kitchome.auth.Exception;
import java.util.List;
import com.kitchome.auth.payload.ValidationError;
import com.kitchome.auth.util.ErrorCode;

public class ValidationException extends BaseException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 664027568357928077L;
	
	private List<ValidationError> error;
	private String message;

	public ValidationException(List<ValidationError> error) {
		super(ErrorCode.VALIDATION_ERROR);
		this.error = error;
	}
	public ValidationException(List<ValidationError> error, String message) {
		super(ErrorCode.VALIDATION_ERROR,message);
		this.error = error;
	}
	
	public List<ValidationError> getError() {
		return error;
	}
}
