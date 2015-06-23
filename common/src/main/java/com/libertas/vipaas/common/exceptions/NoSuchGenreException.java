package com.libertas.vipaas.common.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.NOT_FOUND, reason="No such genre")  // 404
public class NoSuchGenreException extends Exception{
	public NoSuchGenreException(String string) {
		super(string);
	}
}
