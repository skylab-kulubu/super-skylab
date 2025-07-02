package com.skylab.superapp.core.results;

import org.springframework.http.HttpStatus;

public class Result {
	private boolean success;
	private String message;
	private HttpStatus httpStatus;
	private String path;
	
	public boolean isSuccess() {
		return success;
	}
	
	public String getMessage() {
		return message;
	}

	public HttpStatus getHttpStatus(){
		return httpStatus;
	}

	public String getPath() {
		return path;
	}
	
	public Result(boolean success, String message, HttpStatus httpStatus, String path) {
		this.success = success;
		this.message = message;
		this.httpStatus = httpStatus;
		this.path = path;
	}
	
	public Result(boolean success, HttpStatus httpStatus, String path) {
		this.success = success;
		this.httpStatus = httpStatus;
		this.path = path;
	}
}
