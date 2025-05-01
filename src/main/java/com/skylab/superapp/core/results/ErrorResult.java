package com.skylab.superapp.core.results;

import org.springframework.http.HttpStatus;

public class ErrorResult extends Result {

	public ErrorResult(String message, HttpStatus httpStatus) {
		super(false, message, httpStatus);
		
	}
	
	public ErrorResult(HttpStatus httpStatus) {
		super(false, httpStatus);
	}

}
