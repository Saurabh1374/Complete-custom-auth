package com.kitchome.auth.Exception;

import java.util.Locale;
import java.util.Map;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import com.kitchome.auth.payload.ValidationError;

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

}
