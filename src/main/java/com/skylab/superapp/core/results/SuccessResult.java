package com.skylab.superapp.core.results;

import org.springframework.http.HttpStatus;

public class SuccessResult extends Result {

	public SuccessResult(String message, HttpStatus httpStatus, String path) {
		super(true, message, httpStatus, path);

	}

	public SuccessResult(HttpStatus httpStatus, String path) {
		super(true, httpStatus, path);
	}

}
