package com.libertas.vipaas.common.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.NOT_FOUND, reason="No such rating")  // 404
public class NoSuchRatingException extends Exception{
	public NoSuchRatingException(String string) {
		super(string);
	}
}
