package com.quickbird.speedtest.exception;

/**
 * 对抛出的异常进行封装
 * 
 * @author WuzuWeng
 * 
 */

public class CommonException extends Exception {

	private static final long serialVersionUID = 1L;

	public CommonException() {
	}

	public CommonException(String msg) {
		super(msg);
	}
}
