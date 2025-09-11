package com.skylab.superapp.core.results;

import org.springframework.http.HttpStatus;

public class ErrorResult extends Result {

	private String errorCode;

	public ErrorResult(String message, ErrorCode errorCode, HttpStatus httpStatus) {
		super(false, message, httpStatus);
		this.errorCode = errorCode.name();
	}

	public ErrorResult(String message, HttpStatus httpStatus) {
		super(false, message, httpStatus);
	}

	public ErrorResult(HttpStatus httpStatus) {
		super(false, httpStatus);
	}
}
