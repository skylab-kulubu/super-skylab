package com.skylab.superapp.core.results;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;

public class Result {
	private boolean success;
	private String message;
	private HttpStatus httpStatus;
	private String path;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private final LocalDateTime timeStamp = LocalDateTime.now();
	
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

	public LocalDateTime getTimeStamp() {
		return timeStamp;
	}

	public Result(boolean success, String message, HttpStatus httpStatus) {
		this.success = success;
		this.message = message;
		this.httpStatus = httpStatus;
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
		this.path = request.getRequestURI();
	}

	public Result(boolean success, HttpStatus httpStatus) {
		this.success = success;
		this.httpStatus = httpStatus;
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
		this.path = request.getRequestURI();
	}
}
