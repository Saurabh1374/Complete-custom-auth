package com.kitchome.auth.Exception;

import java.io.IOException;
import java.util.Locale;
import java.util.Map;

import com.kitchome.auth.payload.response.ErrorResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.header.Header;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import com.kitchome.auth.payload.ValidationError;

import javax.naming.AuthenticationException;
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
	
	private final MessageSource messageSource;

	public GlobalExceptionHandler(MessageSource messageSource) {
		super();
		this.messageSource = messageSource;
	}
	public String getMessage(String id,Object[] e) {
		Locale locale=LocaleContextHolder.getLocale();
		return messageSource.getMessage(id,e,locale);
	}
	@ExceptionHandler(ValidationException.class)
	public ModelAndView handleUserAlreadyExists(ValidationException ex) {
		StringBuilder sb=new StringBuilder("");
		if(!ex.getError().isEmpty()) {
			for(ValidationError err: ex.getError()) {
				Map<String,String> param=err.getErrorparams();
				err.setErrorMessage(this.getMessage(err.getErrCode().name(),param.values().toArray() ));
			}
		}
		 ModelAndView mav = new ModelAndView("register");
		    mav.addObject("errorMessage", ex.getMessage());
		    return mav;
	}
	@ExceptionHandler(AuthenticationException.class)
	public ResponseEntity<AuthenticationException> authenticationExceptionHandler(HttpServletResponse response, AuthenticationException ex) throws IOException {
		//response.getWriter().write(ex.getMessage());
		return new ResponseEntity<>(new HttpHeaders(),HttpStatus.UNAUTHORIZED);
	}
	@ExceptionHandler(AuthException.class)
	public ResponseEntity<?> authExceptionHandler(AuthException ae){
		log.info("caleed");
		return new ResponseEntity<>(ErrorResponse.of(ae.getErrCode(),ae.getMessage()),ae.getErrCode().getHttpStatus());
	}
	@ExceptionHandler(Exception.class)
	public ResponseEntity<?> exceptionHandler(Exception ex){
		log.info("called");
		return new ResponseEntity<>(ex.getMessage(),HttpStatus.BAD_REQUEST);
	}

}
