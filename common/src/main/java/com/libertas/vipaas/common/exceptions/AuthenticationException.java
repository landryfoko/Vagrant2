package com.libertas.vipaas.common.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.FORBIDDEN, reason="Could not authenticate user")  // 404
public class AuthenticationException extends Exception{
	public AuthenticationException(String string) {
		super(string);
	}
}
