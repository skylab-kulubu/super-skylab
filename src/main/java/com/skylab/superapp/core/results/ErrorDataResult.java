package com.skylab.superapp.core.results;

import org.springframework.http.HttpStatus;

public class ErrorDataResult<T> extends DataResult<T> {

	private String errorCode;

	public ErrorDataResult(T data, String message, ErrorCode errorCode, HttpStatus httpStatus) {
		super(data, false, message, httpStatus);
		this.errorCode = errorCode.name();
	}

	public ErrorDataResult(String message, ErrorCode errorCode, HttpStatus httpStatus) {
		super(null, false, message, httpStatus);
		this.errorCode = errorCode.name();
	}

	public ErrorDataResult(T data, String message, HttpStatus httpStatus) {
		super(data, false, message, httpStatus);
	}

	public ErrorDataResult(String message, HttpStatus httpStatus) {
		super(null, false, message, httpStatus);
	}

	public ErrorDataResult(T data, HttpStatus httpStatus) {
		super(data, false, httpStatus);
	}

	public ErrorDataResult(HttpStatus httpStatus) {
		super(null, false, httpStatus);
	}
}