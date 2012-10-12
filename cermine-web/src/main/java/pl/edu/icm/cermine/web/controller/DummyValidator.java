package pl.edu.icm.cermine.web.controller;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class DummyValidator implements Validator {

	@Override
	public boolean supports(Class<?> clazz) {
		return false;
	}

	@Override
	public void validate(Object target, Errors errors) {		
	}

}
