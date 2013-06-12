package com.beckmanl.mmas.exceptions;

@SuppressWarnings("serial")
public class BadLoginException extends Exception {
	public BadLoginException(String message) {
		super(message);
	}
}
