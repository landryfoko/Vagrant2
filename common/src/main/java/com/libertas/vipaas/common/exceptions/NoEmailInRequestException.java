package com.libertas.vipaas.common.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.BAD_REQUEST, reason="No email in request")  // 404
public class NoEmailInRequestException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public NoEmailInRequestException(String string) {
		super(string);
	}


}
