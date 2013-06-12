package com.beckmanl.mmas.exceptions;

@SuppressWarnings("serial")
public class BadOrderException extends Exception {
	public BadOrderException(String message) {
		super(message);
	}
}