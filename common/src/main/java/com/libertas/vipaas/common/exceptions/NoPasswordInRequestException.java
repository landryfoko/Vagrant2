package com.libertas.vipaas.common.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.BAD_REQUEST, reason="No password in request")  // 404
public class NoPasswordInRequestException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public NoPasswordInRequestException(String string) {
		super(string);
	}
}
