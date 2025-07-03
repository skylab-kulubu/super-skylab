package com.skylab.superapp.core.results;

import org.springframework.http.HttpStatus;

public class ErrorResult extends Result {

	private String errorCode;

	public ErrorResult(String message, ErrorCode errorCode, HttpStatus httpStatus, String path) {
		super(false, message, httpStatus, path);
		this.errorCode = errorCode.name();
	}

	public ErrorResult(String message, HttpStatus httpStatus, String path) {
		super(false, message, httpStatus, path);
	}

	public ErrorResult(HttpStatus httpStatus, String path) {
		super(false, httpStatus, path);
	}
}
