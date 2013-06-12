package com.beckmanl.mmas.exceptions;

@SuppressWarnings("serial")
public class UndefinedConfigException extends Exception {
	public UndefinedConfigException(String message) {
		super(message);
	}
}