package com.libertas.vipaas.common.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.NOT_FOUND, reason="No such user")  // 404
public class NoMediaFoundException extends Exception{
	public NoMediaFoundException(String string) {
		super(string);
	}
}
