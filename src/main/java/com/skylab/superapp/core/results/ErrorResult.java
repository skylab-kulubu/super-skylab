package com.skylab.superapp.core.results;

import org.springframework.http.HttpStatus;

public class ErrorResult extends Result {

	public ErrorResult(String message, HttpStatus httpStatus, String path) {
		super(false, message, httpStatus, path);

	}

	public ErrorResult(HttpStatus httpStatus, String path) {
		super(false, httpStatus, path);
	}

}
