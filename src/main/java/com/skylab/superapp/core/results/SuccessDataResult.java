package com.skylab.superapp.core.results;

import org.springframework.http.HttpStatus;

public class SuccessDataResult<T> extends DataResult<T> {

	public SuccessDataResult(T data, String message, HttpStatus httpStatus, String path) {
		super(data, true, message, httpStatus, path);
	}

	public SuccessDataResult(T data, HttpStatus httpStatus, String path) {
		super(data, true, httpStatus, path);
	}

	public SuccessDataResult(String message, HttpStatus httpStatus, String path) {
		super(null, true, message, httpStatus, path);
	}

	public SuccessDataResult(HttpStatus httpStatus, String path) {
		super(null, true,httpStatus, path);
	}

}
