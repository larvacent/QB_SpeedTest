package com.quickbird.speedtest.exception;


public class NoSdcardException extends CommonException{

	private static final long serialVersionUID = 1L;

	public NoSdcardException() {
	}

	public NoSdcardException(String msg) {
		super(msg);
	}
}