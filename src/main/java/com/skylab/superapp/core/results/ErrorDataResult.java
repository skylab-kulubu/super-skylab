package com.skylab.superapp.core.results;

import org.springframework.http.HttpStatus;

public class ErrorDataResult<T> extends DataResult<T> {

	public ErrorDataResult(T data, String message, HttpStatus httpStatus, String path) {
		super(data, false, message, httpStatus, path);

	}

	public ErrorDataResult(T data, HttpStatus httpStatus, String path) {
		super(data, false, httpStatus, path);
	}

	public ErrorDataResult(String message, HttpStatus httpStatus, String path) {
		super(null, false, message, httpStatus, path);
	}

	public ErrorDataResult(HttpStatus httpStatus, String path) {
		super(null, false, httpStatus, path);
	}

	
}
